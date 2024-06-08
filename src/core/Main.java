package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import java.awt.*;


public class Main {
    private static void handleNewGame(Engine engine, TERenderer ter) {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.text(200, 250, "Enter a seed and press S");
        StdDraw.show();
        String seedString = "";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char ch = StdDraw.nextKeyTyped();
                if (ch == 'S' || ch == 's') {
                    break;
                }
                seedString += ch;
                StdDraw.clear(StdDraw.BLACK);
                StdDraw.text(200, 250, "Seed: " + seedString);
                StdDraw.show();
            }
        }

        long seed = Long.parseLong(seedString);
        engine.getWorld().generate(seed);
        engine.initPlayer();
        engine.initGhost();

        ter.initialize(engine.getWorld().getWidth(), engine.getWorld().getHeight());
        ter.renderFrame(engine.getWorldTiles());
    }

    private static void processGameCommand(char command, Engine world, TERenderer ter) {
        switch (command) {
            case 'W':
                world.movePlayer(Direction.UP);
                break;
            case 'S':
                world.movePlayer(Direction.DOWN);
                break;
            case 'A':
                world.movePlayer(Direction.LEFT);
                break;
            case 'D':
                world.movePlayer(Direction.RIGHT);
                break;
            case 'T':
                world.getGhost().showPath(world.getWorld());
                break;
            default:
                System.out.println("Unexpected direction: ");
                break;
        }
        ter.renderFrame(world.getWorldTiles());
    }

    private static char waitForMenuInput() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char typed = StdDraw.nextKeyTyped();
                if (typed == 'n' || typed == 'l' || typed == 'q') {
                    return Character.toUpperCase(typed);
                }
                return typed;
            }
        }
    }

    private static void handleGameStart(char typed, Engine world, TERenderer ter) {
        if (typed == 'N') {
            handleNewGame(world, ter);
        } else if (typed == 'L') {
            world.loadWorld("save.txt");
            ter.initialize(world.getWorld().getWidth(), world.getWorld().getHeight());
            ter.renderFrame(world.getWorldTiles());
        }
    }


    public static void main(String[] args) {
        StdDraw.setCanvasSize(400, 500);
        StdDraw.setXscale(0, 400);
        StdDraw.setYscale(0, 500);

        boolean inMainMenu = true;  // 标志是否处于主菜单
        boolean colonDetected = false;  // 标志是否检测到冒号

        Engine engine = new Engine();
        TERenderer ter = new TERenderer();
        HUD hud = new HUD(engine.getWorld());
        while (!engine.getWorld().isGameOver()) {
            if (inMainMenu) {
                StdDraw.clear(StdDraw.BLACK);
                StdDraw.setPenColor(StdDraw.WHITE);
                Font font = new Font("Arial", Font.BOLD, 35);
                StdDraw.setFont(font);
                StdDraw.text(200, 350, "CS61B: THE GAME");
                Font font1 = new Font("Arial", Font.CENTER_BASELINE, 20);
                StdDraw.setFont(font1);
                StdDraw.text(200, 250, "New Game (N)");
                StdDraw.text(200, 200, "Load Game (L)");
                StdDraw.text(200, 150, "Quit (Q)");
                StdDraw.show();
                char typed = waitForMenuInput();
                if (typed == 'Q') {
                    System.exit(0);
                } else if (typed == 'N' || typed == 'L') {
                    handleGameStart(typed, engine, ter);
                    inMainMenu = false;
                }
            } else {
                if (StdDraw.hasNextKeyTyped()) {
                    char command = StdDraw.nextKeyTyped();
                    command = Character.toUpperCase(command);

                    if (colonDetected) {
                        if (command == 'Q') {
                            engine.saveBoard();
                            System.exit(0);
                        }
                        colonDetected = false;
                    } else if (command == ':') {
                        colonDetected = true;
                    } else {
                        processGameCommand(command, engine, ter); // process movement
                    }
                }
                Ghost ghost = engine.getGhost();
                ghost.chasePlayer(engine.getWorld());
                ter.renderFrame(engine.getWorldTiles());
                hud.displayTileInfo();
                StdDraw.show();
                StdDraw.pause(60);
            }
        }
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font = new Font("Arial", Font.BOLD, 80);
        StdDraw.setFont(font);
        StdDraw.text(40, 16, "Game Over");
        StdDraw.show();
        StdDraw.pause(2000);

    }
}
