package game;

import cnge.core.CCD;
import cnge.core.CNGE;
import cnge.graphics.Transform;
import game.GameAssets;
import game.GameAssets;
import org.joml.Vector4f;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class Map {

    /* statics */

    public static final int RECT_TYPE_BOUNDING_BOX = 0;
    public static final int RECT_TYPE_PLAYER_SPAWN = 1;
    public static final int TRIANGLE_TYPE_REGULAR = 2;
    public static final int LINE_TYPE_WALL = 3;
    public static final int LINE_TYPE_GATE = 4;
    public static final int LINE_TYPE_SPAWNER = 5;


    public static final char[][] classIdentifiers = {
        {'#','f','f','f'},//bounding box
        {'#','f','0','f'},//player spawn
        {'b','l','u','e'},//regular triangle
        {'#','0','0','0'},//regular wall
        {'r','e','d'},//end gate
        {'l','i','m','e'}//end gate
    };

    public static final int X0 = 0;
    public static final int Y0 = 1;
    public static final int X1 = 2;
    public static final int Y1 = 3;
    public static final int X2 = 4;
    public static final int Y2 = 5;

    private static final char[] BRACKET_FLAG = {'}'};
    private static final char[] DOT_FLAG = {'.'};
    private static final char[] STYLE_FLAG = {'s','t','y','l','e'};
    private static final char[] FILL_FLAG = {'f','i','l','l'};
    private static final char[] STROKE_FLAG = {'s','t','r','o','k','e'};
    private static final char[] RECT_FLAG = {'r','e','c','t'};
    private static final char[] WIDTH_FLAG = {'w','i','d','t','h'};
    private static final char[] HEIGHT_FLAG = {'h','e','i','g','h','t'};
    private static final char[] CLASS_FLAG = {'c','l','a','s','s'};
    private static final char[] POINTS_FLAG = {'p','o','i','n','t','s'};
    private static final char[] POLYGON_FLAG = {'p','o','l','y','g','o','n'};
    private static final char[] LINE_FLAG = {'l','i','n','e'};
    private static final char[] X1_FLAG = {'x','1'};

    protected static Transform lineTransform;
    protected static Transform boundsTransform;
    /* map internal stuff */

    public static class Triangle {
        public int index;
        public float[] points;
        public int type;

        public Triangle(float x0, float y0, float x1, float y1, float x2, float y2, int t) {
            points = new float[]{x0, y0, x1, y1, x2, y2};
            type = t;
        }

        public Triangle() {
            points = new float[6];
            type = 0;
        }
    }

    public static class Line {
        public CCD.Line line;
        public int index;
        public int type;

        public Line(CCD.Line l, int t) {
            line = l;
            type = t;
        }

        public Line() {
            line = new CCD.Line(0, 0, 0, 0);
            type = 0;
        }
    }

    private static class ReadStruct {
        public boolean success;
        public int mode;
        public int index;
        public int index2;
        public StringBuilder builder;
        public float[] tempList;

        public int[] listIndices;

        public ReadStruct() {
            mode = 0;
            index = 0;
            index2 = 0;
            builder = new StringBuilder();
            tempList = new float[6];
        }
    }

    private int mapWidth;
    private int mapHeight;

    private int zonesWide;
    private int zonesTall;

    private float zoneWidth;
    private float zoneHeight;

    private Triangle[][][] triangleZones;
    private Line[][][] lineZones;

    private Triangle[] triangles;
    private Line[] lines;

    private int numTriangles;
    private int numLines;

    /* gameplay map stuff for BLOOD */

    private Line gateLine;
    private Line[] spawnerLines;
    private int numSpawners;

    private float playerStartX;
    private float playerStartY;

    private int bloodCost;
    private char[] gateText;

    private int maxEnemies;
    private int startSize;

    public Map(int w, int h, int zw, int zt, Triangle[] ts, Line[] ls) {
        init(w, h, zw, zt, ts, ls);
    }

    public Map(int zw, int zt, String map, int bc, int mx, int sz) {
        try {
            FileInputStream input = new FileInputStream(new File(map));
            //stuff we get out
            int currentTriangle = 0;
            int currentLine = 0;
            int nt = 0;
            int nl = 0;
            Triangle[] ts = null;
            Line[] ls = null;
            int w = 0;
            int h = 0;

            bloodCost = bc;
            maxEnemies = mx;
            startSize = sz;
            String tempText = "Blood Cost: " + bc;
            gateText = tempText.toCharArray();

            int currentClass = 0;
            int[] classAssociated = new int[20];

            ReadStruct rs = new ReadStruct();
            int current;

            //go thru and find number of lines and triangles
            while((current = input.read()) != -1) {
                switch (rs.mode) {
                    case 0:
                        dualFlag(current, POLYGON_FLAG, LINE_FLAG, rs, 1, 2);
                        break;
                    case 1:
                        ++nt;
                        rs.mode = 0;
                        break;
                    case 2:
                        ++nl;
                        rs.mode = 0;
                        break;
                }
            }

            ts = new Triangle[nt];
            ls = new Line[nl];

            input.close();
            input = new FileInputStream(new File(map));
            rs = new ReadStruct();
            rs.mode = -15;

            int counter = 0;

            while((current = input.read()) != -1) {
                switch(rs.mode) {

                    case -15:
                        readFlag(current, STYLE_FLAG, rs, -14);
                        break;
                    case -14:
                        dualFlag(current, DOT_FLAG, STYLE_FLAG, rs, -13, -10);
                        break;
                    case -13:
                        currentClass = getIntEnd(current, '{', ',', rs, -12);
                        if(rs.success) {
                            ++counter;
                            prepareListIndices(3, rs);
                        }
                        break;
                    case -12:
                        int sel = listFlagSelect(current, rs, new int[]{-11, -11, -14}, STROKE_FLAG, FILL_FLAG, BRACKET_FLAG);
                        if(rs.success && sel < 2) {
                            prepareListIndices(classIdentifiers.length, rs);
                        }
                        break;
                    case -11:
                        int gameType = listFlag(current, classIdentifiers, rs, -12);
                        if(rs.success) {
                            classAssociated[currentClass] = gameType;
                            prepareListIndices(3, rs);
                        }
                        break;

                    case -10: //rectangle finding goodness
                        dualFlag(current, RECT_FLAG, POLYGON_FLAG, rs, -9, 8);
                        break;
                    case -9: //determine rect class
                        readFlag(current, CLASS_FLAG, rs, -8);
                        break;
                    case -8:
                        goUntil(current, '"', rs, -7);
                        break;
                    case -7: //determine whether bounding box or spawner
                        int gotten = getInt(current, '"', rs, -7);
                        int rectClass = classAssociated[gotten];
                        if(rs.success) {
                            if(rectClass == RECT_TYPE_BOUNDING_BOX) {
                                rs.mode = 1;
                            }else if(rectClass == RECT_TYPE_PLAYER_SPAWN) {
                                rs.mode = -6;
                            }
                        }
                        char c = (char)current;
                        break;

                    case -6: //getting the player spawn rect
                        goUntil(current, 'x', rs, -5);
                        break;
                    case -5:
                        goUntil(current, '"', rs, -4);
                        break;
                    case -4:
                        playerStartX = getFloat(current, '"', rs, -3);
                        break;
                    case -3:
                        goUntil(current, 'y', rs, -2);
                        break;
                    case -2:
                        goUntil(current, '"', rs, -1);
                        break;
                    case -1:
                        playerStartY = getFloat(current, '"', rs, -10);
                        break;

                    case 1:
                        readFlag(current, WIDTH_FLAG, rs, 2);
                        break;
                    case 2:
                        goUntil(current, '"', rs, 3);
                        break;
                    case 3:
                        w = getInt(current, '"', rs, 4);
                        break;
                    case 4:
                        readFlag(current, HEIGHT_FLAG, rs, 5);
                        break;
                    case 5:
                        goUntil(current, '"', rs, 6);
                        break;
                    case 6:
                        h = getInt(current, '"', rs, -10);
                        if(rs.success) {
                            System.out.println(h);
                        }
                        break;

                    case 7: //find the next line or polygon
                        dualFlag(current, POLYGON_FLAG, LINE_FLAG, rs, 8, 13);
                        break;
                    case 8: //if we hit a polygon
                        dualFlag(current, CLASS_FLAG, POINTS_FLAG, rs, 9, 11);
                        if(rs.success) {
                            ts[currentTriangle] = new Triangle();
                        }
                        break;
                    case 9: //if the polygon has a class
                        goUntil(current, '"', rs, 10);
                        break;
                    case 10: //read class
                        int classNo = getInt(current, '"', rs, 11);
                        if(rs.success) {
                            ts[currentTriangle].type = classAssociated[classNo];
                        }
                        break;
                    case 11: //points time
                        goUntil(current, '"', rs, 12);
                        break;
                    case 12: //read points
                        ts[currentTriangle].points = getFloatList(current, '"', ' ', rs, 7);
                        if(rs.success) {
                            ++currentTriangle;
                        }
                        break;
                    case 13:
                        readFlag(current, CLASS_FLAG, rs, 14);
                        if(rs.success) {
                            ls[currentLine] = new Line();
                        }
                        break;
                    case 14:
                        goUntil(current, '"', rs, 15);
                        break;
                    case 15:
                        classNo = getInt(current, '"', rs, 16);
                        if(rs.success) {
                            ls[currentLine].type = classAssociated[classNo];
                            if(ls[currentLine].type == LINE_TYPE_GATE) {
                                gateLine = ls[currentLine];
                            }
                        }
                        break;
                    case 16: //if we hit a line
                        readFlag(current, X1_FLAG, rs, 17);
                        break;
                    case 17:
                        goUntil(current, '"', rs, 18);
                        break;
                    case 18:
                        ls[currentLine].line.x0 = getFloat(current, '"', rs, 19);
                        break;
                    case 19:
                        goUntil(current, '"', rs, 20);
                        break;
                    case 20:
                        ls[currentLine].line.y0 = getFloat(current, '"', rs, 21);
                        break;
                    case 21:
                        goUntil(current, '"', rs, 22);
                        break;
                    case 22:
                        ls[currentLine].line.x1 = getFloat(current, '"', rs, 23);
                        break;
                    case 23:
                        goUntil(current, '"', rs, 24);
                        break;
                    case 24:
                        ls[currentLine].line.y1 = getFloat(current, '"', rs, 7);
                        if(rs.success) {
                            ++currentLine;
                        }
                        break;
                }
            }

            input.close();

            init(w, h, zw, zt, ts, ls);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void readFlag(int current, char[] flag, ReadStruct rs, int out) {
        rs.success = false;
        if(current == flag[rs.index]) {
            if(++rs.index == flag.length) {
                rs.index = 0;
                rs.mode = out;
                rs.success = true;
            }
        } else {
            rs.index = 0;
        }
    }

    private void dualFlag(int current, char[] flag, char[] flag2, ReadStruct rs, int out, int out2) {
        rs.success = false;
        if(current == flag[rs.index]) {
            if(++rs.index == flag.length) {
                rs.index = 0;
                rs.mode = out;
                rs.success = true;
            }
        } else {
            rs.index = 0;
        }

        if(current == flag2[rs.index2]) {
            if(++rs.index2 == flag2.length) {
                rs.index2 = 0;
                rs.mode = out2;
                rs.success = true;
            }
        } else {
            rs.index2 = 0;
        }
    }

    private void prepareListIndices(int len, ReadStruct rs) {
        rs.listIndices = new int[len];
    }

    private int listFlag(int current, char[][] flags, ReadStruct rs, int out) {
        rs.success = false;
        int len = rs.listIndices.length;
        for(int i = 0; i < len; ++i) {
            if (current == flags[i][rs.listIndices[i]]) {
                if (++rs.listIndices[i] == flags[i].length) {
                    rs.mode = out;
                    rs.success = true;
                    return i;
                }
            } else {
                rs.listIndices[i] = 0;
            }
        }
        return -1;
    }

    private int listFlagSelect(int current, ReadStruct rs, int[] out, char[]... flags) {
        rs.success = false;
        int len = rs.listIndices.length;
        for(int i = 0; i < len; ++i) {
            if (current == flags[i][rs.listIndices[i]]) {
                if (++rs.listIndices[i] == flags[i].length) {
                    rs.mode = out[i];
                    rs.success = true;
                    return i;
                }
            } else {
                rs.listIndices[i] = 0;
            }
        }
        return -1;
    }

    private void goUntil(int current, char u, ReadStruct rs, int out) {
        rs.success = false;
        if(current == u) {
            rs.mode = out;
            rs.success = true;
        }
    }

    private int getInt(int current, char end, ReadStruct rs, int out) {
        rs.success = false;
        if(current == end) {
            rs.mode = out;
            int ret = Integer.parseInt(rs.builder.toString());
            rs.builder = new StringBuilder();
            rs.success = true;
            return ret;
        } else {
            if(current > 47 && current < 58) {
                rs.builder.append((char) current);
            }
            return 0;
        }
    }

    private int getIntEnd(int current, char end, char end2, ReadStruct rs, int out) {
        rs.success = false;
        if(current == end || current == end2) {
            rs.mode = out;
            int ret = Integer.parseInt(rs.builder.toString());
            rs.builder = new StringBuilder();
            rs.success = true;
            return ret;
        } else {
            if(current > 47 && current < 58) {
                rs.builder.append((char) current);
            }
            return 0;
        }
    }

    private float getFloat(int current, char end, ReadStruct rs, int out) {
        rs.success = false;
        if(current == end) {
            rs.mode = out;
            float ret = Float.parseFloat(rs.builder.toString());
            rs.builder = new StringBuilder();
            rs.success = true;
            return ret;
        } else {
            if(current == '.' || (current > 47 && current < 58)) {
                rs.builder.append((char) current);
            }
            return 0;
        }
    }

    private float[] getFloatList(int current, char end, char space, ReadStruct rs, int out) {
        rs.success = false;
        if(current == end || rs.index == 6) {
            float[] ret = rs.tempList;
            rs.tempList = new float[6];
            rs.index = 0;
            rs.builder = new StringBuilder();
            rs.mode = out;
            rs.success = true;
            return ret;
        } else {
            if(current == space) {
                String kek = rs.builder.toString();
                rs.tempList[rs.index] = Float.parseFloat(rs.builder.toString());
                rs.builder = new StringBuilder();
                ++rs.index;
            } else {
                rs.builder.append((char)current);
            }
            return null;
        }
    }

    private void init(int w, int h, int zw, int zt, Triangle[] ts, Line[] ls) {
        triangles = ts;
        lines = ls;
        ArrayList<Triangle>[][] tempTriangleZones = new ArrayList[zw][zt];
        ArrayList<Line>[][] tempLineZones = new ArrayList[zw][zt];
        //init arraylists
        for(int i = 0; i < zw; ++i) {
            for(int j = 0; j < zt; ++j) {
                tempTriangleZones[i][j] = new ArrayList<>();
                tempLineZones[i][j] = new ArrayList<>();
            }
        }
        numTriangles = triangles.length;
        numLines = lines.length;

        mapWidth = w;
        mapHeight = h;
        zonesWide = zw;
        zonesTall = zt;

        zoneWidth = (float)(mapWidth / zonesWide);
        zoneHeight = (float)(mapHeight / zonesTall);

        //put triangles into zones
        for(int i = 0; i < numTriangles; ++i) {
            Triangle t = triangles[i];
            t.index = i;

            boolean[][] adds = new boolean[zonesWide][zonesTall];

            putIn(t, tempTriangleZones, adds, t.points[X0], t.points[Y0], t.points[X1], t.points[Y1]);
            putIn(t, tempTriangleZones, adds, t.points[X1], t.points[Y1], t.points[X2], t.points[Y2]);
            putIn(t, tempTriangleZones, adds, t.points[X2], t.points[Y2], t.points[X0], t.points[Y0]);
        }

        numSpawners = 0;

        //put lines into zones
        for(int i = 0; i < numLines; ++i) {
            Line l = lines[i];
            if(l.type == LINE_TYPE_SPAWNER) {
                ++numSpawners;
            }
            l.index = i;

            boolean[][] adds = new boolean[zonesWide][zonesTall];

            putIn(l, tempLineZones, adds, l.line.x0, l.line.y0, l.line.x1, l.line.y1);
        }

        //get spawner lines
        int tempIndex = 0;
        spawnerLines = new Line[numSpawners];
        for(int i = 0; i < numLines; ++i) {
            Line l = lines[i];
            if(l.type == LINE_TYPE_SPAWNER) {
                spawnerLines[tempIndex++] = l;
            }
        }

        triangleZones = new Triangle[zonesWide][zonesTall][];
        for(int i = 0; i < zonesWide; ++i) {
            for(int j = 0; j < zonesTall; ++j) {
                triangleZones[i][j] = tempTriangleZones[i][j].toArray(new Triangle[0]);
            }
        }

        lineZones = new Line[zonesWide][zonesTall][];
        for(int i = 0; i < zonesWide; ++i) {
            for(int j = 0; j < zonesTall; ++j) {
                lineZones[i][j] = tempLineZones[i][j].toArray(new Line[0]);
            }
        }
    }

    private void putIn(Object t, ArrayList[][] put, boolean[][] check, float x0, float y0, float x1, float y1) {
        int[] bounds = getBoundsUnsorted(x0, y0, x1, y1);

        for(int j = bounds[0]; j <= bounds[1]; ++j) {
            for(int k = bounds[2]; k <= bounds[3]; ++k) {
                if(zoneInRange(j, k)) {
                    if (!check[j][k]) {
                        check[j][k] = true;
                        put[j][k].add(t);
                    }
                }
            }
        }
    }

    public int[] getBoundsUnsorted(float x0, float y0, float x1, float y1) {
        int zx0 = (int)(x0 / zoneWidth);
        int zy0 = (int)(y0 / zoneHeight);
        int zx1 = (int)(x1 / zoneWidth);
        int zy1 = (int)(y1 / zoneHeight);

        int right = Math.max(zx1, zx0);
        int left = Math.min(zx1, zx0);

        int down = Math.max(zy1, zy0);
        int up = Math.min(zy1, zy0);

        return new int[]{left, right, up, down};
    }

    public int[] getBoundsSorted(float l, float u, float r, float d) {
        int left = (int)(l / zoneWidth);
        int up = (int)(u / zoneHeight);
        int right = (int)(r / zoneWidth);
        int down = (int)(d / zoneHeight);

        return new int[]{left, right, up, down};
    }

    public boolean zoneInRange(float x, float y) {
        return (x >= 0 && x < zonesWide) && (y >= 0 && y < zonesTall);
    }

    public void render(float l, float u, float r, float d, boolean renderT, boolean renderL) {
        int[] bounds = getBoundsSorted(l, u, r, d);

        if(renderT) {
            renderInside(0, mapWidth, 0, mapHeight);
        }

        boolean[] alreadyT = new boolean[numTriangles];
        boolean[] alreadyL = new boolean[numLines];

        for(int i = bounds[0]; i <= bounds[1]; ++i) {
            for(int j = bounds[2]; j <= bounds[3]; ++j) {
                if(zoneInRange(i, j)) {
                    int num = 0;
                    if(renderT) {
                        Triangle[] tList = triangleZones[i][j];
                        num = tList.length;
                        for (int k = 0; k < num; ++k) {
                            Triangle tr = tList[k];
                            if (!alreadyT[tr.index]) {
                                renderTri(tr, i, j);
                                alreadyT[tr.index] = true;
                            }
                        }
                    }
                    if(renderL) {
                        Line[] lList = lineZones[i][j];
                        num = lList.length;
                        for (int k = 0; k < num; ++k) {
                            Line li = lList[k];
                            if (!alreadyL[li.index]) {
                                renderLin(li, i, j);
                                alreadyL[li.index] = true;
                            }
                        }
                    }
                }
            }
        }
    }

    public void renderGatetext(float px, float py) {
        CCD.Line l = gateLine.line;
        CCD.Vector v = new CCD.Vector(gateLine.line);
        float gateCenterX = v.getCenterX() + l.x0;
        float gateCenterY = v.getCenterY() + l.y0;
//        double dist = v.sub(p).length();

        double dist = Math.sqrt((gateCenterX - px) * (gateCenterX - px) + (gateCenterY - py) * (gateCenterY - py));

        dist = 64 - dist;
        dist /= 64.0;

        GameAssets.bloodFont.setColor(1f, 1f, 1f, (float) dist);
        GameAssets.bloodFont.render(gateText, gateCenterX, gateCenterY, 0.25f, true);
    }

    public static void setupBloodMaps() {
        lineTransform = new Transform();
        boundsTransform = new Transform();
    }

    private void renderTri(Triangle t, int zx, int zy) {
        GameAssets.triangleShader.enable();
        GameAssets.triangleShader.setMvp(CNGE.camera.getSMVP());
        GameAssets.triangleShader.setUniforms(1f, 1f, 1f, 1f, t.points);
        GameAssets.mTri.render();
    }

    private void renderLin(Line l, int zx, int zy) {
        CCD.Line line = l.line;
        CCD.Vector lv = new CCD.Vector(l.line);

        double dist = lv.length();
        double angle = lv.getAngle();
        double cx = lv.getCenterX() + line.x0;
        double cy = lv.getCenterY() + line.y0;

        CCD.Vector off = new CCD.Vector(0, 4f).rotate(angle - CNGE.PI);

        lineTransform.setSize((float)dist, 8f);
        lineTransform.setCenter((float)(cx + off.x), (float)(cy + off.y));
        lineTransform.rotation = (float)angle;

        if(l.type != LINE_TYPE_WALL) {
            GameAssets.gradientShader.enable();
            switch (l.type) {
                case LINE_TYPE_GATE:
                    GameAssets.gradientShader.setUniforms(0.5f, 0f, 0.5f, 0f, 0.5f, 0f, 0.5f, 1f);
                    break;
                case LINE_TYPE_SPAWNER:
                    GameAssets.gradientShader.setUniforms(0f, 0.5f, 0f, 0f, 0f, 0.5f, 0f, 1f);
                    break;
            }
            GameAssets.gradientShader.setMvp(CNGE.camera.getMVP(CNGE.camera.getM(lineTransform)));
            GameAssets.rect.render();
        }
    }

    private void renderInside(float left, float right, float up, float down) {
        boundsTransform.setSize(right - left, down - up);
        boundsTransform.setTranslation(left, up);
        GameAssets.colorShader.enable();
        GameAssets.colorShader.setUniforms(0, 0, 0, 1);
        GameAssets.colorShader.setMvp(CNGE.camera.getMVP(CNGE.camera.getM(boundsTransform)));
        GameAssets.rect.render();
    }

    /*
    private void renderOutside(float left, float right, float up, float down) {
        Transform ct = CNGE.camera.getTransform();
        float cw = ct.getInverseWidth();
        float ch = ct.getInverseHeight();
        float cl = ct.x - cw / 2;
        float cu = ct.y - ch / 2;
        float cr = ct.x + ct.width - cw / 2;
        float cd = ct.y + ct.height - ch / 2;

        float fw = left - cl;
        if(fw > 0) {
            boundsTransform.setSize(fw, ch);
            boundsTransform.setTranslation(ct.x, ct.y);
            subRenderOutside();
        }

        fw = cr - right;
        if(fw > 0) {
            boundsTransform.setSize(fw, ch);
            boundsTransform.setTranslation(right, ct.y);
            subRenderOutside();
        }

        fw = up - cu;
        if (fw > 0) {
            boundsTransform.setSize(right - left, fw);
            boundsTransform.setTranslation(left, ct.y);
            subRenderOutside();
        }

        fw = cd - down;
        if (fw > 0) {
            boundsTransform.setSize(right - left, fw);
            boundsTransform.setTranslation(left, down);
            subRenderOutside();
        }
    }*/

    private void subRenderOutside() {
        GameAssets.colorShader.enable();
        GameAssets.colorShader.setUniforms(1, 1, 1, 1);
        GameAssets.colorShader.setMvp(CNGE.camera.getMVP(CNGE.camera.getM(boundsTransform)));
        GameAssets.rect.render();
    }

    public Line[][][] getLineZones() {
        return lineZones;
    }

    public int getZonesWide() {
        return zonesWide;
    }

    public int getZonesTall() {
        return zonesTall;
    }

    public int getWidth() {
        return mapWidth;
    }

    public int getHeight() {
        return mapHeight;
    }

    public float getZoneWidth() {
        return zoneWidth;
    }

    public float getZoneHeight() {
        return zoneHeight;
    }

    public float getPlayerStartX() {
        return playerStartX;
    }

    public float getPlayerStartY() {
        return playerStartY;
    }

    public Line[] getLines() {
        return lines;
    }

    public Line[] getSpawnerLines() {
        return spawnerLines;
    }

    public int getBloodCost() {
        return bloodCost;
    }

    public Line getGateLine() {
        return gateLine;
    }

    public int getMaxEnemies() {
        return maxEnemies;
    }

    public int getStartSize() {
        return startSize;
    }

}
