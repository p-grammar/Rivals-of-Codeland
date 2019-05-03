package cnge.graphics;

import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

abstract public class Shader implements Destroyable {
	
	/**
	 * the program ID of the shader
	 */
	protected int program;
	
	/**
	 * all these shaders use the model view projection matrix, this is the uniform location
	 */
	protected int mvpLoc;
	
	/**
	 * creates vao shader
	 * 
	 * @param vertPath - path to the base code of the vertex shader
	 * @param fragPath - path to the base code of the fragment shader
	 */
	protected Shader(String vertPath, String fragPath) {
		program = glCreateProgram();
		int vert = loadShader(vertPath, GL_VERTEX_SHADER);
		int frag = loadShader(fragPath, GL_FRAGMENT_SHADER);
		glAttachShader(program, vert);
		glAttachShader(program, frag);
		glLinkProgram(program);
		glDetachShader(program, vert);
		glDetachShader(program, frag);
		glDeleteShader(vert);
		glDeleteShader(frag);
		
		mvpLoc = glGetUniformLocation(program, "mvp");
	}
	
	private int loadShader(String path, int type) {
		StringBuilder build = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			String line;
			while((line = br.readLine()) != null) {
				build.append(line);
				build.append('\n');
			}
			br.close();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		String src = build.toString();
		int shader = glCreateShader(type);
		glShaderSource(shader, src);
		
		glCompileShader(shader);

		if(glGetShaderi(shader, GL_COMPILE_STATUS) != 1) {
			System.err.println(glGetProgramInfoLog(shader, glGetProgrami(shader, GL_INFO_LOG_LENGTH)));
			throw new RuntimeException("Failed to compile shader | " + path + " | " + type + " | " + glGetShaderInfoLog(shader) + " PPP ");
		}
		return shader;
	}

	public void setMvp(Matrix4f mvp) {
		glUniformMatrix4fv(mvpLoc, false, mvp.get(new float[16]));
	}
	
	/**
	 * starts the shader.
	 * call this before rending to use the shader
	 */
	public Shader enable() {
		glUseProgram(program);
		return this;
	}

	/**
	 * NOTE: THERE'S NO METHOD HERE
	 * 
	 * make your own method in the shader to pass uniforms
	 * 
	 * gl
	 */
	
	/**
	 * call this after rendering is done to stop using the shader
	 */
	public static void disable() {
		glUseProgram(0);
	}
	
	/**
	 * deletes the shader from opengl
	 */
	public Void destroy() {
		glDeleteProgram(program);
		return null;
	}

}