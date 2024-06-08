package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;

import java.awt.*;

public class HUD {

    private World world;

    public HUD(World world) {
        this.world = world;
    }

    public void displayTileInfo() {
        int mouseX = (int) StdDraw.mouseX() ;
        int mouseY = (int) StdDraw.mouseY() ;

        if (mouseX >= 0 && mouseX < world.getWidth() && mouseY >= 0 && mouseY < world.getHeight()) {
            TETile tile = world.getTile(mouseX, mouseY);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.textLeft(1, world.getHeight() - 1, tile.description());
            
        }
    }
}

