package cnge.graphics.shape;

import org.lwjgl.opengl.GL11;

public class ArrowShape extends TexShape{

	public ArrowShape() {
		initTex(
			new float[] {
				0.5f, 0f, 0,
				1f, 0.5f, 0,
				0f, 0.5f, 0,
				0.4f, 0.5f, 0,
				0.6f, 0.5f, 0,
				0.4f, 1f, 0f,
				0.6f, 1f, 0f
			}, new int[] {
				0, 1, 2,
				3, 4, 5,
				4, 5, 6
			}, new float[] {
				0.5f, 0f,
				1f, 0.5f,
				0f, 0.5f,
				0.4f, 0.5f,
				0.6f, 0.5f,
				0.5f, 1f,
				0.6f, 1f
			}
		);
	}

}
