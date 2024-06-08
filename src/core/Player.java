package core;

import tileengine.TETile;
import tileengine.Tileset;

public class Player {
    private int x; // 玩家的 x 坐标
    private int y; // 玩家的 y 坐标
    private TETile avatar;
    public Player(int startX, int startY) {
        x = startX;
        y = startY;
        avatar = Tileset.AVATAR;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public TETile getAvatar() {
        return avatar;
    }
}

