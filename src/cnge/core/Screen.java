package cnge.core;

import cnge.core.interfaces.Framer;
import cnge.graphics.texture.Texture;
import cnge.graphics.texture.TexturePreset;

import static org.lwjgl.opengl.GL11.glViewport;

public class Screen extends CNGE{

    private static final int DOESNT_MATTER = 0;

    private int frameWidth;
    private int frameHeight;
    private int frameLimit;
    private boolean widthFull;

    /**
     * haha, you cannot call this contstructor
     */
    private Screen(Framer f, int l) {
        framer = f;
        frameLimit = l;
    }

    public static Screen makeAspectScreen() {
        return new Screen(ASPECT_FRAMER, DOESNT_MATTER);
    }

    public static Screen makeAdaptiveScreen(int limit) {
        return new Screen(ADAPTIVE_FRAMER, limit);
    }

    public static Screen makeStretchScreen() {
        return new Screen(STRETCH_FRAMER, DOESNT_MATTER);
    }

    public void reFrame(int w, int h) {
        //calculate frame height
        framer.reFrame(this, w, h);
        //then use it
        gameBuffer.resize(frameWidth, frameHeight);
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    private static final Framer ASPECT_FRAMER = (Screen s, int w, int h) -> {

        float windowRatio = (float)window.getWidth() / window.getHeight();

        float gameRatio = gameWidth / gameHeight;

        if(windowRatio < gameRatio) {
            s.widthFull = true;
            s.frameWidth = window.getWidth();
            s.frameHeight = (int)(window.getHeight() * (windowRatio/gameRatio));
        }else {
            s.widthFull = false;
            s.frameWidth = (int)(window.getWidth() * (gameRatio/windowRatio));
            s.frameHeight = window.getHeight();
        }
    };

    private static final Framer ADAPTIVE_FRAMER = (Screen s, int w, int h) -> {
        s.widthFull = false;

        float windowRatio = (float)window.getWidth() / window.getHeight();

        s.frameHeight = h;

        gameWidth = (int)Math.round(windowRatio * gameHeight);
        if(gameWidth > s.frameLimit) {
            gameWidth = s.frameLimit;
            s.frameWidth = (int)Math.round(window.getWidth() * ( (float)(gameWidth / gameHeight) / (windowRatio) ));
        }else {
            s.frameWidth = w;
        }
    };

    private static final Framer STRETCH_FRAMER = (Screen s, int w, int h) -> {
        s.frameHeight = window.getHeight();
        s.frameWidth = window.getWidth();
    };

    public void setScreenViewport() {
        if(widthFull) {
            glViewport(0, window.getHeight() / 2 - frameHeight / 2, frameWidth, frameHeight);
        } else {
            glViewport(window.getWidth() / 2 - frameWidth / 2, 0, frameWidth, frameHeight);
        }
    }
}
