package game.blob;

import cnge.core.CCD;
import cnge.core.CNGE;
import cnge.core.Loop;
import cnge.graphics.Transform;
import game.Map;
import game.GameAssets;

import static org.lwjgl.glfw.GLFW.*;

abstract public class Blob {

	public static class ColPackage {
		public static final int NO_COLLISION = 0;
		public static final int LINE_COLLISION = 1;
		public static final int CIRCLE_COLLISION = 2;

		public int colMode;
		public Map.Line bestWall;
		public double wallAngle;
		public double bestT;
		public float cx;
		public float cy;

		public ColPackage() {
			colMode = 0;
			bestWall = null;
			wallAngle = 0;
			bestT = 2;
			cx = 0;
			cy = 0;
		}
	}

	public static float FRICTION = 8f;

	//core components of blobs
	public float x, y;
	public float radius;

	public Transform transform;
	public float speed;
	protected float angle;

	public Blob(float o, float a, float r) {
		x = o;
		y = a;
		radius = r;
		transform = new Transform();
	}

	public boolean blobCollision(Blob blob2) {
		CCD.Vector v = new CCD.Vector(blob2.x - x, blob2.y - y);
		double dist = v.length();
		return dist < (radius + blob2.radius);
	}

	public float calculateArea() {
		return radius * radius * CNGE.PI;
	}

	public float radiusToAdd(float area) {
		return (float)((-2 * Math.PI * radius + Math.sqrt(4*Math.PI*Math.PI*radius*radius + 4*Math.PI*area)) / (2 * Math.PI));
	}

	public void cameraOnThis() {

		double constant = CNGE.gameWidth / 6;

		double zoom = CNGE.gameWidth / (constant * radius);

		Transform ct = CNGE.camera.getTransform();

		ct.setScale((float)zoom, (float)zoom);
		ct.setInverseCenter(x, y);
	}

	protected ColPackage movement(Map map, float dx, float dy, boolean gr) {
		CCD.Line move = new CCD.Line(x, y, x + dx, y + dy);
		CCD.Line growMove = new CCD.Line(move);

		ColPackage colPackage = new ColPackage();

		int[] moveBounds = map.getBoundsUnsorted(x, y, x + dx, y + dy);

		for (int i = moveBounds[0]; i <= moveBounds[1]; ++i) {
			for (int j = moveBounds[2]; j <= moveBounds[3]; ++j) {
				if (map.zoneInRange(i, j)) {
					Map.Line[] lines = map.getLineZones()[i][j];
					int numLines = lines.length;
					for (int k = 0; k < numLines; ++k) {
						Map.Line l = lines[k];
						CCD.Line line = l.line;

						CCD.Vector wv = new CCD.Vector(line);
						double tempWallAngle = wv.getAngle();

						//grow prevention
						if(gr) {
							double[] circs = CCD.circleCollisions(line, x, y, radius);
							if(CCD.eitherInRange(circs)) {
								CCD.Vector push = CCD.moveCircle(line, circs, x, y, radius);
								growMove.copy(move);
								growMove.add(push);
							}
						}
						//

						CCD.Vector off = new CCD.Vector(radius, 0);
						if (CCD.normalSide(l.line, x, y)) {
							off.rotate(tempWallAngle - CNGE.PI / 2);
						} else {
							off.rotate(tempWallAngle + CNGE.PI / 2);
						}

						CCD.Line colLine = new CCD.Line(line.x0 + off.x, line.y0 + off.y, line.x1 + off.x, line.y1 + off.y);

						double effectiveT = 2;
						int nowMode = ColPackage.NO_COLLISION;
						float cx = 0;
						float cy = 0;

						double circleStartT = CCD.circleCollision(growMove, line.x0, line.y0, radius);
						if (CCD.inline(circleStartT) && circleStartT < effectiveT) {
							effectiveT = circleStartT;
							nowMode = ColPackage.CIRCLE_COLLISION;
							cx = line.x0;
							cy = line.y0;
						}

						double circleEndT = CCD.circleCollision(growMove, line.x1, line.y1, radius);
						if (CCD.inline(circleEndT) && circleEndT < effectiveT) {
							effectiveT = circleEndT;
							nowMode = ColPackage.CIRCLE_COLLISION;
							cx = line.x1;
							cy = line.y1;
						}

						CCD.Collision col = CCD.result(growMove, colLine);
						if (CCD.inline(col.t_) && col.collision() && col.t_ < effectiveT) {
							effectiveT = col.t_;
							nowMode = ColPackage.LINE_COLLISION;
						}

						if (nowMode != ColPackage.NO_COLLISION && effectiveT < colPackage.bestT) {
							colPackage.colMode = nowMode;
							colPackage.wallAngle = (nowMode == ColPackage.LINE_COLLISION) ?
								tempWallAngle :
								Math.atan2(CCD.yAlong(effectiveT, move) - cy, CCD.xAlong(effectiveT, move) - cx) + Math.PI / 2;
							colPackage.bestWall = l;
							colPackage.bestT = effectiveT;
							colPackage.cx = cx;
							colPackage.cy = cy;
						}
					}
				}
			}
		}

		x = growMove.x0;
		y = growMove.y0;

		if (colPackage.colMode != ColPackage.NO_COLLISION) {
			double colX = CCD.xAlong(colPackage.bestT - 0.01f, growMove);
			double colY = CCD.yAlong(colPackage.bestT - 0.01f, growMove);

			dx = (float) (colX - x);
			dy = (float) (colY - y);

			angle = (float) (2 * (colPackage.wallAngle) - angle);
			speed *= 0.75f;
		}

		x += dx;
		y += dy;

		return colPackage;
	}

	abstract public void update(Map map);

	abstract public void render();

}
