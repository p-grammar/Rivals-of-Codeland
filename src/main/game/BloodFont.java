package game;

import cnge.core.CNGE;
import cnge.core.Font;
import cnge.graphics.Transform;
import org.joml.Vector4f;

public class BloodFont extends Font {

    private Transform t;

    private float r, g, b, a;
    private boolean upsideDown;

    public BloodFont() {
        super("res/font/font.bmp", "res/font/font.csv");
        t = new Transform();
    }

    public void setColor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public void setUpsideDown(boolean u) {
        upsideDown = u;
    }

    @Override
    protected void charRender(int cx, int cy, float left, float right, float up, float down) {
        t.setSize(right - left, down - up);
        t.setTranslation(left, up);
        texture.bind();
        GameAssets.textShader.enable();
        Vector4f vec = texture.getDims(cx, cy);
        GameAssets.textShader.setUniforms(vec, r, g, b, a);
        GameAssets.textShader.setMvp(camera.getMVP(camera.getM(t)));
        if(upsideDown) {
            GameAssets.framebufferRect.render();
        } else {
            GameAssets.rect.render();
        }
    }

}
