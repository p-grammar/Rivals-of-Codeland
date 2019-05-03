package cnge.graphics;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL11C.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

import cnge.core.CNGE;
import cnge.graphics.sound.ALManagement;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class Window extends CNGE {

	public static final int DEFAULT_INIT = -1;

	/*
	 * inits
	 */
	private boolean resizable = true;
	private boolean full = false;
	private boolean decorated = true;

	private int initX = DEFAULT_INIT;
	private int initY = DEFAULT_INIT;
	private int initWidth = DEFAULT_INIT;
	private int initHeight = DEFAULT_INIT;

	private String iconPath = null;
	private String name = "oCNGE 5";
	private boolean vSync = true;

    private long cursorID;

	private int x;
	private int y;
	private int width;
	private int height;

	private long window;
	private GLFWVidMode vidMode;
	private long monitor;

	private boolean resizing;

	//region inits

	public Window initResizable(boolean r) {
		resizable = r;
		return this;
	}

	public Window initFull(boolean f) {
		full = f;
		return this;
	}

	public Window initDecorated(boolean d) {
		decorated = d;
		return this;
	}

	public Window initDims(int w, int h) {
		initWidth = w;
		initHeight = h;
		return this;
	}

	public Window initPosition(int x, int y) {
		initX = x;
		initY = y;
		return this;
	}

	public Window initName(String n) {
		name = n;
		return this;
	}

	public Window initVsync(boolean v) {
		vSync = v;
		return this;
	}

	public Window initIcon(String path) {
        iconPath = path;
        return this;
    }

	//endregion

	public Window init() {
		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

		if(!glfwInit()) {
			System.err.println("GLFW Failed to Initialize");
			System.exit(-1);
		}

		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);
		glfwWindowHint(GLFW_DECORATED, decorated ? GLFW_TRUE : GLFW_FALSE);

		vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		calculate();
		calcFramerate();

		window = glfwCreateWindow(1, 1, name, 0, 0);

		reWindow();

		threadContextualize();
		threadCreateCapabilties();

		glfwSwapInterval(vSync ? 1 : 0);

		glfwSetWindowSizeCallback(window,
			(long window, int ww, int hh) -> {
				width = ww;
				height = hh;
				resizing = true;
			}
		);

		if(iconPath != null) {
		    setIcon(iconPath);
        }

		glEnable(GL30.GL_CLIP_DISTANCE0);
		glEnable(GL_TEXTURE_2D);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		resizing = false;

		return this;
	}

	public boolean getResizing() {
		return resizing;
	}

	public void resetResizing() {
		resizing = false;
	}

	public void threadUnContextualize() {
		glfwMakeContextCurrent(0L);
	}

	public void threadContextualize() {
		glfwMakeContextCurrent(window);
	}

	public void threadCreateCapabilties() {
		createCapabilities();
	}

	public void calcFramerate() {
		framerate = (framerate == -1) ? vidMode.refreshRate() + 10 : framerate;
	}

	public void calcMonitor() {
		monitor = full ? glfwGetPrimaryMonitor() : 0;
	}

	public void calcDims() {
		width = full ?  vidMode.width() :  vidMode.width() / 2;
		height = full ? vidMode.height() : vidMode.height() / 2;
	}

	public void calcPosition() {
		x = full ? 0 : vidMode.width() / 4;
		y = full ? 0 : vidMode.height() / 4;
	}

	public void calculate() {
		calcMonitor();
		calcDims();
		calcPosition();
	}

	public void reWindow() {
		glfwSetWindowMonitor(window, monitor, x, y, width, height, framerate);
	}

	public void setFull(boolean f) {
		full = f;
	}

    public void resetCursor() {
        glfwSetCursorPos(window, vidMode.width(), vidMode.height());
    }

	public void showCursor() {
		glfwSetCursor(window, cursorID);
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}
	
	public void hideCursor() {
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
	}
	
	public void setIcon(String imagePath) {
		
		GLFWImage image = Window.makeGLFWImage(imagePath);   
		GLFWImage.Buffer buffer = GLFWImage.malloc(1);
		buffer.put(0, image);
		
		glfwSetWindowIcon(window, buffer);
	}
	
	public static GLFWImage makeGLFWImage(String imagePath) {
		BufferedImage b = null;
		try {
			b = ImageIO.read(new File(imagePath));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		int bwi = b.getWidth();
		int bhi = b.getHeight();
		int len = bwi * bhi;
		
		int[] rgbArray = new int[len];
		
		System.out.println();
		
		b.getRGB(0, 0, bwi, bhi, rgbArray, 0, bwi);
		
		ByteBuffer buffer = BufferUtils.createByteBuffer(len * 4);
		
		for(int i = 0; i < len; ++i) {
			int rgb = rgbArray[i];
			buffer.put((byte)(rgb >> 16 & 0xff));
			buffer.put((byte)(rgb >>  8 & 0xff));
			buffer.put((byte)(rgb       & 0xff));
			buffer.put((byte)(rgb >> 24 & 0xff));
		}
		
		buffer.flip();
			
	    // create vao GLFWImage
	    GLFWImage img = GLFWImage.create();
	    img.width(bwi);     // setup the images' width
	    img.height(bhi);   // setup the images' height
	    img.pixels(buffer);   // pass image data
	    
	    return img;
	}
	
	/**
	 * saves vao bufferedimage of the current loadRender buffer
	 * performance = bad
	 */
	public void takeScreenShot(int scw, int sch) {
		ByteBuffer buffer = BufferUtils.createByteBuffer(scw * sch * 4);
		GL11.glReadPixels(0, 0, scw, sch, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		
		int num = 0;
		File file;
		do {
			file = new File("screenshot" + num + ".png"); // The file to save to.
			++num;
		} while(file.exists());
		String format = "PNG"; // Example: "PNG" or "JPG"
		BufferedImage image = new BufferedImage(scw, sch, BufferedImage.TYPE_INT_RGB);
		   
		for(int x = 0; x < scw; x++) 
		{
		    for(int y = 0; y < sch; y++)
		    {
		        int i = (x + (scw * y)) * 4;
		        int red = buffer.get(i) & 0xFF;
		        int green = buffer.get(i + 1) & 0xFF;
		        int blue = buffer.get(i + 2) & 0xFF;
		        int alpha = buffer.get(1 + 3) & 0xff;
		        image.setRGB(x, sch - (y + 1), (alpha << 24) | (red << 16) | (green << 8) | blue);
		    }
		}
		   
		try {
		    ImageIO.write(image, format, file);
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public void setCursor(String cursorPath) {
		GLFWImage cursorImg = makeGLFWImage(cursorPath);

	    // create custom cursor and store its ID
	    int hotspotX = 0;
	    int hotspotY = 0;
	    cursorID = GLFW.glfwCreateCursor(cursorImg, hotspotX , hotspotY);

	    // set current cursor
	    glfwSetCursor(window, cursorID);
	}

	public void setCursorPos(double x, double y) {
		double wx = width * x;
		double wy = height * y;
		glfwSetCursorPos(window, x, y);
	}
	
	public Vector2f getMouseCoords(Camera c) {
		double[] x = new double[1];
		double[] y = new double[1];
		glfwGetCursorPos(window, x, y);
		Vector2f ret = new Vector2f((float)x[0], (float)y[0]);
		Transform t = c.getTransform();
		ret.mul(t.getInverseWidth()/screen.getFrameWidth(), t.getInverseHeight()/screen.getFrameHeight());
		//ret.x += (width - screen.getFrameWidth()) / ( 2 * CNGE.gameWidth / screen.getFrameWidth());
		//ret.y += (height - screen.getFrameHeight()) / ( 2 * CNGE.gameWidth / screen.getFrameHeight());
		//System.out.println((width - screen.getFrameWidth()) / ( 2 * CNGE.gameWidth / screen.getFrameWidth()));
		//System.out.println(width - screen.getFrameWidth());
		return ret;
	}
	
	public boolean mousePressed(int button) {
		return glfwGetMouseButton(window, button) == GLFW_PRESS;
	}
	
	public boolean keyPressed(int keyCode) {
		return glfwGetKey(window, keyCode) == GLFW_PRESS;
	}
	
	public void pollEvents() {
		glfwPollEvents();
	}
	
	public static void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	public static void clear(float r, float g, float b, float a) {
		glClearColor(r, g, b, a);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	public void swap() {
		glfwSwapBuffers(window);
	}
	
	public void close() {
		glfwSetWindowShouldClose(window, true);
	}
	
	public boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setSize(int w, int h) {
		glfwSetWindowSize(window, w, h);
		glViewport(0,0,w,h);
		width = w;
		height = h;
	}
	
	public long get() {
		return window;
	}
}
