package cnge.graphics.shape;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class CircleShape extends TexShape {

	public CircleShape(int p) {
		float[] v = makeVertices(p);
		initTex(v, makeIndices(p), makeTexCoords(p, v));
	}

	private float[] makeVertices(int p) {
		float[] ret = new float[p * 3];
		for(int i = 0; i < p; ++i) {
			double ang = i * 2 * Math.PI / p;
			ret[i * 3    ] = (float)(Math.cos(ang) + 1) / 2;
			ret[i * 3 + 1] = (float)(Math.sin(ang) + 1) / 2;
			ret[i * 3 + 2] = 0;
		}
		return ret;
	}

	private int[] makeIndices(int p) {
		int t = p - 2;
		int[] ret = new int[t * 3];
		for(int i = 0; i < t; ++i) {
			ret[i * 3    ] = 0;
			ret[i * 3 + 1] = i + 1;
			ret[i * 3 + 2] = i + 2;
		}
		return ret;
	}

	private float[] makeTexCoords(int p, float[] v) {
		float[] ret = new float[p * 2];
		for(int i = 0; i < p; ++i) {
			ret[i * 2    ] = v[i * 3];
			ret[i * 2 + 1] = v[i * 3 + 1];
		}
		return ret;
	}

}
