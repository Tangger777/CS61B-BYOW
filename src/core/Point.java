package core;

import java.util.*;

public class Point {
    private final int x;
    private final int y;


    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static List<Point> getFourNeighborPoints(Point p) {
        Point left = new Point(p.x - 1, p.y);
        Point right = new Point(p.x + 1, p.y);
        Point up = new Point(p.x, p.y + 1);
        Point down = new Point(p.x, p.y - 1);

        List<Point> neighbors = new ArrayList<>();
        neighbors.add(left);
        neighbors.add(right);
        neighbors.add(up);
        neighbors.add(down);
        return neighbors;
    }

    public static List<Point> getRandNeighborPoints(Point p, Random random) {
        List<Point> neighbors = getFourNeighborPoints(p);
        Collections.shuffle(neighbors, random);
        return neighbors;
    }


    @Override
    public boolean equals(Object o) {

        if ((o instanceof Point p)) {
            return x == p.x && y == p.y;

        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /*

     */
    public boolean isNearMain(World world, Variables v) {
        for (Point near : getFourNeighborPoints(this)) {
            if (near.getRoomRootPoint(world).isInMainArea(v)) {
                return true;
            }
        }
        return false;
    }

    /*
     * check if the point is in the main area
     */
    public boolean isInMainArea(Variables v) {
        HashMap<Point, Point> root = v.getRoot();
        Point mainArea = v.getMainArea();
        return Objects.equals(root.get(this), mainArea)
                || Objects.equals(root.get(root.get(this)), mainArea);
    }

    public Point getRoomRootPoint(World world) {
        if (world.isRoom(x, y)) {
            return Room.getBottomLeft(world, this);
        }
        return this;
    }

    /*
    get the near point of a connection
     */
    public static Point[] getNearPoint(World world, Point connection) {
        int x1 = connection.x + 1;
        int y1 = connection.y;
        int x2 = connection.x - 1;
        int y2 = connection.y;
        // if the right side is not a road or a room, then the left side is a road or a room
        if (!world.isFloor(x1, y1) && !world.isRoom(x1, y1)) {
            x1 = connection.x;
            y1 = connection.y + 1;
            x2 = connection.x;
            y2 = connection.y - 1;
        }
        return new Point[]{new Point(x1, y1), new Point(x2, y2)};
    }


}

