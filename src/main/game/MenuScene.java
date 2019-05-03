package game;

import cnge.core.*;
import cnge.graphics.Camera;
import cnge.graphics.Transform;
import cnge.graphics.shape.RectShape;
import cnge.graphics.texture.Texture;
import org.joml.Vector2f;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.*;

public class MenuScene extends Scene {

	public MenuScene(Class<? extends AssetBundle>... unloadFlags) {
		super(unloadFlags, GameLoadScreen.class, GameAssets.class);
	}

	private static final char[] TITLE_TEXT = {'B','L','O','O','D',' ','G','A','T','E','S'};
	private static final char[] PLAY_TEXT = {'P','L','A','Y'};
	private static final char[] HELP_TEXT = {'H','E','L','P'};

	private Transform renderT;

	private Button playButton;
	private Button helpButton;
	private Button helpPanel;

	private float rotation;
	private float speed;
	private float distance;

	private boolean playing;
	private Timer playTimer;
	private float darken;

	private boolean cardUp;

	@Override
	public void sceneStart() {
		CNGE.window.showCursor();
		renderT = new Transform();
		playButton = new Button(38,  56, 180, 32, GameAssets.healthBuffer,   true);
		helpButton = new Button(38, 104, 180, 32, GameAssets.healthBuffer2,  true);
		helpPanel  = new Button(8, 8, 240, 128, GameAssets.healthBuffer3, false);

		rotation = 0;
		distance = 64;
		speed = CNGE.PI / 4;

		playing = false;
		playTimer = new Timer(2);

		cardUp = false;

		GameAssets.menuSong.play(true);
	}

