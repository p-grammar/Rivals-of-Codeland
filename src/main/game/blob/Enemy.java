package game.blob;

import cnge.core.CCD;
import cnge.core.CNGE;
import cnge.core.Loop;
import cnge.core.Timer;
import cnge.graphics.Transform;
import game.GameAssets;
import game.Map;

public class Enemy extends Blob {

    public static final double MIN_MOVE_TIME = 1;
    public static final double MAX_MOVE_TIME = 2;

    private Timer moveTimer;
    private Timer spawnTimer;

    private CCD.Line spawnLine;

    private Transform lineTransform;

    private boolean spawning;

    public int enemyIndex;
    private float pRadius;

    private float nextAngle;

    public Enemy(float r, Map.Line s, int i) {
        super(0, 0, r);
        enemyIndex = i;
        spawning = true;

        CCD.Line l = s.line;
        CCD.Vector lv = new CCD.Vector(l);
        double lineAngle = lv.getAngle();
        CCD.Vector nv = new CCD.Vector(2.5f * r, 0);
        nv.rotate(lineAngle - CNGE.PI / 2);

        float cx = lv.getCenterX() + l.x0 + nv.getCenterX();
        float cy = lv.getCenterY() + l.y0 + nv.getCenterY();

        spawnLine = new CCD.Line(cx, cy, cx - nv.x, cy - nv.y);

        lineTransform = new Transform();

        angle = (float)lineAngle + CNGE.PI / 2;
        nextAngle = (float)(Math.random() * Math.PI * 2);

        spawnTimer = new Timer(0.5f);
        moveTimer = new Timer(MIN_MOVE_TIME);
        spawnTimer.start();
    }

    public void resetMoveTimer() {
        moveTimer.setTime((Math.random() * (MAX_MOVE_TIME - MIN_MOVE_TIME)) + MIN_MOVE_TIME);
    }

    @Override
    public void update(Map map) {
        if(spawning) {
            spawning = !spawnTimer.update(Loop.time);
            double t = Timer.COSINE.interpolate(0, 1, spawnTimer.getAlong());
            x = (float)CCD.xAlong(t, spawnLine);
            y = (float)CCD.yAlong(t, spawnLine);
            if(!spawning) {
                resetMoveTimer();
                moveTimer.start();
            }
        } else {
            if(moveTimer.continualUpdate(Loop.time)){
               resetMoveTimer();
               angle = nextAngle;
               nextAngle = (float)(Math.random() * Math.PI * 2);
               speed = (float)(Math.random() * 20 + 10) * (float)Math.sqrt(radius);
            }

            speed -= FRICTION * Loop.time;
            if(speed < 0) {
                speed = 0;
            }

            double speedStep = speed * Loop.time;
            float dx = (float)(speedStep * Math.cos(angle));
            float dy = (float)(speedStep * Math.sin(angle));

            ColPackage pack = movement(map, dx, dy, false);
        }
    }

    public void givePRadius(float r) {
        pRadius = r;
    }
    
    @Override
    public void render() {
        transform.setSize(radius * 2, radius * 2);
        transform.setCenter(x, y);

        GameAssets.circleShader.enable();
        if(radius <= pRadius) {
            GameAssets.circleShader.setUniforms(0.9f, 0f, 0f, 1f);
        } else {
            GameAssets.circleShader.setUniforms(0.6f, 0f, 0f, 1f);
        }
        GameAssets.circleShader.setMvp(CNGE.camera.getMVP(CNGE.camera.getM(transform)));
        GameAssets.rect.render();

        lineTransform.setSize(radius * 2, radius * 2);
        lineTransform.setCenter(x, y);
        float along = 2 * (float)moveTimer.getAlong() - 1;
        if(along < 0) {
            along = 0;
        }

        double startAngle = angle;
        if(startAngle > Math.PI * 2) {
            startAngle -= Math.PI * 2;
        } else if(startAngle < 0) {
            startAngle += Math.PI * 2;
        }
        double endAngle = nextAngle;
        if(endAngle < Math.PI) {
            if (angle > nextAngle + Math.PI) {
                startAngle = angle - (Math.PI * 2);
            }
        } else {
            if (angle < nextAngle - Math.PI) {
                startAngle = angle + (Math.PI * 2);
            }
        }
        lineTransform.rotation = Timer.LINEAR.interpolate(startAngle, nextAngle, along) + CNGE.PI / 2;

        GameAssets.colorShader.enable();
        GameAssets.colorShader.setUniforms(0, 0, 0, 0.5f);
        GameAssets.colorShader.setMvp(CNGE.camera.getMVP(CNGE.camera.getM(lineTransform)));
        GameAssets.arrowShape.render();
    }
}
