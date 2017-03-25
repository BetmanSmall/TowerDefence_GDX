package com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder;

import java.io.File;
import java.util.ArrayDeque;

/**
 * Functionality for managing the different routing algorithms.
 * The routing algorithms save all necessary information to the vertices on the VertexMatrix (the map)
 * @author BetmanSmall
 */
public class PathFinder {
    public Node[][] nodeMatrix;
    private boolean clearmap;
    private int[] start;
    private int[] goal;
    private Tools Tools;
    private Cartographer c;

    public PathFinder() {
        Tools = new Tools();
        c = new Cartographer();
    }

    public ArrayDeque<Node> route(int sX, int sY, int eX, int eY) {
        return route(new int[]{sY, sX}, new int[]{eY, eX}, Options.ASTAR, Options.EUCLIDEAN_HEURISTIC, false);
    }

    /**
     * Routing with specified settings, returns the shortest route.
     * @param start start coordinates y,x of the route.
     * @param goal end coordinates y,x of the route.
     * @param algo chosen algorithm, algorithms provided in PathFinder as final static integers.
     * @param heuristic chosen heuristic, heuristics provided in PathFinder as final static integers.
     * @param diagonalMovement true -> diagonal movement allowed. Always allowed for JPS.
     * @return null if no charMatrix loaded or invalid [y,x] coordinates
     */
    public ArrayDeque<Node> route(int[] start, int[] goal, Options algo, Options heuristic, boolean diagonalMovement) {
        if(nodeMatrix ==null) return null;
        if(!Tools.valid(start[0], start[1], nodeMatrix) || !Tools.valid(goal[0], goal[1], nodeMatrix)) return null;
        
        //set default heuristic if none was chosen for non-DIJKSTRA algorithm
        if(algo!= Options.DIJKSTRA && heuristic== Options.NO_HEURISTIC) heuristic = Options.MANHATTAN_HEURISTIC;
        
        this.start=start;
        this.goal=goal;
        
        //clear map only if it has been used in routing.
        if(clearmap) clearMap();
        clearmap=true;
                
        //select correct algo & settings
        if(algo== Options.DIJKSTRA) return new A_Star(nodeMatrix, start, goal, Options.NO_HEURISTIC, diagonalMovement).run();
        else if(algo== Options.ASTAR) return new A_Star(nodeMatrix, start, goal, heuristic, diagonalMovement).run();
        else if(algo== Options.JPS) return new JPS(nodeMatrix, start, goal, heuristic, true).run();
        return null;
    }
    
    /**
     * Return the distance of the shortest route found by the last run algorithm.
     * @return distance from start to goal.
     */
    public double getDistance(){
        return this.nodeMatrix[goal[0]][goal[1]].getDistance();
    }
    
    /**
     * Loads the desired map to be used by the routing algorithms.
     * @param charM 
     */
    public void loadCharMatrix(char[][] charM){
        nodeMatrix = new Node[charM.length][charM[0].length];
        
        for (int i = 0; i < nodeMatrix.length; i++)
            for (int j = 0; j < nodeMatrix[0].length; j++)
                nodeMatrix[i][j]=new Node(j, i, charM[i][j]);

        //no need to clear map in PathFinder.route();
        clearmap=false;
    }
    
    /**
     * Convert .map file to a char[][] matrix.
     */
    public char[][] dotMapToCharMatrix(File file) throws Exception {
        c.loadMap(file);
        return c.toCharMatrix();
    }

    /**
     * Return the number of comparisons done by the routing algorithm that was last run.
     * @return comparisons.
     */
    public int comparisons(){
        int comps=0;
        for (int i = 0; i < nodeMatrix.length; i++)
            for (int j = 0; j < nodeMatrix[0].length; j++)
                if(nodeMatrix[i][j].getDistance() != -1) comps++;
        return comps;
    }
    
    /**
     * Get currently loaded vertex matrix (map).
     * @return nodeMatrix.
     */
    public Node[][] getNodeMatrix() {
        return nodeMatrix;
    }
    
    /**
     * Generates and returns a random validate coordinate [y,x] on the map.
     * Returns null if no charMatrix is loaded.
     * @return 
     */
    public int[] getRandomCoordinate(){
        if(this.nodeMatrix ==null) return null;
        return Tools.randomPoint(nodeMatrix);
    }
    
    /**
     * Returns the same coordinate [y,x] if already valid, otherwise finds the closes valid.
     * Returns null if no charMatrix is loaded.
     * @return 
     */
    public int[] getClosestValidCoordinate(int[] coord){
        if(this.nodeMatrix ==null) return null;
        return Tools.closestValidCoordinate(nodeMatrix, coord);
    }

    private void clearMap(){
        for (int i = 0; i < nodeMatrix.length; i++)
            for (int j = 0; j < nodeMatrix[0].length; j++)
//                nodeMatrix[i][j]=new Node(j, i, nodeMatrix[i][j].getKey());
                nodeMatrix[i][j].clear();
    }
}
