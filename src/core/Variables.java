package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Variables {
    World world;
    World tempWorld;
    private Random RANDOM;
    private ArrayList<Point> connections;
    private HashMap<Point, Point> roomDiagnalPoints;
    private HashMap<Point, Point> root; // to store all WALL and FLOOR
    private HashSet<Point> areas;
    private Point mainArea;

    Variables(long seed) {
        connections = new ArrayList<>();
        roomDiagnalPoints = new HashMap<>();
        root = new HashMap<>();
        areas = new HashSet<>();
        mainArea = null;
        RANDOM = new Random(seed);
    }



    public Random getRANDOM() {
        return RANDOM;
    }

    public ArrayList<Point> getConnections() {
        return connections;
    }


    public HashMap<Point, Point> getRoomDiagnalPoints() {
        return roomDiagnalPoints;
    }

    public HashMap<Point, Point> getRoot() {
        return root;
    }

    public HashSet<Point> getAreas() {
        return areas;
    }

    public Point getMainArea() {
        return mainArea;
    }

    public void setMainArea(Point mainArea) {
        this.mainArea = mainArea;
    }

    public World getTempWorld() {
        return tempWorld;
    }

    public World getWorld() {
        return world;
    }
}
