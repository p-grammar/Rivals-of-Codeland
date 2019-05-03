package game;

import cnge.core.CNGE;
import cnge.core.LoadScreen;
import cnge.graphics.Camera;
import cnge.graphics.Shader;
import cnge.graphics.Window;
import cnge.graphics.shape.RectShape;
import game.shaders.ColorShader;

public class GameLoadScreen extends LoadScreen {

    private ColorShader colorShader;
    private RectShape rect;
    private Camera camera;

    public GameLoadScreen(ColorShader cs, RectShape rs, Camera c) {
        colorShader = cs;
        rect = rs;
        camera = c;
    }

    //TOP LEFT
    //60, 50
    //64, 54

    //BOTTOM LEFT
    //60, 94
    //64, 90

    //TOP RIGHT
    //196, 50
    //192, 54

    //BOTTOM RIGHT
    //196, 64
    //192, 90

    @Override
    protected void loadRender(int along, int total) {
        CNGE.window.pollEvents();

        Window.clear(0, 0, 0, 1);

        colorShader.enable();

        colorShader.setUniforms(1, 1, 1, 1);

        colorShader.setMvp(camera.getMVP(camera.getMBounds(60, 64, 50, 94)));
        rect.render();

        colorShader.setMvp(camera.getMVP(camera.getMBounds(60, 196, 50, 54)));
        rect.render();

        colorShader.setMvp(camera.getMVP(camera.getMBounds(60, 196, 90, 94)));
        rect.render();

        colorShader.setMvp(camera.getMVP(camera.getMBounds(192, 196, 50, 94)));
        rect.render();

        colorShader.setMvp(camera.getMVP(camera.getMBounds(64, 64 + (128 * ((float)along / total)), 54, 90)));
        rect.render();

        Shader.disable();
    }

}