	@Override
	public void windowResized(int w, int h) {
		GameAssets.drawBuffer.resize(2 * w, 2 * h);
		GameAssets.healthBuffer.resize((int)(w * 0.5625 * 2), (int)(h * 0.125 * 2));
		GameAssets.healthBuffer2.resize((int)(w * 0.703125 * 2), (int)(h * 0.222222222222 * 2));
		GameAssets.healthBuffer3.resize((int)(w * 0.9375 * 2), (int)(h * 0.888888888889 * 2));

		camera.getTransform().setScale(1, 1);
		camera.getTransform().setTranslation(0, 0);

		camera.setDims(180, 32);

		/* HEALTH BUFFER 1 */
		GameAssets.healthBuffer.enableTexture();
		window.clear(0, 0, 0, 0);

		GameAssets.colorShader.enable();

		renderT.setSize(180, 16);
		renderT.setTranslation(0, 16);
		GameAssets.colorShader.setMvp(camera.getMVP(camera.getM(renderT)));
		GameAssets.colorShader.setUniforms(1, 1, 1, 1);
		GameAssets.rect.render();

		renderT.setSize(148, 16);
		renderT.setTranslation(16, 0);
		GameAssets.colorShader.setMvp(camera.getMVP(camera.getM(renderT)));
		GameAssets.colorShader.setUniforms(1, 1, 1, 1);
		GameAssets.rect.render();

		GameAssets.circleShader.enable();

		renderT.setSize(32, 32);
		renderT.setTranslation(0, 0);
		GameAssets.circleShader.setMvp(camera.getMVP(camera.getM(renderT)));
		GameAssets.circleShader.setUniforms(1, 1, 1, 1);
		GameAssets.rect.render();

		renderT.setSize(32, 32);
		renderT.setTranslation(148, 0);
		GameAssets.circleShader.setMvp(camera.getMVP(camera.getM(renderT)));
		GameAssets.circleShader.setUniforms(1, 1, 1, 1);
		GameAssets.rect.render();

		GameAssets.colorShader.enable();

		renderT.setSize(60, 8);
		renderT.setTranslation(60, 16);
		GameAssets.colorShader.setMvp(camera.getMVP(camera.getM(renderT)));
		GameAssets.colorShader.setUniforms(0, 0, 0, 1);
		GameAssets.rect.render();

		renderT.setSize(44, 8);
		renderT.setTranslation(68, 8);
		GameAssets.colorShader.setMvp(camera.getMVP(camera.getM(renderT)));
		GameAssets.colorShader.setUniforms(0, 0, 0, 1);
		GameAssets.rect.render();

		GameAssets.circleShader.enable();

		renderT.setSize(16, 16);
		renderT.setTranslation(60, 8);
		GameAssets.circleShader.setMvp(camera.getMVP(camera.getM(renderT)));
		GameAssets.circleShader.setUniforms(0, 0, 0, 1);
		GameAssets.rect.render();

		renderT.setSize(16, 16);
		renderT.setTranslation(104, 8);
		GameAssets.circleShader.setMvp(camera.getMVP(camera.getM(renderT)));
		GameAssets.circleShader.setUniforms(0, 0, 0, 1);
		GameAssets.rect.render();

		GameAssets.bloodFont.setColor(1, 1, 1, 1);
		GameAssets.bloodFont.setUpsideDown(true);
		GameAssets.bloodFont.render(PLAY_TEXT, 90, 16, 1, true);
		GameAssets.bloodFont.setUpsideDown(false);

		/* HEALTH BUFFER 2 */
		GameAssets.healthBuffer2.enableTexture();
		window.clear(0, 0, 0, 0);

		GameAssets.colorShader.enable();

		renderT.setSize(180, 16);
		renderT.setTranslation(0, 16);
		GameAssets.colorShader.setMvp(camera.getMP(camera.getM(renderT)));
		GameAssets.colorShader.setUniforms(1, 1, 1, 1);
		GameAssets.rect.render();

		renderT.setSize(148, 16);
		renderT.setTranslation(16, 0);
		GameAssets.colorShader.setMvp(camera.getMP(camera.getM(renderT)));
		GameAssets.colorShader.setUniforms(1, 1, 1, 1);
		GameAssets.rect.render();

		GameAssets.circleShader.enable();

		renderT.setSize(32, 32);
		renderT.setTranslation(0, 0);
		GameAssets.circleShader.setMvp(camera.getMP(camera.getM(renderT)));
		GameAssets.circleShader.setUniforms(1, 1, 1, 1);
		GameAssets.rect.render();

		renderT.setSize(32, 32);
		renderT.setTranslation(148, 0);
		GameAssets.circleShader.setMvp(camera.getMP(camera.getM(renderT)));
		GameAssets.circleShader.setUniforms(1, 1, 1, 1);
		GameAssets.rect.render();

		GameAssets.colorShader.enable();

		renderT.setSize(60, 8);
		renderT.setTranslation(60, 16);
		GameAssets.colorShader.setMvp(camera.getMP(camera.getM(renderT)));
		GameAssets.colorShader.setUniforms(0, 0, 0, 1);
		GameAssets.rect.render();

		renderT.setSize(44, 8);
		renderT.setTranslation(68, 8);
		GameAssets.colorShader.setMvp(camera.getMP(camera.getM(renderT)));
		GameAssets.colorShader.setUniforms(0, 0, 0, 1);
		GameAssets.rect.render();

		GameAssets.circleShader.enable();

		renderT.setSize(16, 16);
		renderT.setTranslation(60, 8);
		GameAssets.circleShader.setMvp(camera.getMP(camera.getM(renderT)));
		GameAssets.circleShader.setUniforms(0, 0, 0, 1);
		GameAssets.rect.render();

		renderT.setSize(16, 16);
		renderT.setTranslation(104, 8);
		GameAssets.circleShader.setMvp(camera.getMP(camera.getM(renderT)));
		GameAssets.circleShader.setUniforms(0, 0, 0, 1);
		GameAssets.rect.render();

		GameAssets.bloodFont.setColor(1, 1, 1, 1);
		GameAssets.bloodFont.setUpsideDown(true);
		GameAssets.bloodFont.render(HELP_TEXT, 90, 16, 1, true);
		GameAssets.bloodFont.setUpsideDown(false);

		/* HEALTH BUFFER 3 */
		GameAssets.healthBuffer3.enableTexture();
		camera.setDims(240, 128);
		window.clear(0, 0, 0, 0);

		GameAssets.colorShader.enable();

		renderT.setSize(240, 112);
		renderT.setTranslation(0, 8);
		GameAssets.colorShader.setMvp(camera.getMP(camera.getM(renderT)));
		GameAssets.colorShader.setUniforms(1, 1, 1, 1);
		GameAssets.rect.render();

		renderT.setSize(224, 8);
		renderT.setTranslation(8, 0);
		GameAssets.colorShader.setMvp(camera.getMP(camera.getM(renderT)));
		GameAssets.colorShader.setUniforms(1, 1, 1, 1);
		GameAssets.rect.render();

		renderT.setSize(224, 8);
		renderT.setTranslation(8, 120);
		GameAssets.colorShader.setMvp(camera.getMP(camera.getM(renderT)));
		GameAssets.colorShader.setUniforms(1, 1, 1, 1);
		GameAssets.rect.render();

		GameAssets.circleShader.enable();

		renderT.setSize(16, 16);
		renderT.setTranslation(0, 0);
		GameAssets.circleShader.setMvp(camera.getMP(camera.getM(renderT)));
		GameAssets.circleShader.setUniforms(1, 1, 1, 1);
		GameAssets.rect.render();

		renderT.setTranslation(0, 112);
		GameAssets.circleShader.setMvp(camera.getMP(camera.getM(renderT)));
		GameAssets.circleShader.setUniforms(1, 1, 1, 1);
		GameAssets.rect.render();

		renderT.setTranslation(224, 0);
		GameAssets.circleShader.setMvp(camera.getMP(camera.getM(renderT)));
		GameAssets.circleShader.setUniforms(1, 1, 1, 1);
		GameAssets.rect.render();

		renderT.setTranslation(224, 112);
		GameAssets.circleShader.setMvp(camera.getMP(camera.getM(renderT)));
		GameAssets.circleShader.setUniforms(1, 1, 1, 1);
		GameAssets.rect.render();

		GameAssets.bloodFont.setColor(1, 1, 1, 1);
		GameAssets.bloodFont.setUpsideDown(true);
		GameAssets.bloodFont.render(PLAY_TEXT, 90, 16, 1, true);
		GameAssets.bloodFont.setUpsideDown(false);

		camera.defaultDims();
	}

