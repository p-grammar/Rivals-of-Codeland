package game.blob;

import cnge.core.CCD;
import cnge.core.CNGE;
import cnge.core.Loop;
import cnge.core.Timer;
import cnge.graphics.Transform;
import game.GameScene;
import game.Map;
import game.GameAssets;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;

public class Player extends Blob {

    Map.Line lastCollide;
    private float mStartX;
    private float mStartY;
    private float mFlingX;
    private float mFlingY;
    private CCD.Vector flingVec;
    private boolean flingMode;

    private Transform arrowTransform;

    private float oldRadius;
    private float goingToBeRadius;
    private Timer growTimer;
    private boolean growing;

    private boolean winning;
    private Timer winTimer;
    private CCD.Line winLine;

    private boolean hit;
    private boolean controllable;
    private boolean epic_win;

    private float maxSize;

    public Player(float o, float a, float r, int ms) {
        super(o, a, r);
        speed = 0;
        angle  = 0;
        lastCollide = null;
        arrowTransform = new Transform();
        growTimer = new Timer(1);
        winTimer = new Timer(1);
        controllable = false;
        epic_win = false;
        maxSize = ms + 0.5f;
    }

    public void update(Map map) {

        if(winning) {
            winTimer.update(Loop.time);
            double along = winTimer.getAlong();
            x = (float)CCD.xAlong(along, winLine);
            y = (float)CCD.yAlong(along, winLine);
        } else {
            if (growing) {
                growing = !growTimer.update(Loop.time);
            }

            if(controllable) {
                if (flingMode) {
                    if (CNGE.window.mousePressed(GLFW_MOUSE_BUTTON_2)) {
                        flingMode = false;
                    } else {
                        doArrow();
                        CNGE.window.setCursorPos(0.5, 0.5);
                        if (!CNGE.window.mousePressed(GLFW_MOUSE_BUTTON_1)) {
                            flingMode = false;
                            angle = (float) flingVec.getAngle();
                            speed = (float) flingVec.length();
                        }
                    }
                } else {
                    if (CNGE.window.mousePressed(GLFW_MOUSE_BUTTON_1)) {
                        flingMode = true;
                        Vector2f mouse = CNGE.window.getMouseCoords(CNGE.camera);
                        CNGE.window.setCursorPos(0.5, 0.5);
                        mStartX = mouse.x;
                        mStartY = mouse.y;
                        mFlingX = mouse.x;
                        mFlingY = mouse.y;
                        doArrow();
                    }
                }
            } else {
                flingMode = false;
            }

            speed -= FRICTION * Loop.time;

            boolean collided = false;

            if(growing) {
                radius = Timer.LINEAR.interpolate(oldRadius, goingToBeRadius, growTimer.getAlong());
            }

            if (speed < 0) {
                speed = 0;
            }

            double speedStep = speed * Loop.time;

            float dx = (float) (Math.cos(angle) * speedStep);
            float dy = (float) (Math.sin(angle) * speedStep);

            ColPackage pack = movement(map, dx, dy, growing);
            collided = pack.colMode != ColPackage.NO_COLLISION;

            if(radius >= map.getBloodCost() && pack.bestWall == map.getGateLine()) {
                winning = true;
                winTimer.start();
                CCD.Line l = pack.bestWall.line;

                CCD.Vector wv = new CCD.Vector(l);
                float cx = wv.getCenterX();
                float cy = wv.getCenterY();

                CCD.Vector mv = new CCD.Vector(radius, 0);
                mv.rotate(pack.wallAngle - CNGE.PI / 2);

                winLine = new CCD.Line(x, y, cx + mv.x + l.x0, cy + mv.y + l.y0);

                epic_win = true;
            }

        }
    }

    /**
     * collision with enemies chafeel
     *
     * @return tru if u died
     */
    public boolean enemyUpdate(GameScene scene, Enemy[] eList) {
        int len = eList.length;
        for(int i = 0; i < len; ++i) {
            Enemy en = eList[i];
            if(en != null && blobCollision(en)) {
                if(radius >= en.radius) {
                    growBy(radiusToAdd(en.calculateArea()));
                    scene.removeEnemy(en.enemyIndex);
                    scene.inccreaseRampage(1f);
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    private void doArrow() {
        Vector2f mouse = CNGE.window.getMouseCoords(CNGE.camera);
        Transform ct = CNGE.camera.getTransform();
        mFlingX += ct.width / 2 - mouse.x;
        mFlingY += ct.height / 2 - mouse.y;
        flingVec = new CCD.Vector(mFlingX - mStartX, mFlingY - mStartY);
    }

    public void growBy(float r) {
        growTimer.setTime(0.5);
        growTimer.start();
        oldRadius = radius;
        if(growing) {//if already growing
            goingToBeRadius = goingToBeRadius + r;
        } else {
            goingToBeRadius = radius + r;
        }
        if(goingToBeRadius > maxSize) {
            goingToBeRadius = maxSize;
        }
        growing = true;
    }

    public void render() {
        transform.setSize(radius * 2, radius * 2);
        transform.setCenter(x, y);

        GameAssets.circleShader.enable();
        GameAssets.circleShader.setUniforms(1f, 0f, 1f, 1f);
        GameAssets.circleShader.setMvp(CNGE.camera.getMVP(CNGE.camera.getM(transform)));
        GameAssets.rect.render();
    }

    public void arrowRender() {
        if(flingMode && !epic_win) {
            arrowTransform.setSize(radius * 2, (float)flingVec.length());
            arrowTransform.setCenter(x + flingVec.getCenterX(), y + flingVec.getCenterY());
            arrowTransform.rotation = (float)flingVec.getAngle() + CNGE.PI / 2;
            GameAssets.gradientShader.enable();
            GameAssets.gradientShader.setUniforms(1, 1, 1, 0.5f, 1, 1, 1, 0f);
            GameAssets.gradientShader.setMvp(CNGE.camera.getMVP(CNGE.camera.getM(arrowTransform)));
            GameAssets.arrowShape.render();
        }
    }

    public void setControllable(boolean c) {
        controllable = c;
    }

    public boolean winUpdate() {
        return epic_win;
    }

}
