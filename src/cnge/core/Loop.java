package cnge.core;

import cnge.core.interfaces.SubLooper;

public class Loop<subLooper> extends CNGE {

    /*
     * the fps printer is above all
     */
    private static VoidCall fpsPrinter;

    /*
     * external time variables
     */
    public static int fps;
    public static double time;
    public static long nanos;

    /*
     * internal variables for the loop
     */
    private long usingFPS;
    private long last;
    private long lastSec;
    private int frames;
    private long now;
    private boolean lastResizing;

    /*
     * the things the loop does
     */
    private SubLooper update;
    private SubLooper render;

    private interface VoidCall {
        void call();
    }

    public static void setFpsPrinter() {
        fpsPrinter = debug ? PRINT_FPS : NO_PRINT_FPS;
    }

    private void beginLoop() {
        usingFPS = 1000000000 / framerate;
        last = System.nanoTime();
        lastSec = last;
        frames = 0;
        now = 0;
        lastResizing = true;
    }

    private boolean shouldDoFrame() {
        now = System.nanoTime();
        if(now-last > usingFPS) {
            nanos = (now-last);
            time = nanos/1000000000d;
            last = now;
            ++frames;
            return true;
        }
        return false;
    }

    private void checkSecondHasPassed() {
        if(now-lastSec > 1000000000) {
            fps = frames;
            frames = 0;
            lastSec = now;
            fpsPrinter.call();
        }
    }

    public void setUpdateRender(SubLooper u, SubLooper r) {
        update = u;
        render = r;
    }

    public void run() {
        beginLoop();
        while(!window.shouldClose()) {
           if(shouldDoFrame()) {
               window.pollEvents();
               if(!window.getResizing()) {
                   if(lastResizing) {
                       screen.reFrame(window.getWidth(), window.getHeight());
                       scene.windowResized(screen.getFrameWidth(), screen.getFrameHeight());
                   }
                   CNGE.updateRender.loop(update, render);
               }
               lastResizing = window.getResizing();
               window.resetResizing();
               window.swap();
           }
           checkSecondHasPassed();
        }
        audio.destroy();
    }

    public static final VoidCall PRINT_FPS = () -> System.out.println(fps);


    public static final VoidCall NO_PRINT_FPS = () -> {};

}
