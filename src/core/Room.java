package core;

import tileengine.Tileset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;


public class Room {
    public static final int MAX_NUM_LOOP = 15;
    private int x, y;
    private int width, height; 

    // room size
    public Room(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }

    public static void createRooms(World world, Variables v) {
        int numRooms = v.getRANDOM().nextInt(15, 20);
        for (int i = 0; i < numRooms; i++) {
            generateSingleRoom(world, v);
        }
    }

    public static void generateSingleRoom(World world, Variables v) {
        Random random = v.getRANDOM();
        int h = random.nextInt(3) * 2 + 5; // 5 is the minimum height
        int w = random.nextInt(3) * 2 + 5;

        int x = random.nextInt((world.getWidth() - w - 1) / 2) * 2 + 1; // to make sure x is odd
        int y = random.nextInt((world.getHeight() - h - 1) / 2) * 2 + 1; // to make sure y is odd
        int ct = 0;

        while (!world.isNothing(x, y) || intersects(x, y, w, h, v)) {
            x = random.nextInt((world.getWidth() - w - 1) / 2) * 2 + 1;
            y = random.nextInt((world.getHeight() - h - 1) / 2) * 2 + 1;
            ct++;
            if (ct == MAX_NUM_LOOP) {
                return;
            }
        }
        // store the left bottom and right top points of the room into the hashmap
        v.getRoomDiagnalPoints().put(new Point(x, y), new Point(x + w - 1, y + h - 1));
        // Fill the room with floor
        fillRoom(x, y, w, h, world);

    }

    private static void fillRoom(int x, int y, int width, int height, World world) {
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                world.getTiles()[i][j] = Tileset.ROOM;
            }
        }
    }
    /*
    check if given x,y,width,height intersects with any room that exists
     */
    public static boolean intersects(int x, int y, int width, int height, Variables v) {
        HashMap<Point, Point> roomAreas = v.getRoomDiagnalPoints();
        for (Point p : roomAreas.keySet()) {
            Point p2 = roomAreas.get(p);
            if (x <= p2.getX() && x + width >= p.getX() && y <= p2.getY() && y + height >= p.getY()) {
                return true;
            }
        }
        return false;

    }

    public static void addRoomsToArea(Variables v) {
        Set<Point> keys = v.getRoomDiagnalPoints().keySet();
        v.getAreas().addAll(keys); // add all rooms to the areas
        keys.forEach(room -> v.getRoot().put(room, room)); // add all rooms to the root
    }

    public static Point getBottomLeft(World world, Point unit) {
        int x = unit.getX();
        int y = unit.getY();
        // Find the leftmost non-room position
        while (x > 0 && world.isRoom(x - 1, y)) {
            x--;
        }
        // Find the bottommost non-room position
        while (y > 0 && world.isRoom(x, y - 1)) {
            y--;
        }
        return new Point(x, y);
    }
    public static Point getRandomRoom(Variables v) {
        ArrayList<Point> rooms = new ArrayList<>(v.getRoomDiagnalPoints().keySet());
        Point mainArea = v.getMainArea();
        rooms.remove(mainArea);
        return rooms.isEmpty() ? null : rooms.get(v.getRANDOM().nextInt(rooms.size()));
    }
}

