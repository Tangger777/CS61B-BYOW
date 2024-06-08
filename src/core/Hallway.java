package core;

import tileengine.Tileset;

import java.util.*;

public class Hallway {

    /**
     * The algorithm used in this method is inspired by the article:
     * "Rooms and Mazes: A Procedural Dungeon Generator"
     * by Bob Nystrom
     * URL: https://journal.stuffwithstuff.com/2014/12/21/rooms-and-mazes/
     */
    public static void createHallways(World world, Variables v) {
        initialHallway(world, v);
        floodFillHallway(world, v);
        connectToRooms(world, v);

    }

    private static void initialHallway(World world, Variables v) {
        for (int i = 1; i < world.getWidth() - 1; i++) {
            for (int j = 1; j < world.getHeight() - 1; j++) {
                if (world.isNothing(i, j) && isOdd(i) && isOdd(j)) {
                    setHallway(i, j, world);
                    Point p = new Point(i, j);
                    v.getRoot().put(p, p);
                }

            }
        }
    }


    private static void floodFillHallway(World world, Variables v) {
        ArrayList<Point> walls = Wall.getAllWalls(world);
        while (!walls.isEmpty()) {
            int idx = v.getRANDOM().nextInt(walls.size());
            Point wall = walls.get(idx);
            Point unit1 = getFirstNearPoint(wall);
            Point unit2 = getSecondNearPoint(wall);
            var root = v.getRoot();
            // connect two roads if they don't intersect
            if (isValidRoadConnection(world, unit1, unit2, root)) {
                kruskalUnion(unit1, unit2, root);
                root.put(wall, unit1);
                setHallway(wall.getX(), wall.getY(), world);
            }
            walls.remove(idx);
        }
    }

    /**
     * check if two units can be connected,
     * they must be floor and not intersected
     */
    private static boolean isValidRoadConnection(World world, Point unit1, Point unit2, HashMap<Point, Point> root) {
        return world.isFloor(unit1.getX(), unit1.getY())
                && world.isFloor(unit2.getX(), unit2.getY())
                && !isIntersected(unit1, unit2, root);
    }

    /**
     * check if two units are intersected,
     * use kruskal algorithm to check if they are in the same set
     */
    private static boolean isIntersected(Point p1, Point p2, HashMap<Point, Point> root) {
        return kruskalFind(p1, root).equals(kruskalFind(p2, root));
    }

    /**
     * find ancestor and path compression
     */
    private static Point kruskalFind(Point unit, HashMap<Point, Point> root) {
        if (root.get(unit) != unit) {
            root.put(unit, kruskalFind(root.get(unit), root));
        }
        return root.get(unit);
    }

    /**
     * union two sets
     */
    private static void kruskalUnion(Point unit1, Point unit2, HashMap<Point, Point> root) {
        Point root1 = kruskalFind(unit1, root);
        Point root2 = kruskalFind(unit2, root);
        root.put(root1, root2);

    }

    private static Point getFirstNearPoint(Point wall) {
        int x = wall.getX();
        int y = wall.getY();
        int x1 = x % 2 == 1 ? x : x + 1;
        int y1 = x % 2 == 1 ? y + 1 : y;
        // let x1 be odd, and y1 be even

        return new Point(x1, y1);
    }

    private static Point getSecondNearPoint(Point wall) {
        int x = wall.getX();
        int y = wall.getY();
        int x2 = x % 2 == 1 ? x : x - 1;
        int y2 = x % 2 == 1 ? y - 1 : y;
        // let x2 be odd, and y2 be even

        return new Point(x2, y2);
    }

    private static boolean isOdd(int num) {
        return num % 2 == 1;
    }

    private static void setHallway(int x, int y, World world) {
        world.setTile(x, y, Tileset.FLOOR);
    }


    private static void connectToRooms(World world, Variables v) {
        // prepare variables for connecting
        v.getRoot().clear();
        Hallway.addHallwaysToArea(world, v);
        Room.addRoomsToArea(v);
        v.setMainArea(Room.getRandomRoom(v));

        Wall.findConnections(world, v);
        Wall.connectAreas(world, v);

        removeDeadEnds(world);
        Wall.removeAllWalls(world);
        Wall.reconstructWall(world);

    }

    /*
        add all hallways to the area HashSet<Point>,
        add all Points in a Hallway unit to the root HashMap<root, pointInHallway>
     */
    private static void addHallwaysToArea(World world, Variables variables) {
        int width = world.getWidth();
        int height = world.getHeight();
        boolean[][] visited = new boolean[width][height];

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                if (world.isFloor(x, y) && !visited[x][y]) {
                    Point roadPoint = new Point(x, y);
                    variables.getAreas().add(roadPoint);
                    variables.getRoot().put(roadPoint, roadPoint);
                    exploreConnectedRoadsBFS(world, visited, roadPoint, roadPoint, variables.getRoot());
                }
            }
        }
    }

    private static void exploreConnectedRoadsBFS(World world, boolean[][] visited, Point curPoint,
                                                 Point rootP, HashMap<Point, Point> root) {
        Queue<Point> queue = new ArrayDeque<>();
        queue.add(curPoint);
        while (!queue.isEmpty()) {
            curPoint = queue.poll();

            for (Point neighbor : Point.getFourNeighborPoints(curPoint)) {
                int neighborX = neighbor.getX();
                int neighborY = neighbor.getY();
                if (world.isFloor(neighborX, neighborY) && !visited[neighborX][neighborY]) {
                    queue.add(neighbor);
                    root.put(neighbor, rootP);
                    visited[neighborX][neighborY] = true;
                }
            }
        }
    }

    private static void removeDeadEnds(World world) {
        boolean[][] visited = new boolean[world.getWidth()][world.getHeight()];
        boolean done;
        do {
            done = true;
            for (int x = 1; x < world.getWidth() - 1; x++) {
                for (int y = 1; y < world.getHeight() - 1; y++) {
                    if (world.isFloor(x, y) && !visited[x][y]) {
                        int wallCount = countAdjacentWalls(world, x, y);
                        // if we find a rood with 3 walls surrounding it, it must be a dead end
                        if (wallCount == 3) {
                            world.getTiles()[x][y] = Tileset.NOTHING;
                            visited[x][y] = true;
                            done = false;
                        }
                    }
                }
            }
        } while (!done);
    }

    private static int countAdjacentWalls(World world, int x, int y) {
        int wallCount = 0;
        wallCount += isWallOrNothing(world, x - 1, y) ? 1 : 0;
        wallCount += isWallOrNothing(world, x + 1, y) ? 1 : 0;
        wallCount += isWallOrNothing(world, x, y - 1) ? 1 : 0;
        wallCount += isWallOrNothing(world, x, y + 1) ? 1 : 0;
        return wallCount;
    }

    private static boolean isWallOrNothing(World world, int x, int y) {
        return world.isWall(x, y) || world.isNothing(x, y);
    }
}
