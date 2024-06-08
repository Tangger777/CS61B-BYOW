package core;


import tileengine.TETile;
import tileengine.Tileset;

public class AutograderBuddy {

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] getWorldFromInput(String input) {
        Engine gameEngine = new Engine();
        input = input.toUpperCase();
        StringBuilder seedBuilder = new StringBuilder();
        boolean buildingSeed = false;
        boolean colonDetected = false;
        if (input.charAt(0) == 'Q') {
            return gameEngine.getWorldTiles();
        }

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (colonDetected) {
                if (c == 'Q') {
                    gameEngine.saveBoard();
                    return gameEngine.getWorldTiles();
                }
                colonDetected = false;
            } else if (c == ':') {
                colonDetected = true;
            } else if (c == 'N') {
                buildingSeed = true;
            } else if (buildingSeed) {
                if (Character.isDigit(c)) {
                    seedBuilder.append(c);
                } else if (c == 'S') {
                    long seed = Long.parseLong(seedBuilder.toString());
                    gameEngine.getWorld().generate(seed);
                    buildingSeed = false;
                    gameEngine.initPlayer();
                }
            } else if (c == 'L') {
                gameEngine.loadWorld("save.txt");
            } else {
                handleMovement(c, gameEngine);
            }
        }

        return gameEngine.getWorldTiles();
    }

    private static void handleMovement(char command, Engine gameEngine) {
        // 根据命令移动玩家
        switch (command) {
            case 'W':
                gameEngine.movePlayer(Direction.UP);
                break;
            case 'S':
                gameEngine.movePlayer(Direction.DOWN);
                break;
            case 'A':
                gameEngine.movePlayer(Direction.LEFT);
                break;
            case 'D':
                gameEngine.movePlayer(Direction.RIGHT);
                break;
            default:
                System.out.println("Unexpected direction: ");
                break;
        }
    }


    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character()
                || t.character() == Tileset.AVATAR.character()
                || t.character() == Tileset.FLOWER.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }
}
