package game.shaders;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform4f;

import cnge.graphics.Shader;
import org.joml.Vector4f;

public class TileShader extends Shader {
	
	private int texLoc;
	private int colorLoc;
	private int frameLoc;

	public TileShader() {
		super("res/shaders/tile/til2d.vs", "res/shaders/tile/til2d.fs");
		
		texLoc = glGetUniformLocation(program, "frame");
		colorLoc = glGetUniformLocation(program, "inColor");
	}
	
	public void setUniforms(Vector4f dims, float r, float g, float b, float a) {
		glUniform4f(texLoc, dims.x, dims.y, dims.z, dims.w);
		glUniform4f(colorLoc, r, g, b, a);
	}
	
}