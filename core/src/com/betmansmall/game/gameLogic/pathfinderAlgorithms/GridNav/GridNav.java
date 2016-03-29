package com.betmansmall.game.gameLogic.pathfinderAlgorithms.GridNav;

import java.io.File;
import java.util.ArrayDeque;

/**
 * Functionality for managing the different routing algorithms.
 * The routing algorithms save all necessary information to the vertices on the VertexMatrix (the map)
 * @author Elias Nygren
 */
public class GridNav {
    private Vertex[][] vertexMatrix;
    private boolean clearmap;
    private int[] start;
    private int[] goal;
    private Tools Tools;
    private Cartographer c;

    public GridNav() {
        Tools = new Tools();
        c = new Cartographer();
    }

    public ArrayDeque<Vertex> route(int sX, int sY, int eX, int eY) {
        return route(new int[]{sY, sX}, new int[]{eY, eX}, Options.ASTAR, Options.EUCLIDEAN_HEURISTIC, false);
    }

    /**
     * Routing with specified settings, returns the shortest route.
     * @param start start coordinates y,x of the route.
     * @param goal end coordinates y,x of the route.
     * @param algo chosen algorithm, algorithms provided in GridNav as final static integers.
     * @param heuristic chosen heuristic, heuristics provided in GridNav as final static integers.
     * @param diagonalMovement true -> diagonal movement allowed. Always allowed for JPS.
     * @return null if no charMatrix loaded or invalid [y,x] coordinates
     */
   
    public ArrayDeque<Vertex> route(int[] start, int[] goal, Options algo, Options heuristic, boolean diagonalMovement) {
        if(vertexMatrix==null) return null;
        if(!Tools.valid(start[0], start[1], vertexMatrix) || !Tools.valid(goal[0], goal[1], vertexMatrix)) return null;
        
        //set default heuristic if none was chosen for non-DIJKSTRA algorithm
        if(algo!= Options.DIJKSTRA && heuristic== Options.NO_HEURISTIC) heuristic = Options.MANHATTAN_HEURISTIC;
        
        this.start=start;
        this.goal=goal;
        
        //clear map only if it has been used in routing.
        if(clearmap) clearMap();
        clearmap=true;
                
        //select correct algo & settings
        if(algo== Options.DIJKSTRA) return new Astar(vertexMatrix, start, goal, Options.NO_HEURISTIC, diagonalMovement).run();
        else if(algo== Options.ASTAR) return new Astar(vertexMatrix, start, goal, heuristic, diagonalMovement).run();
        else if(algo== Options.JPS) return new JPS(vertexMatrix, start, goal, heuristic, true).run();
        return null;
    }
    
    /**
     * Return the distance of the shortest route found by the last run algorithm.
     * @return distance from start to goal.
     */
    public double getDistance(){
        return this.vertexMatrix[goal[0]][goal[1]].getDistance();
    }
    
    /**
     * Loads the desired map to be used by the routing algorithms.
     * @param charM 
     */
    public void loadCharMatrix(char[][] charM){
        vertexMatrix = new Vertex[charM.length][charM[0].length];
        
        for (int i = 0; i < vertexMatrix.length; i++) 
            for (int j = 0; j < vertexMatrix[0].length; j++) 
                vertexMatrix[i][j]=new Vertex(j, i, charM[i][j]);

        //no need to clear map in GridNav.route();
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
        for (int i = 0; i < vertexMatrix.length; i++) 
            for (int j = 0; j < vertexMatrix[0].length; j++) 
                if(vertexMatrix[i][j].getDistance() != -1) comps++;
        return comps;
    }
    
    /**
     * Get currently loaded vertex matrix (map).
     * @return vertexMatrix.
     */
    public Vertex[][] getVertexMatrix() {
        return vertexMatrix;
    }
    
    /**
     * Generates and returns a random validate coordinate [y,x] on the map.
     * Returns null if no charMatrix is loaded.
     * @return 
     */
    public int[] getRandomCoordinate(){
        if(this.vertexMatrix==null) return null;
        return Tools.randomPoint(vertexMatrix);
    }
    
    /**
     * Returns the same coordinate [y,x] if already valid, otherwise finds the closes valid.
     * Returns null if no charMatrix is loaded.
     * @return 
     */
    public int[] getClosestValidCoordinate(int[] coord){
        if(this.vertexMatrix==null) return null;
        return Tools.closestValidCoordinate(vertexMatrix, coord);
    }
    
    
    private void clearMap(){
        for (int i = 0; i < vertexMatrix.length; i++) 
            for (int j = 0; j < vertexMatrix[0].length; j++) 
                vertexMatrix[i][j]=new Vertex(j, i, vertexMatrix[i][j].getKey());
    }
    
}