	@Override
	public void update() {
		camera.getTransform().setScale(1, 1);
		camera.getTransform().setTranslation(0, 0);

		rotation += Loop.time * speed;
		rotation %= 2 * CNGE.PI;

		Vector2f mouse = window.getMouseCoords(camera);
		float mx = mouse.x;
		float my = mouse.y;
		boolean down = window.mousePressed(GLFW_MOUSE_BUTTON_1);

		if(cardUp) {
			if(helpPanel.update(mx, my, down)) {
				cardUp = false;
			}
		} else {
			if (playButton.update(mx, my, down)) {
				if (!playing) {
					playing = true;
					playTimer.start();
				}
			}
			if(helpButton.update(mx, my, down)) {
				cardUp = true;
			}
		}

		if(playing) {
			if(playTimer.update(Loop.time)) {
				GameAssets.menuSong.stop();
				CNGE.setScene(new GameScene());
			} else {
				darken = Timer.LINEAR.interpolate(0, 1.5, playTimer.getAlong());
			}
		}
	}

	@Override
	public void render() {
		GameAssets.drawBuffer.enable();

		window.clear(0, 0, 0, 1);

		renderT.setTranslation((float)Math.cos(rotation) * distance - gameWidth / 2, (float)Math.sin(rotation) * distance - gameHeight / 2);
		renderT.setSize(gameWidth * 2, gameHeight * 2);
		GameAssets.bloodWallTexture.bind();
		GameAssets.textureShader.enable();
		GameAssets.textureShader.setMvp(camera.getMVP(camera.getM(renderT)));
		GameAssets.rect.render();

		GameAssets.bloodFont.setColor(0.36f, 0, 0, 1f);
		GameAssets.bloodFont.render(TITLE_TEXT, 128, 24, 1.5f, true);

		playButton.render();
		helpButton.render();

		if(cardUp) {
			helpPanel.render();
			GameAssets.tutorialTexture.bind();
			GameAssets.textureShader.enable();
			GameAssets.textureShader.setMvp(camera.getMVP(camera.getM(camera.getTransform())));
			GameAssets.rect.render();
		}

		if(playing) {
			GameAssets.gradientShader.enable();
			GameAssets.gradientShader.setUniforms(0, 0, 0, (darken - 0.5f), 0, 0, 0, darken);
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
