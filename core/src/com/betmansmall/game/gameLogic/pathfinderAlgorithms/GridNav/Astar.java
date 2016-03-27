/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.betmansmall.game.gameLogic.pathfinderAlgorithms.GridNav;

import java.util.ArrayDeque;
import java.util.PriorityQueue;



/**
 * Implementation of the A* algorithm with Java built in data structures. 
 * @author Elias Nygren
 */
public class Astar {
    private PriorityQueue<Vertex> heap;
    private Vertex[][] map;
    private Vertex start;
    private Vertex goal;
    private String directions;
    private com.betmansmall.game.gameLogic.pathfinderAlgorithms.GridNav.Options heuristics;
    
    private boolean utilitymode=false;
    private com.betmansmall.game.gameLogic.pathfinderAlgorithms.GridNav.Tools Tools;
    private ArrayDeque<Vertex> utilityStack;
    
    /**
     * Initializes the Astar so it is ready to run.
     * @param map The charmatrix representation of the map used by this routing algorithm
     * @param start The starting point of the route, supplied in {y, x} format coordinate array
     * @param goal The end point of the route, supplied in {y, x} format coordinate array
     * @param heuristics desired heuristic
     * @param diagonalMovement true -> diagonalMovement is allowed, false -> denied
     */
    
    protected Astar(Vertex[][] map, int[] start, int[] goal, com.betmansmall.game.gameLogic.pathfinderAlgorithms.GridNav.Options heuristics, boolean diagonalMovement){
        Tools = new Tools();
        this.map = map;
        this.heap = new PriorityQueue<Vertex>(map.length*map[0].length);
        this.start = map[start[0]][start[1]];
        this.goal = map[goal[0]][goal[1]];
        this.heuristics=heuristics;
        
        this.start.setOnPath(true);
        this.goal.setOnPath(true);
        
        if(diagonalMovement) directions = "12345678";
        else directions = "1357";
        
    }
    
    /**
     * Initializes the Astar in utilitymode, which finds the closest walkable vertex with Dijkstra.
     * Uses utilityStack to store changed vertices so their state can be reset.
     * @param map The vertex matrix representation of the map used by this routing algorithm
     * @param start The starting point of the route, supplied in {y, x} format coordinate array
     * @param goal The end point of the route, supplied in {y, x} format coordinate array
     * @param heuristics XXX
     * @param diagonalMovement true -> diagonalMovement is allowed, false -> denied
     * @param utilitymode true -> utilitymode ON.
     */
    
    protected Astar(Vertex[][] map, int[] start, int[] goal, com.betmansmall.game.gameLogic.pathfinderAlgorithms.GridNav.Options heuristics, boolean diagonalMovement, boolean utilitymode){
        this(map, start, goal, heuristics, diagonalMovement);
        this.utilitymode=utilitymode;
        this.utilityStack = new ArrayDeque<Vertex>();
        this.utilityStack.push(this.start);
        this.utilityStack.push(this.goal);
    }
    
    /**
     * Runs the Astar algorithm with the provided map, starting point and the goal.
     * Saves all information to the Vertex objects on the map.
     * @return the best route as an ArrayList of vertices.
     */
    

    protected ArrayDeque<Vertex> run() {
        
        //INIT
        start.setDistance(0);        
        heap.add(start);     
        start.setOpened(true);
        Vertex vertex;
        ArrayDeque<Vertex> ngbrs;
        
        //ALGO
        while(!heap.isEmpty()){
            vertex = heap.poll();
            //vertex is closed when the algorithm has dealt with it
            vertex.setClosed(true);            
                                                
            //utilitymode used by LosAlgoritmos.closestValidCoordinate
            if(utilitymode){
                if(Tools.valid(vertex.getY(), vertex.getX(), map)){                    
                    ArrayDeque<Vertex> s = new ArrayDeque<Vertex>();
                    s.push(vertex);                    
                    return s;
                }            
            }
            //no utilitymode-> proceed normally
            else {
                //if v == target, stop algo, find the route from path matrix
                if(vertex.equals(goal)) return Tools.shortestPath(goal, start);
            }

            //for all neighbours
            ngbrs = Tools.getNeighbors(map, vertex, directions);
            if(utilitymode) {
                ngbrs = Tools.getAllNeighbors(map, vertex);
                while(!ngbrs.isEmpty()){
                    Vertex v = ngbrs.poll();
                    this.utilityStack.push(v);
                }
                ngbrs = Tools.getAllNeighbors(map, vertex);
            }
          
            while(!ngbrs.isEmpty()){
                Vertex ngbr = ngbrs.poll();
                //no need to process a vertex that has already been dealt with
                if(ngbr.isClosed()) continue;
                //distance == sqrt(2) if diagonal movement to neighbour, else 1
                double distance = vertex.getDistance() + ((ngbr.getX() - vertex.getX() == 0 || ngbr.getY() - vertex.getY() == 0) ? 1 : Math.sqrt(2));                
                
                //relax IF vertex is not opened (not placed to heap yet) OR shorter distance to it has been found
                if(!ngbr.isOpened() || ngbr.getDistance()>distance){
                    ngbr.setDistance(distance);
                    //use appropriate heuristic if necessary, -1 is the default value of distance to goal
                    if(ngbr.getToGoal() == -1) ngbr.setToGoal(Tools.heuristics(ngbr.getY(), ngbr.getX(), this.heuristics, goal));              
                    
                    ngbr.setPath(vertex);
        
                    //if vertex was not yet opened, open it and place to heap. Else update its position in heap.
                    if(!ngbr.isOpened()){
                        heap.add(ngbr);
                        ngbr.setOpened(true);
                    } else {                        
                        boolean wasremoved = heap.remove(ngbr);
                        if(wasremoved) heap.add(ngbr);
                    }                    
                }                                
            }
        }     
        //no route found
        return null;
    }
    

    /**
     * UtilityStack contains all vertices that were processed during utilityMode.
     * It is sufficient to reset only these vertices.
     * @return utilityStack.
     */
    protected ArrayDeque<Vertex> getUtilityStack() {
        return utilityStack;
    }
    
    
    
    
    
}

    
