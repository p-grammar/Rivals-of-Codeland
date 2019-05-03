package cnge.graphics.shape;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class VAO {
	
	private int mode;
	private int numAttribs;
	private int ibo;
	private int vao;
	private int nor;
	private int count;
	private int[] attribs;
	
	public VAO(int num, float[] vertices, int[] indices, int drawMode) {
		attribs = new int[num];
		numAttribs = 0;
		
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		addAttrib(vertices, 3);
		
		ibo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

		count = indices.length;
		
		mode = drawMode;
	}
	
	public void addAttrib(float[] f, int s) {
		int vbo = glGenBuffers();
		attribs[numAttribs] = vbo;
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, f, GL_STATIC_DRAW);
		glVertexAttribPointer(numAttribs, s, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(numAttribs);
		++numAttribs;
	}
	
	public void render() {
		glBindVertexArray(vao);
		glDrawElements(mode, count, GL_UNSIGNED_INT, 0);
	}
	
	protected void destroy() {
		for(int i = 0; i < numAttribs; ++i) {
			glDeleteBuffers(attribs[i]);
		}
		glDeleteBuffers(ibo);
		glDeleteVertexArrays(vao);
	}
	
}
