package cnge.graphics.texture;

import org.joml.Vector4f;

public class TileTexture extends Texture {
	
	private float frameWidth;
	private float frameHeight;

	/*
	 * constructors
	 */

	public TileTexture(String path, int fw, int ft, TexturePreset tp) {
		super(path, tp);
		frameWidth = 1f/fw;
		frameHeight = 1f/ft;
	}

	public TileTexture(String path, int fw, int ft) {
		super(path);
		frameWidth = 1f/fw;
		frameHeight = 1f/ft;
	}

	public TileTexture(String path, int fw, TexturePreset tp) {
		super(path, tp);
		frameWidth = 1f/fw;
	}

	public TileTexture(String path, int fw) {
		super(path);
		frameWidth = 1f/fw;
	}

	public Vector4f getDims(int x, int y) {
		Vector4f dims = new Vector4f();
		dims.x = frameWidth;
		dims.y = frameHeight;
		dims.z = x*frameWidth;
		dims.w = y*frameHeight;
		return dims;
	}

	public Vector4f getDims(int x) {
		Vector4f dims = new Vector4f();
		dims.x = frameWidth;
		dims.y = frameHeight;
		dims.z = x*frameWidth;
		dims.w = 0;
		return dims;
	}

}