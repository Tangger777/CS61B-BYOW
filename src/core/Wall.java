package core;

import tileengine.Tileset;

import java.util.ArrayList;
import java.util.HashMap;

public class Wall {

    public static void initialWalls(World world, Variables v) {
        for (int i = 1; i < world.getWidth() - 1; i++) {
            for (int j = 1; j < world.getHeight() - 1; j++) {
                if (world.isNothing(i, j)) {
                    if (!isOdd(i) || !isOdd(j)) {
                        setWall(i, j, world);
                        Point p = new Point(i, j);
                        v.getRoot().put(p, p);
                    }
                }

            }
        }
    }

    private static boolean isOdd(int num) {
        return num % 2 == 1;
    }

    private static void setWall(int x, int y, World world) {
        world.setTile(x, y, Tileset.WALL);
    }

    public static ArrayList<Point> getAllWalls(World world) {
        ArrayList<Point> ret = new ArrayList<>();
        for (int x = 1; x < world.getWidth() - 1; x++) {
            for (int y = 1; y < world.getHeight() - 1; y++) {
                if (world.isWall(x, y)) {
                    ret.add(new Point(x, y));
                }
            }
        }
        return ret;
    }

    /*
    find all the walls that around a room and can be connected to the hallway
     */
    public static void findConnections(World world, Variables variables) {
        for (int x = 1; x < world.getWidth() - 1; x++) {
            for (int y = 1; y < world.getHeight() - 1; y++) {
                if (world.isWall(x, y) && ableToConnectAreas(world, x, y)) {
                    variables.getConnections().add(new Point(x, y));
                }
            }
        }
    }

    /*
    check where x,y can neighbor with a room
 */
    private static boolean ableToConnectAreas(World world, int x, int y) {
        return world.isFloor(x - 1, y) && world.isRoom(x + 1, y)
                || world.isFloor(x + 1, y) && world.isRoom(x - 1, y)
                || world.isFloor(x, y - 1) && world.isRoom(x, y + 1)
                || world.isFloor(x, y + 1) && world.isRoom(x, y - 1);
    }

    public static void connectAreas(World world, Variables v) {
        ArrayList<Point> connections = v.getConnections();
        while (!connections.isEmpty()) {
            Point connection = getRandomNearMainConnection(world, v);
            connect(world, connection, v);
            removeRestConnection(world, v);
        }
    }

    private static Point getRandomNearMainConnection(World world, Variables v) {
        ArrayList<Point> connections = v.getConnections();

        return connections.stream().filter(p -> p.isNearMain(world, v)).findAny().
                orElse(connections.get(v.getRANDOM().nextInt(connections.size())));


    }

    private static void connect(World world, Point connection, Variables v) {
        Point[] near = Point.getNearPoint(world, connection);
        Point u1 = near[0].getRoomRootPoint(world);
        Point u2 = near[1].getRoomRootPoint(world);
        // world.getTiles()[connection.getX()][connection.getY()] = Tileset.UNLOCKED_DOOR;
        world.setTile(connection.getX(), connection.getY(), Tileset.FLOOR);
        v.getConnections().remove(connection);
        HashMap<Point, Point> root = v.getRoot();
        Point mainArea = v.getMainArea();
        root.put(u1.isInMainArea(v) ? root.get(u2) : root.get(u1), mainArea);
    }

    private static void removeRestConnection(World world, Variables v) {
        ArrayList<Point> connections = v.getConnections();
        connections.removeIf(c -> {
            Point[] nears = Point.getNearPoint(world, c);
            Point u1 = nears[0].getRoomRootPoint(world);
            Point u2 = nears[1].getRoomRootPoint(world);
            int x = c.getX();
            int y = c.getY();
            return u1.isInMainArea(v)
                    && u2.isInMainArea(v)
                    && !world.isFloor(x, y)
                    && v.getRANDOM().nextInt(v.getRoomDiagnalPoints().size()) >= 1;
        });
    }

    public static void removeAllWalls(World world) {
        for (int x = 1; x < world.getWidth() - 1; x++) {
            for (int y = 1; y < world.getHeight() - 1; y++) {
                if (world.isWall(x, y)) {
                    world.setTile(x, y, Tileset.NOTHING);
                }
            }
        }
    }

    public static void reconstructWall(World world) {
        int width = world.getWidth();
        int height = world.getHeight();

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                if (world.isUnit(x, y)) {
                    buildWallAroundUnit(world, x, y);
                }
            }
        }
    }

    private static void buildWallAroundUnit(World world, int x, int y) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                int pX = x + dx;
                int pY = y + dy;
                if (!world.isUnit(pX, pY)) {
                    setWall(pX, pY, world);
                }
            }
        }
    }


}
