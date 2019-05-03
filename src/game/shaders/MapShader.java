package game.shaders;

import cnge.graphics.Shader;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL20.*;

/**
 * draws vao simple single color into vao shape
 */
public class MapShader extends Shader {

	private int colorLoc;
	private int frameLoc;

	private int samplerLoc;
	private int sampler2Loc;

	private int invertLoc;

	public MapShader() {
		super("res/shaders/map/map2d.vs", "res/shaders/map/map2d.fs");
		
		colorLoc = glGetUniformLocation(program, "inColor");
		frameLoc = glGetUniformLocation(program, "frame");
		samplerLoc = glGetUniformLocation(program, "sampler");
		sampler2Loc = glGetUniformLocation(program, "sampler2");
		invertLoc = glGetUniformLocation(program, "invert");
	}
	
	/**
	 * sends uniforms to the color shader after being enabled
	 * 
	 * @param r - red value
	 * @param g - green value
	 * @param b - blue value
	 */
	public void setUniforms(Vector4f frame, float r, float g, float b, int a0, int a1, float i) {
		glUniform4f(frameLoc, frame.x, frame.y, frame.z, frame.w);
		glUniform3f(colorLoc, r, g, b);
		glUniform1i(samplerLoc, a0);
		glUniform1i(sampler2Loc, a1);
		glUniform1f(invertLoc, i);
	}

}
