package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

public class Engine {
    private static final int DEFAULT_WIDTH = 81;
    private static final int DEFAULT_HEIGHT = 31;
    private World world;
    private Player player;

    private Ghost ghost;
    private int width;
    private int height;

    public Engine() {
        this.world = new World();
        this.ghost = new Ghost();
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;

    }

    public void initPlayer() {

        int startX = 0;
        int startY = 0;


        while (!world.isFloor(startX, startY)) {
            startX++;
            startY++;
        }

        player = new Player(startX, startY);
        world.setTile(startX, startY, player.getAvatar());
    }

    public void initGhost() {
        ghost.generateGhost(world);
    }


    public void movePlayer(Direction direction) {
        int dx = 0, dy = 0;

        switch (direction) {
            case UP:
                dy = 1;
                break;
            case DOWN:
                dy = -1;
                break;
            case LEFT:
                dx = -1;
                break;
            case RIGHT:
                dx = 1;
                break;
            default:
                System.out.println("Unexpected direction: " + direction);
                break;
        }

        int newX = player.getX() + dx;
        int newY = player.getY() + dy;


        if (canMoveTo(newX, newY)) {
            //  floor
            world.setTile(player.getX(), player.getY(), Tileset.FLOOR);
            player.move(dx, dy);

            world.setTile(newX, newY, player.getAvatar());
        }
    }

    private boolean canMoveTo(int x, int y) {
        return world.getTiles()[x][y].character() != Tileset.WALL.character() && world.getTiles()[x][y].character()
                != Tileset.NOTHING.character();
    }


    public void saveBoard() {
        StringBuilder sb = new StringBuilder();
        sb.append(width).append(" ").append(height).append("\n");

        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                if (world.getTiles()[x][y] == Tileset.NOTHING) {
                    sb.append("0");
                } else if (world.getTiles()[x][y] == Tileset.WALL) {
                    sb.append("1");
                } else if (world.getTiles()[x][y] == Tileset.AVATAR) {
                    sb.append("2");
                } else if (world.getTiles()[x][y] == Tileset.ROOM) {
                    sb.append("3");
                } else if (world.getTiles()[x][y] == Tileset.GHOST) {
                    sb.append("4");
                } else if (world.getTiles()[x][y] == Tileset.GHOST_PATH) {
                    sb.append("5");
                } else {
                    sb.append("6");
                }
            }
            sb.append("\n");
        }
        FileUtils.writeFile("save.txt", sb.toString());
    }

    public void loadWorld(String filename) {
        String fileContents = FileUtils.readFile(filename);
        String[] lines = fileContents.split("\n");
        String[] dimensions = lines[0].split(" ");
        int lwidth = Integer.parseInt(dimensions[0]);
        int lheight = Integer.parseInt(dimensions[1]);
        this.width = lwidth;
        this.height = lheight;

        world = new World();


        for (int y = 0; y < height; y++) {
            String line = lines[lines.length - y - 1];
            for (int x = 0; x < width; x++) {
                char tileChar = line.charAt(x);
                if (tileChar == '0') {
                    world.changeTile(x, y, Tileset.NOTHING);
                }
                if (tileChar == '1') {
                    world.changeTile(x, y, Tileset.WALL);
                }
                if (tileChar == '2') {
                    world.changeTile(x, y, Tileset.AVATAR);
                    player = new Player(x, y);
                }
                if (tileChar == '3') {
                    world.changeTile(x, y, Tileset.ROOM);
                }
                if (tileChar == '4') {
                    ghost = new Ghost();
                    ghost.generateGhost(world, x, y);
                }
                if (tileChar == '5') {
                    world.changeTile(x, y, Tileset.GHOST_PATH);
                }
                if (tileChar == '6') {
                    world.changeTile(x, y, Tileset.FLOOR);
                }
            }
        }

    }

    public World getWorld() {
        return world;
    }

    public Ghost getGhost() {
        return ghost;
    }

    public TETile[][] getWorldTiles() {
        return world.getTiles();
    }
}
