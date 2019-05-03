package game;

import cnge.core.*;
import cnge.graphics.Transform;
import game.blob.Enemy;
import game.blob.Player;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class GameScene extends Scene {

    private static final char[] BLOOD_TEXT = {'B','l','o','o','d'};
    private static final double RAMPAGE_DECAY = 0.25;

    private Level startingLevel;
    private Level currentLevel;
    private Map currentMap;

    private Player player;

    private int maxEnemies;
    private int numEnemies;
    private int enemyPlace;
    private Enemy[] enemies;

    private Timer enemySpawnTimer;

    private boolean starting;
    private Timer levelStartTimer;

    private boolean lost;
    private float darken;
    private Timer deathTimer;

    private boolean won;
    private Timer winTimer;

    private Transform healthTransform;

    private float songMix;
    private Timer songMixTimer;
    private float rampageMeter;
    private boolean songChanging;
    private boolean rampageMode;
    private float songMixStart;
    private float songMixEnd;

    private boolean cameFromWin;

    public GameScene(Class<? extends AssetBundle>... unloads) {
        super(unloads, GameLoadScreen.class, GameAssets.class);
    }

    @Override
    /**
     * we get here from menu, runs once per game at the start
     */
    public void sceneStart() {
        CNGE.window.hideCursor();

        Map.setupBloodMaps();

        startingLevel = new Level(
            0,
            () -> new Map(2, 2, "res/maps/map0.svg", 4, 20, 2),
            new Level(
                1,
                () -> new Map(2, 2, "res/maps/map1.svg", 10, 30, 5),
                new Level(
                    2,
                    () -> new Map(2, 2, "res/maps/map3.svg", 3, 35, 1),
                    new Level(
                        3,
                        () -> new Map(2, 2, "res/maps/map2.svg", 6, 35, 2),
                        new Level(
                            3,
                            () -> new Map(2, 2, "res/maps/map4.svg", 9, 35, 2),
                            null
                        )
                    )
                )
            )
        );

        songMixTimer = new Timer(0.65);
        GameAssets.calmSong.play(true);
        GameAssets.fightSong.play(true);
        songMix = 0;

        healthTransform = new Transform();

        cameFromWin = false;

        levelStartRoutine(startingLevel);
    }

    public void levelStartRoutine(Level l) {
        currentLevel = l;
        currentMap = l.load();
        player = new Player(currentMap.getPlayerStartX(), currentMap.getPlayerStartY(), currentMap.getStartSize(), currentMap.getBloodCost());

        maxEnemies = currentMap.getMaxEnemies();
        enemies = new Enemy[maxEnemies];
        numEnemies = 0;
        enemyPlace = 0;

        lost = false;
        deathTimer = new Timer(2);

        won = false;
        winTimer = new Timer(2);
        rampageMeter = 0;

        enemySpawnTimer = new Timer(1);

        levelStartTimer = new Timer(1);
        levelStartTimer.start();
        starting = true;
    }

    public void actuallyStartLevel() {
        enemySpawnTimer.start();
        resetEnemySpawnTimer();
        spawnEnemy();
        enemySpawnTimer.start();

        player.setControllable(true);
        starting = false;
    }

    public void resetEnemySpawnTimer() {
        enemySpawnTimer.setTime(Math.random() * 3 + 1);
    }

    public void findNextLevel(Level l) {
        Level goingTo = l.getNextLevel();
        if(goingTo == null) {
            winGame();
        } else {
            levelStartRoutine(goingTo);
        }
    }

    public void winGame() {
        GameAssets.calmSong.stop();
        GameAssets.fightSong.stop();
        CNGE.setScene(new MenuScene());
    }

    public void loseLevel() {
        cameFromWin = false;
        lost = true;
        player = null;
        deathTimer.start();
    }

    public void winLevel() {
        cameFromWin = true;
        won = true;
        winTimer.start();
    }

    public void spawnEnemy() {
        if(numEnemies < maxEnemies) {
            Map.Line[] lines = currentMap.getSpawnerLines();
            int len = lines.length;
            Map.Line sel = lines[(int)(Math.random() * lines.length)];

            float radius = player.radius + (float)(Math.random() * player.radius) - player.radius / 2;

            addEnemy(radius, sel);
        }
    }

    public void addEnemy(float r, Map.Line l) {
        enemies[enemyPlace] = new Enemy(r, l, enemyPlace);
        ++numEnemies;
        if(numEnemies < maxEnemies) {
            while(enemies[enemyPlace = (enemyPlace + 1) % maxEnemies] != null);
        }
    }

    public void removeEnemy(int i) {
        enemies[i] = null;
        --numEnemies;
    }

    @Override
    public void windowResized(int w, int h) {
        GameAssets.drawBuffer.resize(2 * w, 2 * h);
        GameAssets.healthBuffer.resize((int)(w * 0.5625 * 2), (int)(h * 0.125 * 2));
        GameAssets.mapBuffer.resize(2 * w, 2 * h);

        /* render mask texture on health bar */
        GameAssets.healthBuffer.enableTexture();
        window.clear(0f, 0f, 0f, 1f);

        camera.getTransform().setTranslation(0, 0);
        camera.getTransform().setScale(1, 1);
        camera.setDims(144, 18);
        GameAssets.colorShader.enable();

        healthTransform.setTranslation(0, 9);//top box
        healthTransform.setSize(144, 9);
        GameAssets.colorShader.setUniforms(1, 1, 1, 1);
        GameAssets.colorShader.setMvp(camera.getMP(camera.getM(healthTransform)));
        GameAssets.rect.render();

        healthTransform.setTranslation(9, 0);//bottom box
        healthTransform.setSize(126, 9);
        GameAssets.colorShader.setUniforms(1, 1, 1, 1);
        GameAssets.colorShader.setMvp(camera.getMP(camera.getM(healthTransform)));
        GameAssets.rect.render();

        GameAssets.circleShader.enable();
        healthTransform.setTranslation(0, 0);//left circle
        healthTransform.setSize(18, 18);
        GameAssets.circleShader.setUniforms(1, 1, 1, 1);
        GameAssets.circleShader.setMvp(camera.getMP(camera.getM(healthTransform)));
        GameAssets.rect.render();

        healthTransform.setTranslation(126, 0);//right circle
        healthTransform.setSize(18, 18);
        GameAssets.circleShader.setUniforms(1, 1, 1, 1);
        GameAssets.circleShader.setMvp(camera.getMP(camera.getM(healthTransform)));
        GameAssets.rect.render();

        GameAssets.colorShader.enable();
        healthTransform.setTranslation(52, 12);//top middle box
        healthTransform.setSize(40, 6);
        GameAssets.colorShader.setUniforms(0, 0, 0, 1);
        GameAssets.colorShader.setMvp(camera.getMP(camera.getM(healthTransform)));
        GameAssets.rect.render();

        healthTransform.setTranslation(58, 6);//bottom middle box
        healthTransform.setSize(28, 6);
        GameAssets.colorShader.setUniforms(0, 0, 0, 1);
        GameAssets.colorShader.setMvp(camera.getMP(camera.getM(healthTransform)));
        GameAssets.rect.render();

        GameAssets.circleShader.enable();
        healthTransform.setTranslation(52, 6);//left middle circle
        healthTransform.setSize(12, 12);
        GameAssets.circleShader.setUniforms(0, 0, 0, 1);
        GameAssets.circleShader.setMvp(camera.getMP(camera.getM(healthTransform)));
        GameAssets.rect.render();

        healthTransform.setTranslation(80, 6);//right middle circle
        healthTransform.setSize(12, 12);
        GameAssets.circleShader.setUniforms(0, 0, 0, 1);
        GameAssets.circleShader.setMvp(camera.getMP(camera.getM(healthTransform)));
        GameAssets.rect.render();

        GameAssets.bloodFont.setColor(1, 1, 1, 1);
        GameAssets.bloodFont.setUpsideDown(true);
        GameAssets.bloodFont.render(BLOOD_TEXT, 72, 12, 0.5f, true);
        GameAssets.bloodFont.setUpsideDown(false);

        camera.defaultDims();
    }

    public void setSongUp() {
        songMixTimer.start();
        songMixStart = songMix;
        songMixEnd = 1;
        songChanging = true;
        rampageMode = true;
    }

    public void setSongDown() {
        songMixTimer.start();
        songMixStart = songMix;
        songMixEnd = 0;
        songChanging = true;
        rampageMode = false;
    }

    public void inccreaseRampage(float value) {
        rampageMeter += value;
    }

    @Override
    public void update() {
        rampageMeter -= RAMPAGE_DECAY * Loop.time;
        if(rampageMeter < 0) {
            rampageMeter = 0;
        }
        if(!songChanging) {
            if(rampageMode) {
                if(rampageMeter < 1.5) {
                    setSongDown();
                }
            } else {
                if(rampageMeter > 1.5) {
                    setSongUp();
                }
            }
        }

        if(songChanging) {
            if(songMixTimer.update(Loop.time)) {
                songChanging = false;
            }
            songMix = Timer.LINEAR.interpolate(songMixStart, songMixEnd, (songMixTimer.getAlong()));
        }

        GameAssets.calmSong.setVolume(1 - songMix);
        GameAssets.fightSong.setVolume(songMix);

        if(player != null) {
            player.update(currentMap);
        }

        for(int i = 0; i < maxEnemies; ++i) {
            if(enemies[i] != null) {
                enemies[i].update(currentMap);
            }
        }

        if(player != null && player.enemyUpdate(this, enemies)) {
            loseLevel();
        }

        if(player != null && player.winUpdate() && !won) {
            winLevel();
        }

        if(lost) {
            if(deathTimer.update(Loop.time)) {
                //reset this whole mf
                levelStartRoutine(currentLevel);
            } else {
                darken = Timer.COSINE.interpolate(0, 1.5, deathTimer.getAlong());
            }
        } else {
            if (enemySpawnTimer.continualUpdate(Loop.time)) {
                resetEnemySpawnTimer();
                spawnEnemy();
            }
        }

        if(won) {
            if(winTimer.update(Loop.time)) {
                findNextLevel(currentLevel);
            } else {
                darken = Timer.COSINE.interpolate(0, 1.5, winTimer.getAlong());
            }
        }

        if(player != null) {
            player.cameraOnThis();
        }

        if(starting) {
            if (!levelStartTimer.update(Loop.time)) {
                darken = (float) (1 - levelStartTimer.getAlong()) * 1.5f;
            } else {
                actuallyStartLevel();
            }
        }
    }

    @Override
    public void render() {
        /* get level mask */
        GameAssets.mapBuffer.enableTexture();
        window.clear(1, 1, 1, 1);
        Transform t = camera.getTransform();
        currentMap.render(t.x, t.y, t.getInverseWidth() + t.x, t.getInverseHeight() + t.y, true, false);

        GameAssets.drawBuffer.enableTexture();
        window.clear(0, 0, 0, 1);

        /* RENDER FLOOR */
        GameAssets.mapBuffer.getTexture().bind(0);
        GameAssets.floorTexture.bind(1);
        GameAssets.mapShader.enable();
        GameAssets.mapShader.setMvp(camera.ndcFullMatrix());
        GameAssets.mapShader.setUniforms(new Vector4f(1, 1, 0, 0), 1f, 1f, 1f, 0, 1, 1);
        GameAssets.rect.render();

        if(player != null) {
            player.render();
        }

        for(int i = 0; i < maxEnemies; ++i) {
            if(enemies[i] != null) {
                if(player != null) {
                    enemies[i].givePRadius(player.radius);
                }
                enemies[i].render();
            }
        }

        /* RENDER TRIANGLE WALLS */
        GameAssets.mapBuffer.getTexture().bind(0);
        GameAssets.bloodWallTexture.bind(1);
        GameAssets.mapShader.enable();
        GameAssets.mapShader.setMvp(camera.ndcFullMatrix());
        GameAssets.mapShader.setUniforms(new Vector4f(1, 1, 0, 0), 1f, 1f, 1f, 0, 1, 0);
        GameAssets.rect.render();

        currentMap.render(t.x, t.y, t.getInverseWidth() + t.x, t.getInverseHeight() + t.y, false, true);

        if(player != null) {
            player.arrowRender();
        }

        if(player != null) {
            currentMap.renderGatetext(player.x, player.y);
        }

        healthTransform.setTranslation(56, 4);
        healthTransform.setSize(144, 18);
        GameAssets.healthBuffer.getTexture().bind();

        GameAssets.healthShader.enable();
        Matrix4f model = camera.getM(healthTransform);
        GameAssets.healthShader.setUniforms(0f, 0f, 0f);
        GameAssets.healthShader.setModelMatrix(model);
        GameAssets.healthShader.setPlane(0, 0, 0, 0);
        GameAssets.healthShader.setMvp(camera.getMP(model));
        GameAssets.rect.render();
        if(player != null) {
            double hAlong = player.radius / currentMap.getBloodCost();
            if(hAlong > 1) {
                hAlong = 1;
            }
            model = camera.getM(healthTransform);
            GameAssets.healthShader.setModelMatrix(model);
            GameAssets.healthShader.setPlane(-1, 0, 0, (float)(hAlong * 144) + 56);
            if(hAlong == 1) {
                GameAssets.healthShader.setUniforms(0.85f, 0f, 0.96f);
            } else {
                GameAssets.healthShader.setUniforms(0.85f, 0f, 0f);
            }
            GameAssets.healthShader.setMvp(camera.getMP(model));
            GameAssets.rect.render();
        }

        if(lost) {
            GameAssets.gradientShader.enable();
            GameAssets.gradientShader.setUniforms(0, 0, 0, (darken - 0.5f), 0, 0, 0, darken);
            GameAssets.gradientShader.setMvp(camera.ndcFullMatrix());
            GameAssets.rect.render();
        } else if(starting) {
            GameAssets.gradientShader.enable();
            if(cameFromWin) {
                GameAssets.gradientShader.setUniforms(1, 1, 1, (darken - 0.5f), 1, 1, 1, darken);
            } else {
                GameAssets.gradientShader.setUniforms(0, 0, 0, (darken - 0.5f), 0, 0, 0, darken);
            }
            GameAssets.gradientShader.setMvp(camera.ndcFullMatrix());
            GameAssets.rect.render();
        } else if(won) {
            GameAssets.gradientShader.enable();
            GameAssets.gradientShader.setUniforms(1, 1, 1, (darken - 0.5f), 1, 1, 1, darken);
            GameAssets.gradientShader.setMvp(camera.ndcFullMatrix());
            GameAssets.rect.render();
        }


        /*render to gamebuffer*/
        CNGE.gameBuffer.enable();
        GameAssets.drawBuffer.getTexture().bind();
        GameAssets.textureShader.enable();
        GameAssets.textureShader.setMvp(CNGE.camera.ndcFullMatrix());
        GameAssets.rect.render();
    }

}
