package cnge.graphics.texture;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import static cnge.graphics.texture.TexturePreset.TP;
import static cnge.graphics.texture.TexturePreset.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.glTexImage2DMultisample;

import cnge.graphics.Destroyable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Texture implements Destroyable {
	
	protected int id;

	protected int width;
	protected int height;

	protected TexturePreset preset;

	protected class TextureInfo {
		public int wi;
		public int hi;
		public ByteBuffer bb;

		public TextureInfo(int w, int h, ByteBuffer b) {
			wi = w;
			hi = h;
			bb = b;
		}
	}

	/*
	 * CONSTRUCTORS
	 */
	
	public Texture(String path, TexturePreset tp) {
		TextureInfo ti = createTextureInfo(path);
		init(ti.wi, ti.hi, ti.bb, tp);
		System.gc();
	}

	public Texture(String path) {
		TextureInfo ti = createTextureInfo(path);
		init(ti.wi, ti.hi, ti.bb, TP());
		System.gc();
	}

	public Texture(int w, int h, TexturePreset tp) {
		init(w, h, BufferUtils.createByteBuffer(w * h * 4).flip(), tp);
	}

	public Texture(int w, int h) {
		init(w, h, BufferUtils.createByteBuffer(w * h * 4).flip(), TP());
	}

	public Texture() {
		id = glGenTextures();
		preset = TP();
	}

	/*
	 * constructor helpers
	 */
	
	protected void init(int wi, int hi, ByteBuffer bb, TexturePreset tp) {
		id = glGenTextures();
		preset = tp;
		width = wi;
		height = hi;

		bind();

		parameters();

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, wi, hi, 0, GL_RGBA, GL_UNSIGNED_BYTE, bb);

		unbind();
	}

	protected void parameters() {
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, preset.clampHorz);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, preset.clampVert);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, preset.minFilter);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, preset.magFilter);
	}

	protected TextureInfo createTextureInfo(String path) {
		BufferedImage image = null;

		try {
			image = ImageIO.read(new File(path));
		} catch(IOException ex) {
			ex.printStackTrace();
			System.err.println("TEXTURE NOT FOUND, resolving to placeholder");
			try {
				image = ImageIO.read(new File("res/cnge/missing.png"));
			} catch (IOException ex2) { ex2.printStackTrace(); System.exit(-3); }
		}

		int wi = image.getWidth();
		int hi = image.getHeight();

		int[] pixels = image.getRGB(0, 0, wi, hi, null, 0, wi);

		ByteBuffer buffer = BufferUtils.createByteBuffer(wi * hi * 4);

		for(int i = 0; i < hi; ++i) {
			for(int j = 0; j < wi; ++j) {
				int pixel = pixels[i * wi + j];
				buffer.put((byte)((pixel >> 16) & 0xff));
				buffer.put((byte)((pixel >>  8) & 0xff));
				buffer.put((byte)((pixel      ) & 0xff));
				buffer.put((byte)((pixel >> 24) & 0xff));
			}
		}

		buffer.flip();

		return new TextureInfo(wi, hi, buffer);
	}

	/*
	 * stuff to do
	 */

	public void resize(int wi, int hi) {
		width = wi;
		height = hi;

		ByteBuffer bb = BufferUtils.createByteBuffer(wi * hi * 4);
		bb.flip();

		bind();

		parameters();

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, wi, hi, 0, GL_RGBA, GL_UNSIGNED_BYTE, bb);

		unbind();
	}

	public void bind() {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, id);
	}

	public void bind(int active) {
		glActiveTexture(GL_TEXTURE0 + active);
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	public static void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public int getId() {
		return id;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public TexturePreset getPreset() {
		return preset;
	}

	public Void destroy() {
		glDeleteTextures(id);
		return null;
	}

}
