import core.AutograderBuddy;
import edu.princeton.cs.algs4.StdDraw;
import org.junit.jupiter.api.Test;
import tileengine.TERenderer;
import tileengine.TETile;

public class WorldGenTests {
    @Test
    public void basicTest() {
        // put different seeds here to test different worlds
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n1234349667896789s");

        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(5000); // pause for 5 seconds so you can see the output
    }

    @Test
    public void basicInteractivityTest() {
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("q");

        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(5000);

    }

    @Test
    public void basicSaveTest() {
        TETile[][] tiles = AutograderBuddy.getWorldFromInput("n123swasd:q");
        TETile[][] tiles2 = AutograderBuddy.getWorldFromInput("lwasd");

        TERenderer ter = new TERenderer();
        ter.initialize(tiles.length, tiles[0].length);
        ter.renderFrame(tiles);
        StdDraw.pause(5000);

        ter.initialize(tiles2.length, tiles2[0].length);
        ter.renderFrame(tiles2);
        StdDraw.pause(5000);
    }
}
