package game.shaders;

import cnge.graphics.Shader;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform4f;

/**
 * draws vao simple single color into vao shape
 */
public class GradientShader extends Shader {

	private int colorLoc;
	private int colorLoc2;

	public GradientShader() {
		super("res/shaders/gradient/gra2d.vs", "res/shaders/gradient/gra2d.fs");
		
		colorLoc = glGetUniformLocation(program, "color");
		colorLoc = glGetUniformLocation(program, "color2");
	}
	
	/**
	 * sends uniforms to the color shader after being enabled
	 * LOTS GRADIENT COLRS
	 */
	public void setUniforms(float r, float g, float b, float a, float r1, float g1, float b1, float a1) {
		glUniform4f(colorLoc, r, g, b, a);
		glUniform4f(colorLoc2, r1, g1, b1, a1);
	}

}
