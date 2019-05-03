package cnge.graphics.shape;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class MTriangleShape extends Shape {

    public MTriangleShape() {
        init(
        1,
            new float[]{
                0, 0, 0,
                0, 0, 0,
                0, 0, 0
            },
            new int[]{
                0, 1, 2
            },
            GL_TRIANGLES
        );
    }
}
