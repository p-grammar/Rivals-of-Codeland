package cnge.graphics.shape;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

/**
 * vao shape that supports textures
 */
abstract public class TexShape extends Shape {

	protected void initTex(float[] vertices, int[] indices, float[] texCoords) {
		init(2, vertices, indices, GL_TRIANGLES);
		vao.addAttrib(texCoords, 2);
	}

}
