package game;

public class Level {

    private int levelID;

    private Level nextLevel;
    private OnLoad onLoad;

    public Level(int i, OnLoad o, Level l) {
        levelID = 0;
        onLoad = o;
        nextLevel = l;
        levelID = i;
    }

    public Level getNextLevel() {
        return nextLevel;
    }

    public Map load() {
        return onLoad.loadMap();
    }

    public int getLevel() {
        return levelID;
    }

    interface OnLoad {
        Map loadMap();
    }

}
