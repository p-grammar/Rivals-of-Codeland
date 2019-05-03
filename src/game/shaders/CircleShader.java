package game.shaders;

import cnge.graphics.Shader;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform4f;

/**
 * draws vao simple single color into vao shape
 */
public class CircleShader extends Shader {

	private int colorLoc;

	public CircleShader() {
		super("res/shaders/circle/cir2d.vs", "res/shaders/circle/cir2d.fs");
		
		colorLoc = glGetUniformLocation(program, "color");
	}
	
	/**
	 * sends uniforms to the color shader after being enabled
	 * 
	 * @param r - red value
	 * @param g - green value
	 * @param b - blue value
	 * @param a - alpha value
	 */
	public void setUniforms(float r, float g, float b, float a) {
		glUniform4f(colorLoc, r, g, b, a);
	}

}
