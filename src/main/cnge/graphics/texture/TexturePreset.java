package cnge.graphics.texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_REPEAT;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;

/**
 * this is used to pass in parameters into vao texture in vao modular way, where you only need to give what's different than the default option
 * 
 * make you set the defaults to this class in main or per scene
 * 
 * @author Emmet
 */
public class TexturePreset {

	public static int defaultClampHorz;
	public static int defaultClampVert;
	public static int defaultMinFilter;
	public static int defaultMagFilter;

	public int clampHorz;
	public int clampVert;
	public int minFilter;
	public int magFilter;

	public static void setDefaults(int dch, int dcv, int dmf, int dgf) {
		defaultClampHorz = dch;
		defaultClampVert = dcv;
		defaultMinFilter = dmf;
		defaultMagFilter = dgf;
	}

	private TexturePreset() {
		clampHorz = defaultClampHorz;
		clampVert = defaultClampVert;
		minFilter = defaultMinFilter;
		minFilter = defaultMagFilter;
	}

	public static TexturePreset TP(){
		return new TexturePreset();
	}

	/*
	 * setters
	 */

	public TexturePreset ch_Repeat() {
		clampHorz = GL_REPEAT;
		return this;
	}

	public TexturePreset ch_Mirrored() {
		clampHorz = GL_MIRRORED_REPEAT;
		return this;
	}

	public TexturePreset ch_Edge() {
		clampHorz = GL_CLAMP_TO_EDGE;
		return this;
	}

	public TexturePreset ch_Border() {
		clampHorz = GL_CLAMP_TO_BORDER;
		return this;
	}

	public TexturePreset cv_Repeat() {
		clampVert = GL_REPEAT;
		return this;
	}

	public TexturePreset cv_Mirrored() {
		clampVert = GL_MIRRORED_REPEAT;
		return this;
	}

	public TexturePreset cv_Edge() {
		clampVert = GL_CLAMP_TO_EDGE;
		return this;
	}

	public TexturePreset cv_Border() {
		clampVert = GL_CLAMP_TO_BORDER;
		return this;
	}

	public TexturePreset min_nearest() {
		minFilter = GL_NEAREST;
		return this;
	}

	public TexturePreset min_linear() {
		minFilter = GL_LINEAR;
		return this;
	}

	public TexturePreset mag_nearest() {
		magFilter = GL_NEAREST;
		return this;
	}

	public TexturePreset mag_linear() {
		magFilter = GL_LINEAR;
		return this;
	}

	public TexturePreset repeat() {
		clampHorz = GL_REPEAT;
		clampVert = GL_REPEAT;
		return this;
	}

	public TexturePreset mirrored() {
		clampHorz = GL_MIRRORED_REPEAT;
		clampVert = GL_MIRRORED_REPEAT;
		return this;
	}

	public TexturePreset edge() {
		clampHorz = GL_CLAMP_TO_EDGE;
		clampVert = GL_CLAMP_TO_EDGE;
		return this;
	}

	public TexturePreset border() {
		clampHorz = GL_CLAMP_TO_BORDER;
		clampVert = GL_CLAMP_TO_BORDER;
		return this;
	}

	public TexturePreset nearest() {
		minFilter = GL_NEAREST;
		magFilter = GL_NEAREST;
		return this;
	}

	public TexturePreset linear() {
		minFilter = GL_LINEAR;
		minFilter = GL_LINEAR;
		return this;
	}

}
