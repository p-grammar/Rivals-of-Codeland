package cnge.graphics.texture;

import org.lwjgl.BufferUtils;
import org.w3c.dom.Text;

import java.nio.ByteBuffer;

import static cnge.graphics.texture.TexturePreset.TP;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.glTexImage2DMultisample;

public class MultisampleTexture extends Texture {

	private int samples;

	public MultisampleTexture(int wi, int hi, int sa, TexturePreset tp) {
		init(wi, hi, sa, tp);
	}

	public MultisampleTexture(int wi, int hi, int sa) {
		init(wi, hi, sa, TP());
	}

	private void init(int w, int h, int s, TexturePreset t) {
		id = glGenTextures();
		width = w;
		height = h;
		samples = s;
		preset = t;

		bind();

		parameters();

		glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, GL_RGB, width, height, true);

		unbind();
	}

	public void resize(int wi, int hi) {
		width = wi;
		height = hi;

		ByteBuffer bb = BufferUtils.createByteBuffer(wi * hi * 4);
		bb.flip();

		bind();

		parameters();

		glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, GL_RGB, width, height, true);

		unbind();
	}

	@Override
	public void bind() {
		glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, id);
	}

	public static void unbind() {
		glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, 0);
	}

}
