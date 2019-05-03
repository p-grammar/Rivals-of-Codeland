package cnge.graphics.shape;

import cnge.graphics.Destroyable;

abstract public class Shape implements Destroyable {
	
	protected VAO vao;

	protected void init(int numAttribs, float vertices[], int[] indices, int drawMode) {
		vao = new VAO(numAttribs, vertices, indices, drawMode);
	}

	public void render() {
		vao.render();
	}

	public Void destroy() {
		vao.destroy();
		return null;
	}
	
}