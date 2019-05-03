package game.shaders;

import cnge.graphics.Shader;

import static org.lwjgl.opengl.GL20.*;

/**
 * draws vao simple single color into vao shape
 */
public class TriangleShader extends Shader {

	private int colorLoc;
	private int offsetLoc;

	public TriangleShader() {
		super("res/shaders/triangle/tri2d.vs", "res/shaders/triangle/tri2d.fs");
		
		colorLoc = glGetUniformLocation(program, "color");
		offsetLoc = glGetUniformLocation(program, "offset");
	}
	
	/**
	 * sends uniforms to the color shader after being enabled
	 * 
	 * @param r - red value
	 * @param g - green value
	 * @param b - blue value
	 * @param a - alpha value
	 */
	public void setUniforms(float r, float g, float b, float a, float[] of) {
		glUniform4f(colorLoc, r, g, b, a);
		glUniform2fv(offsetLoc, of);
	}

}
