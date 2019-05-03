package game;

import cnge.core.AssetBundle;
import cnge.core.Font;
import cnge.graphics.FrameBuffer;
import cnge.graphics.shape.*;
import cnge.graphics.sound.Sound;
import cnge.graphics.texture.MultisampleTexture;
import cnge.graphics.texture.Texture;
import game.shaders.*;

import static cnge.graphics.texture.TexturePreset.TP;

public class GameAssets extends AssetBundle {

    public static final int GAME_ASSETS_LOAD_NUMBER = 25;

    /*
     *
     */
    public static CircleShape circle;
    public static TriangleShader triangleShader;
    public static FrameBuffer drawBuffer;
    public static FrameBuffer healthBuffer;
    public static FrameBuffer healthBuffer2;
    public static FrameBuffer healthBuffer3;
    public static MTriangleShape mTri;
    public static ArrowShape arrowShape;
    public static HealthShader healthShader;
    public static Sound calmSong;
    public static Sound menuSong;
    public static Sound fightSong;
    public static FrameBuffer mapBuffer;
    public static Texture bloodWallTexture;
    public static Texture floorTexture;
    public static MapShader mapShader;
    public static RectShape rect;
    public static TextureShader textureShader;
    public static CircleShader circleShader;
    public static ColorShader colorShader;
    public static BloodFont bloodFont;
    public static TileShader textShader;
    public static GradientShader gradientShader;
    public static FramebufferRect framebufferRect;
    public static Texture tutorialTexture;
    /*
     *
     */

    public GameAssets() {
        super(GAME_ASSETS_LOAD_NUMBER);
    }

    @Override
    public void loadRoutine() {
        doLoad(circle = new CircleShape(16));
        doLoad(triangleShader = new TriangleShader());
        doLoad(drawBuffer = new FrameBuffer(new Texture(1, 1), false));
        doLoad(healthBuffer = new FrameBuffer(new Texture(1, 1, TP().border()), false));
        doLoad(healthBuffer2 = new FrameBuffer(new Texture(1, 1, TP().border()), false));
        doLoad(healthBuffer3 = new FrameBuffer(new Texture(1, 1, TP().border()), false));
        doLoad(mTri = new MTriangleShape());
        doLoad(arrowShape = new ArrowShape());
        doLoad(healthShader = new HealthShader());
        doLoad(calmSong = new Sound("res/sounds/calm-song.wav"));
        doLoad(fightSong = new Sound("res/sounds/fight-song.wav"));
        doLoad(menuSong = new Sound("res/sounds/menu-song.wav"));
        doLoad(mapBuffer = new FrameBuffer(new Texture(1, 1, TP().linear()), false));
        doLoad(bloodWallTexture = new Texture("res/textures/bloodwall.png", TP().repeat().linear()));
        doLoad(floorTexture = new Texture("res/textures/floor.png", TP().repeat().linear()));
        doLoad(mapShader = new MapShader());
        doLoad(rect = new RectShape());
        doLoad(textureShader = new TextureShader());
        doLoad(circleShader = new CircleShader());
        doLoad(colorShader = new ColorShader());
        doLoad(bloodFont = new BloodFont());
        doLoad(textShader = new TileShader());
        doLoad(gradientShader = new GradientShader());
        doLoad(framebufferRect = new FramebufferRect());
        doLoad(tutorialTexture = new Texture("res/textures/tutorial.png", TP()));
    }

    @Override
    public void unloadRoutine() {
        doLoad(circle.destroy());
        doLoad(triangleShader.destroy());
        doLoad(drawBuffer.destroy());
        doLoad(mTri.destroy());
        doLoad(arrowShape.destroy());
    }

}
