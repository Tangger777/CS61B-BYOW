package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    private static final int WIDTH = 81;
    private static final int HEIGHT = 31;
    private TETile[][] tiles;
    private boolean isGameOver = false;

    private Random random;



    public World() {
        this.tiles = new TETile[WIDTH][HEIGHT];
        fillTilesNothing();
    }

    private void fillTilesNothing() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }


    public void generate(long seed) {
        Variables v = new Variables(seed);
        this.random = v.getRANDOM();
        fillTilesNothing();
        Room.createRooms(this, v);
        Wall.initialWalls(this, v);
        Hallway.createHallways(this, v);

    }



    public TETile getTile(int x, int y){
        return tiles[x][y];
    }

    public TETile[][] getTiles() {
        return tiles;
    }

    public void changeTile(int x, int y, TETile tile) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            tiles[x][y] = tile;
        } else {
            throw new IllegalArgumentException("Coordinates out of bounds");
        }
    }


    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public Random getRandom() {
        return random;
    }

    public Point getRandomReachablePoint(Random rd) {
        int x = 0;
        int y = 0;
        while (!isUnit(x, y)) {
            x = rd.nextInt(WIDTH);
            y = rd.nextInt(HEIGHT);
        }
        return new Point(x, y);
    }

    public List<Point> getPath() {
        List<Point> path = new ArrayList<>();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (tiles[i][j] == Tileset.GHOST_PATH) {
                    path.add(new Point(i, j));
                }
            }
        }
        return path;
    }


    public boolean isFloor(int x, int y) {
        return tiles[x][y] == Tileset.FLOOR;
    }

    public boolean isRoom(int x, int y) {
        return tiles[x][y] == Tileset.ROOM;
    }

    public boolean isNothing(int x, int y) {
        return tiles[x][y] == Tileset.NOTHING;
    }

    public boolean isDoor(int x, int y) {
        return tiles[x][y] == Tileset.UNLOCKED_DOOR;
    }

    public boolean isNothing(Point p) {
        return isNothing(p.getX(), p.getY());
    }

    public boolean isWall(int x, int y) {
        return tiles[x][y] == Tileset.WALL;
    }

    public boolean isAvatar(int x, int y) {
        return tiles[x][y] == Tileset.AVATAR;
    }

    public boolean isGhost(int x, int y) {
        return tiles[x][y] == Tileset.GHOST;
    }

    public boolean isPath(int x, int y) {
        return tiles[x][y] == Tileset.GHOST_PATH;
    }

    public boolean isUnit(int x, int y) {
        return isRoom(x, y) || isFloor(x, y) || isDoor(x, y) || isGhost(x, y) || isPath(x, y) || isAvatar(x, y);
    }
    public void setTile(int x, int y, TETile t) {
        tiles[x][y] = t;
    }

    public void setTile(Point p, TETile t) {
        setTile(p.getX(), p.getY(), t);
    }

    public void setGameOver() {
        isGameOver = true;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public static void main(String[] args) {

        World myWorld = new World();
        myWorld.generate(1234);
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(myWorld.getTiles());
    }
}
