package cnge.core;

public class CCD {

    public static class Line {
        public float x0, y0, x1, y1;

        public Line(float x0, float y0, float x1, float y1) {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
        }

        public Line(Line l) {
            x0 = l.x0;
            y0 = l.y0;
            x1 = l.x1;
            y1 = l.y1;
        }

        public Line() {
            x0 = 0;
            y0 = 0;
            x1 = 0;
            y1 = 0;
        }

        public void copy(Line l) {
            x0 = l.x0;
            x1 = l.x1;
            y0 = l.y0;
            y1 = l.y1;
        }

        public void add(Vector v) {
            x0 += v.x;
            x1 += v.x;
            y0 += v.y;
            y1 += v.y;
        }
    }

    public static class Vector {
        public float x;
        public float y;

        public Vector(float o, float a) {
            x = o;
            y = a;
        }

        public Vector(Line l) {
            x = l.x1 - l.x0;
            y = l.y1 - l.y0;
        }

        public Vector(Vector v) {
            x = v.x;
            y = v.y;
        }

        public double length() {
            return Math.sqrt(x * x + y * y);
        }

        public Vector normalize() {
            double len = length();
            x /= len;
            y /= len;
            return this;
        }

        public Vector add(Vector v) {
            x += v.x;
            y += v.y;
            return this;
        }

        public Vector sub(Vector v) {
            x -= v.x;
            y -= v.y;
            return this;
        }

        public Vector multiply(double s) {
            x *= s;
            y *= s;
            return this;
        }

        public Vector divide(double s) {
            x /= s;
            y /= s;
            return this;
        }

        public Vector rotate(double t) {
            double tempX = x * Math.cos(t) - y * Math.sin(t);
            y = (float)(x * Math.sin(t) + y * Math.cos(t));
            x = (float)tempX;
            return this;
        }

        public double getAngle() {
            return Math.atan2(y, x);
        }

        public float getCenterX() {
            return x / 2;
        }

        public float getCenterY() {
            return y / 2;
        }

        public Vector setLength(double l) {
            double len = length();
            x *= l / len;
            y *= l / len;
            return this;
        }
    }

    public static class Collision {
        public int info;
        public double t_;

        public Collision(int i, double t) {
            info = i;
            t_ = t;
        }

        public boolean collision() {
            return (info & 0x001) == 0x001;
        }

        public boolean collisionNormal() {
            return (info & 0x010) == 0x010;
        }

        public boolean collisionAntiNormal() {
            return (info & 0x100) == 0x100;
        }
    }

    private static double hitPoint(Line move, Line wall) {
        return (
            (wall.y1 - wall.y0) * (move.x0 - wall.x0) - (wall.x1 - wall.x0) * (move.y0 - wall.y0)
        ) / (
            (wall.x1 - wall.x0) * (move.y1 - move.y0) - (wall.y1 - wall.y0) * (move.x1 - move.x0)
        );
    }

    public static double circleCollision(Line move, double cx, double cy, double r) {
        Line nm = new Line((float)(move.x0 - cx), (float)(move.y0 - cy), (float)(move.x1 - cx), (float)(move.y1 - cy));

        double c = nm.x0 * nm.x0 + nm.y0 * nm.y0 - r * r;
        double b = 2 * (nm.x1 * nm.x0 - nm.x0 * nm.x0 + nm.y1 * nm.y0 - nm.y0 * nm.y0);
        double a = nm.x1 * nm.x1 - 2 * nm.x1 * nm.x0 + nm.x0 * nm.x0 + nm.y1 * nm.y1 - 2 * nm.y1 * nm.y0 + nm.y0 * nm.y0;

        return (-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a);
    }

    public static double[] circleCollisions(Line move, double cx, double cy, double r) {
        Line nm = new Line((float)(move.x0 - cx), (float)(move.y0 - cy), (float)(move.x1 - cx), (float)(move.y1 - cy));

        double c = nm.x0 * nm.x0 + nm.y0 * nm.y0 - r * r;
        double b = 2 * (nm.x1 * nm.x0 - nm.x0 * nm.x0 + nm.y1 * nm.y0 - nm.y0 * nm.y0);
        double a = nm.x1 * nm.x1 - 2 * nm.x1 * nm.x0 + nm.x0 * nm.x0 + nm.y1 * nm.y1 - 2 * nm.y1 * nm.y0 + nm.y0 * nm.y0;

        double root = Math.sqrt(b * b - 4 * a * c);
        double t0 = (-b - root) / (2 * a);
        double t1 = (-b + root) / (2 * a);

        return new double[] {t0, t1};
    }

