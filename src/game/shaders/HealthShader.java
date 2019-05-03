package game.shaders;

import cnge.graphics.Shader;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL20.*;

/**
 * draws vao simple single color into vao shape
 */
public class HealthShader extends Shader {

	private int colorLoc;
	private int modelLoc;
	private int planeLoc;

	public HealthShader() {
		super("res/shaders/health/hlt2d.vs", "res/shaders/health/hlt2d.fs");
		
		colorLoc = glGetUniformLocation(program, "inColor");
		modelLoc = glGetUniformLocation(program, "model");
		planeLoc = glGetUniformLocation(program, "plane");
	}
	
	/**
	 * sends uniforms to the color shader after being enabled
	 * 
	 * @param r - red value
	 * @param g - green value
	 * @param b - blue value
	 */
	public void setUniforms(float r, float g, float b) {
		glUniform3f(colorLoc, r, g, b);
	}

	public void setPlane(float x, float y, float z, float w) {
		glUniform4f(planeLoc, x, y, z, w);
	}

	public void setModelMatrix(Matrix4f model) {
		glUniformMatrix4fv(modelLoc, false, model.get(new float[16]));
	}

}
