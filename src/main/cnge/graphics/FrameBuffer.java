package cnge.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;

import cnge.core.CNGE;
import cnge.graphics.texture.MultisampleTexture;
import cnge.graphics.texture.Texture;

public class FrameBuffer extends CNGE implements Destroyable {
	
    private int id;
    private int colorRenderBufferID = -1;
    private int depthRenderBufferID = -1;

    private int width;
    private int height;

    private Texture colorTexture;
    private Texture depthTexture;

    private boolean hasDepth;

    private boolean multiSampled;
    private int samples;

    private Attachment colorAttachment;
    private Attachment depthAttachment;

    private interface Attachment {
        void attach(FrameBuffer fb);
    }

    private void init(Texture ct, Texture dt, int w, int h, boolean m, int s, boolean d, Attachment color, Attachment depth) {
        colorTexture = ct;
        depthTexture = dt;
        width = w;
        height = h;
        multiSampled = m;
        samples = s;
        colorAttachment = color;
        depthAttachment = depth;
        id = glGenFramebuffers();

        bind();

        colorAttachment.attach(this);
        if(hasDepth = d) {
            depthAttachment.attach(this);
        }

        unbind();
    }

    public FrameBuffer(Texture t, boolean d) {
        int w = t.getWidth();
        int h = t.getHeight();
        init(t, new Texture(w, h, t.getPreset()), w, h, false, 0, d, textureColor, textureDepth);
    }

    public FrameBuffer(MultisampleTexture t, boolean d) {
        int w = t.getWidth();
        int h = t.getHeight();
        init(t, new Texture(w, h, t.getPreset()), w, h, false, 0, d, textureColorMultisample, textureDepthMultisample);
    }

    public FrameBuffer(int w, int h, int s, boolean d) {
        init(null, null, w, h, false, s, d, colorMultisample, depthMultisample);
    }

    public void resize(int w, int h) {
        width = w;
        height = h;
        if(colorTexture != null) {
            colorTexture.resize(width, height);
        }
        if(depthTexture != null) {
            depthTexture.resize(width, height);
        }
        bind();
        colorAttachment.attach(this);
        if(hasDepth) {
            depthAttachment.attach(this);
        }
        unbind();
    }

    private static Attachment textureColor = (FrameBuffer fb) -> {
        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, fb.colorTexture.getId(), 0);
    };
    private static Attachment textureDepth = (FrameBuffer fb) -> {
        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, fb.depthTexture.getId(), 0);
    };

    private static Attachment textureColorMultisample = (FrameBuffer fb) -> {
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, fb.colorTexture.getId(), 0);
    };
    private static Attachment textureDepthMultisample = (FrameBuffer fb) -> {
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D_MULTISAMPLE, fb.depthTexture.getId(), 0);
    };

    private static Attachment colorMultisample = (FrameBuffer fb) -> {
        fb.colorRenderBufferID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, fb.colorRenderBufferID);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, fb.samples, GL_RGBA8, fb.width, fb.height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, fb.colorRenderBufferID);
    };
    private static Attachment depthMultisample = (FrameBuffer fb) -> {
        fb.depthRenderBufferID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, fb.depthRenderBufferID);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, fb.samples, GL_DEPTH_COMPONENT, fb.width, fb.height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, fb.depthRenderBufferID);
    };

    private void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, id);
    }

    private void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void resolve(FrameBuffer output) {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, id);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, output.getId());
        int bits = (hasDepth && output.hasDepth) ? GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT : GL_COLOR_BUFFER_BIT;
        glBlitFramebuffer(0, 0, width, height, 0, 0, output.getWidth(), output.getHeight(), bits, GL_LINEAR);
        unbind();
    }

    /*
     * enablers
     */

    public void enable() {
        bind();
        glViewport(0, 0, width, height);
    }

    public void enableTexture() {
        glBindTexture(GL_TEXTURE_2D, colorTexture.getId());
        bind();
        glViewport(0, 0, width, height);
    }

    public void enableTextureMultisample() {
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, colorTexture.getId());
        bind();
        glViewport(0, 0, width, height);
    }
    
    /**
     * switches the frameBuffer back to the window
     */
    public static void enableDefault() {
    	glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, window.getWidth(), window.getHeight());
    }
    
    /**
     * returns the current texture bound to the fbo
     * 
     * @return current texture
     */
    public Texture getTexture() {
        return colorTexture;
    }
    
    /**
     * gets the fbo handle
     * 
     * @return fbo handle
     */
    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Void destroy() {
        if(colorTexture != null) {
            colorTexture.destroy();
        }
        if(depthTexture != null) {
            depthTexture.destroy();
        }
        if(colorRenderBufferID != -1) {
            glDeleteBuffers(colorRenderBufferID);
        }
        if(depthRenderBufferID != -1) {
            glDeleteBuffers(depthRenderBufferID);
        }
        glDeleteFramebuffers(id);
        return null;
    }

}