    public static boolean eitherInRange(double[] ts) {
        return (ts[0] > 0 && ts[0] < 1) || (ts[1] > 0 && ts[1] < 1);
    }

    public static CCD.Vector moveCircle(Line wall, double[] circ, double cx, double cy, double r) {
        double t3 = (circ[0] + circ[1]) / 2;

        double wx = xAlong(t3, wall);
        double wy = yAlong(t3, wall);

        Vector toCenter = new Vector((float)(cx - wx), (float)(cy - wy));

        double len = toCenter.length();
        double addTo = r - len;

        return toCenter.multiply((addTo / len) + 0.01);
    }

    public static boolean inline(double t) {
        return !(t > 1 || t < 0);
    }

    private static boolean inline(double t, double r) {
        return !(t > -r || t < r);
    }

    public static boolean normalSide(Line wall, double x0, double y0) {
        return (wall.y1 - wall.y0) * (wall.x0 - x0) + (wall.x1 - wall.x0) * (y0 - wall.y0) < 0;
    }

    public static boolean wallStartNormal(Line move, Line wall) {
        return (move.y1 - move.y0) * (move.x0 - wall.x0) + (move.x1 - move.x0) * (wall.y0 - move.y0) > 0;
    }

    public static boolean wallEndNormal(Line move, Line wall) {
        return (move.y1 - move.y0) * (move.x0 - wall.x1) + (move.x1 - move.x0) * (wall.y1 - move.y0) > 0;
    }

    public static double wallStartNormalGet(Line move, Line wall) {
        Vector v = new Vector(move);
        return ((move.y1 - move.y0) * (move.x0 - wall.x0) + (move.x1 - move.x0) * (wall.y0 - move.y0)) / v.length();
    }

    public static double wallEndNormalGet(Line move, Line wall) {
        Vector v = new Vector(move);
        return ((move.y1 - move.y0) * (move.x0 - wall.x1) + (move.x1 - move.x0) * (wall.y1 - move.y0)) / v.length();
    }

    private static double tDistance(Line move, Line wall, double d) {
        Vector a = new Vector(move.x0 - wall.x0, move.y0 - wall.y0);
        Vector b = new Vector(move.x1 - wall.x0, move.y1 - wall.y0);
        Vector c = new Vector(wall.x1 - wall.x0, wall.y1 - wall.y0);
        double theta = c.getAngle();
        a.rotate(-theta);
        b.rotate(-theta);
        return ((d - a.y) / (b.y - a.y));
    }

    private static boolean startEndDistances(Line move, double x0, double y0, double r) {
        double xComp = (move.x1 - x0);
        double yComp = (move.y1 - y0);
        double de = Math.sqrt(xComp * xComp + yComp * yComp);
        xComp = (move.x0 - x0);
        yComp = (move.y0 - y0);
        double ds = Math.sqrt(xComp * xComp + yComp * yComp);
        return de <= r || ds <= r;
    }

    public static double closestPoint(float x0, float y0, Line wall) {
        return (
            Math.pow(wall.x0 - x0, 2) + Math.pow(wall.y0 - y0, 2) + Math.pow(wall.x1 - wall.x0, 2) + Math.pow(wall.y1 - wall.y0, 2) - Math.pow(wall.x1 - x0, 2) - Math.pow(wall.y1 - y0, 2)
        ) / (
            2 * (Math.pow(wall.x1 - wall.x0, 2) + Math.pow(wall.y1 - wall.y0, 2))
        );
    }

    //
    //calls VVVVVVVV
    //

    public static Collision result(Line move, Line wall) {
        double t;
        if(inline(t = hitPoint(move, wall))) {
            boolean sn = wallStartNormal(move, wall);
            boolean en = wallEndNormal(move, wall);

            if(sn && !en) {
                return new Collision(0x011, t);
            } else if(!sn && en) {
                return new Collision(0x101, t);
            }
        }
        return new Collision(0x000, t);
    }

    public static Collision radiusResult(Line move, Line wall, double r) {
        double bt;
        if(inline(bt = tDistance(move, wall, r))) {
           if (inline(tDistance(wall, move, r))) {
                return new Collision(0x001, bt);
           }
        }
        return new Collision(0x000, bt);
    }

    public static double xAlong(double t, Line line) {
        return (line.x1 - line.x0) * t + line.x0;
    }

    public static double yAlong(double t, Line line) {
        return (line.y1 - line.y0) * t + line.y0;
    }

}
