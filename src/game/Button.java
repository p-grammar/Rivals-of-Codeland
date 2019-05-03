package game;

import cnge.core.CNGE;
import cnge.graphics.FrameBuffer;
import cnge.graphics.Transform;


public class Button {

    Transform t;
    FrameBuffer renderBuffer;

    boolean hovered;
    boolean pressed;
    boolean locked;

    boolean downLock;

    boolean clickDisplay;

    public Button(float x, float y, float w, float h, FrameBuffer fb, boolean c) {
        t = new Transform(x, y, w, h);
        hovered = false;
        pressed = false;
        locked = false;
        renderBuffer = fb;
        clickDisplay = c;
    }

    /**
     * @return tru if pressed
     */
    public boolean update(float mx, float my, boolean down) {

        boolean hoverCheck = mx > t.x && mx < t.x + t.width && my > t.y && my < t.y + t.height;

        if(downLock){
            pressed = true;
            hovered = hoverCheck;
            if(!down) {
                downLock = false;
                pressed = false;
                if(hovered) {
                    return true;
                }
            }
        } else {
            if (hovered) {
                if (hoverCheck) {
                    if (down) {
                        if (!locked) {
                            downLock = true;
                        }
                    } else {
                        locked = false;
                    }
                } else {
                    hovered = false;
                }
            } else {
                if (hoverCheck) {
                    hovered = true;
                    if (down) {
                        locked = true;
                    }
                }
            }
        }

        return false;
    }

    public void render() {
        renderBuffer.getTexture().bind();
        GameAssets.healthShader.enable();
        GameAssets.healthShader.setMvp(CNGE.camera.getMP(CNGE.camera.getM(t)));

        if(clickDisplay) {
            if (hovered) {
                if (pressed) {
                    GameAssets.healthShader.setUniforms(0.3f, 0.3f, 0.3f);
                } else {
                    GameAssets.healthShader.setUniforms(0.85f, 0f, 0f);
                }
            } else {
                if (pressed) {
                    GameAssets.healthShader.setUniforms(0.3f, 0.3f, 0.3f);
                } else {
                    GameAssets.healthShader.setUniforms(0.4f, 0.4f, 0.4f);
                }
            }
        } else {
            GameAssets.healthShader.setUniforms(0.2f, 0.2f, 0.2f);
        }

        GameAssets.rect.render();

    }

}
