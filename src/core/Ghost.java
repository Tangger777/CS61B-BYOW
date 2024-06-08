package core;

import tileengine.Tileset;

import java.util.*;

public class Ghost {
    private Point lastPlayerLocation;
    private Point location;
    private List<Point> ghostPath;


    private boolean pathShown = false;


    public void generateGhost(World world) {
        this.location = world.getRandomReachablePoint(world.getRandom());
        world.setTile(location.getX(), location.getY(), Tileset.GHOST);
    }

    public void generateGhost(World world, int x, int y) {
        this.location = new Point(x, y);
        world.setTile(location.getX(), location.getY(), Tileset.GHOST);
    }


    public void chasePlayer(World world) {
        Point playerLoc = getPlayerLocation(world);
        Point ghostLoc = this.location;


        if (playerLoc == null) {
            world.setGameOver();
            System.out.println("Game Over");
            return;
        }
        if (!playerLoc.equals(lastPlayerLocation)) {
            lastPlayerLocation = playerLoc;
            this.ghostPath = findPathBfs(world, ghostLoc, playerLoc);
        }
        moveGhost(world, ghostPath);

    }

    private List<Point> findPathBfs(World world, Point start, Point end) {
        Queue<Point> queue = new LinkedList<>();
        Map<Point, Point> parentMap = new HashMap<>();
        Set<Point> visited = new HashSet<>();

        queue.offer(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.equals(end)) {
                return reconstructPath(parentMap, start, end);
            }

            for (Point neighbor : Point.getFourNeighborPoints(current)) {
                if (!visited.contains(neighbor) && world.isUnit(neighbor.getX(), neighbor.getY())) {
                    queue.offer(neighbor);
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                }
            }
        }

        return null;
    }

    private List<Point> reconstructPath(Map<Point, Point> parentMap, Point start, Point end) {
        List<Point> path = new ArrayList<>();
        Point current = end;

        while (!current.equals(start)) {
            path.add(0, current);
            current = parentMap.get(current);
        }

        path.add(0, start);
        return path;
    }


    /* Alternative implementation using DFS
    private List<Point> findPathDfs(World world, Point start, Point end) {
        HashSet<Point> visited = new HashSet<>();
        List<Point> path = new ArrayList<>();

        if (dfsHelper(world, start, end, visited, path)) {
            return path;
        }

        return null;
    }

    private boolean dfsHelper(World world, Point current, Point end, HashSet<Point> visited, List<Point> path) {
        if (current.equals(end)) {
            path.add(current);
            return true;
        }

        if (visited.contains(current) || !world.isUnit(current.getX(), current.getY())) {
            return false;
        }

        visited.add(current);
        //path.add(current);


        for (Point neighbor : Point.getRandNeighborPoints(current, world.getRandom())) {
            if (dfsHelper(world, neighbor, end, visited, path)) {
                path.addFirst(current);
                return true;
            }
        }

        //path.removeLast();
        return false;
    }
    */

    private void showPath(World world, List<Point> path) {
        if (path == null) {
            return;
        }
        for (Point p : path) {
            if (world.isFloor(p.getX(), p.getY()) || world.isRoom(p.getX(), p.getY())) {
                world.setTile(p.getX(), p.getY(), Tileset.GHOST_PATH);
            }
        }
        this.pathShown = true;
    }

    public void showPath(World world) {
        if (!this.pathShown) {
            showPath(world, ghostPath);
        } else {
            clearPath(world);
        }

    }

    public void clearPath(World world) {
        if (ghostPath == null) {
            return;
        }
        for (Point p : world.getPath()) {
            if (world.isPath(p.getX(), p.getY())) {
                world.setTile(p.getX(), p.getY(), Tileset.FLOOR);
            }
        }
        this.pathShown = false;
    }

    private Point getPlayerLocation(World world) {
        for (int i = 0; i < world.getWidth(); i++) {
            for (int j = 0; j < world.getHeight(); j++) {
                if (world.getTiles()[i][j] == Tileset.AVATAR) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }

    private void moveGhost(World world, List<Point> path) {
        if (path == null) {
            return;
        }
        Point next = path.get(0);
        world.setTile(location.getX(), location.getY(), Tileset.FLOOR);
        world.setTile(next.getX(), next.getY(), Tileset.GHOST);
        location = next;
        path.remove(0);

    }
}
