package cnge.graphics.shape;

public class FramebufferRect extends TexShape{

	public FramebufferRect() {
		initTex(
			new float[] {
			   1, 0, 0,
			   1, 1, 0,
			   0, 1, 0,
			   0, 0, 0
			}, new int[] {
				0, 1, 3,
				1, 2, 3
			}, new float[] {
				1, 1,
				1, 0,
				0, 0,
				0, 1
			}
		);
	}
	
}
