/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.betmansmall.game.gameLogic.GridNav;

import java.util.ArrayDeque;
import java.util.Random;


/**
 * Gathered tools used by routing algorithms, tests and GUI.
 * With Java built in data structures.
 * @author Elias Nygren
 */
public class Tools {
    
     /**
     * Implementations of A* heuristics.
     * When Dijkstra (no heuristics) selected, returns -1
     * @param y y coordinate of current vertex
     * @param x x coordinate of current vertex
     * @param heuristic selected heuristic
     * @return estimate distance to goal
     */
    protected double heuristics(int y, int x, Options heuristic, Vertex whereTo){
        int dx = Math.abs(y - whereTo.getY());
        int dy = Math.abs(x - whereTo.getX());
        if(heuristic==Options.MANHATTAN_HEURISTIC) return dy + dx;                
        if(heuristic==Options.DIAGONAL_HEURISTIC) return (dx + dy) + (Math.sqrt(2) - 2) * Math.min(dx, dy);
        if(heuristic==Options.DIAGONAL_EQUAL_COST_HEURISTIC) return Math.max(dx, dy);
        if(heuristic==Options.EUCLIDEAN_HEURISTIC) return Math.sqrt(dx * dx + dy * dy);
        return -1;
    }
    
    
    /**
     * Finds the shortest path from Vertex[][] path.
     * Used by the run() method after finding the goal.
     * @param goal The vertex at the goal (end point).
     * @param start The vertex at the start.
     * @return the best route as an ArrayList of vertices.
     */
    protected ArrayDeque<Vertex> shortestPath(Vertex goal,  Vertex start){
        ArrayDeque<Vertex> pino = new ArrayDeque<Vertex>();
        pino.push(goal);
        if(goal.equals(start)) return pino;
        Vertex u = goal.getPath();
        while(!u.equals(start)){     
            u.setOnPath(true);
            pino.push(u);
            u = u.getPath();
        }         
        pino.push(u);
        start.setOnPath(true);

        return pino;
    }
    
    
    /**
     * Gets the neighbors of a given vertex on the map.
     * @param u the vertex whose neighbors are desired.
     * @return a list of the neighbors
     */
    protected ArrayDeque<Vertex> getNeighbors(Vertex[][] map, Vertex u, String directions){
        ArrayDeque<Vertex> ngbrs = new ArrayDeque<Vertex>();
        for(char c : directions.toCharArray()){
            Vertex v = getNeighbor(map, u, c);    
            //if v valid (within the map)
            if(v!=null){
                if(v.isWalkable()) ngbrs.add(v);
            }            
        }
        return ngbrs;                
    }
    
    /**
     * Gets the all, walkable or not, neighbors of a given vertex on the map.
     * @param u the vertex whose neighbors are desired.
     * @return a list of the neighbors
     */
    
    protected ArrayDeque<Vertex> getAllNeighbors(Vertex[][] map, Vertex u){
        ArrayDeque<Vertex> ngbrs = new ArrayDeque<Vertex>();
        for(char c : "12345678".toCharArray()){
            Vertex v = getNeighbor(map, u, c);   
            if(v!=null) ngbrs.add(v);
        }
        return ngbrs; 
    }
    
    /**
     * Gets the neighbor in the specified direction - left, up, right, down, 
     * @param u the vertex whose neighbor is desired
     * @param c the direction from which the neighbor is desired, format: L/U/R/D
     * @return null if the direction is out of map, otherwise the neighbor vertex.
     */
    protected Vertex getNeighbor(Vertex[][] map, Vertex u, char c){
        int i = 0; int j=0;        
        if(c=='1'){ --j;} //left
        else if(c=='2'){ --j; --i;} //left&up
        else if(c=='3'){ --i;} //up
        else if(c=='4'){ --i; ++j;} //righ&up
        else if(c=='5'){ ++j; } //right
        else if(c=='6'){ ++i; ++j;} //right&down
        else if(c=='7'){ ++i; } //down
        else if(c=='8'){ ++i; --j;} //right&up

        int x = u.getX()+j;
        int y = u.getY()+i;
        
        //check if within bounds of the matrix
        if(x<0 || y < 0 ||x>=map[0].length || y>=map.length){
            return null;
        } else {
            return map[y][x];
        }       
    }
    
    
    /**
     * True if coordinate is walkable and within the map, false otherwise.
     * @param y y coordinate.
     * @param x x coordinate.
     * @param map Vertex[][] map.
     * @return boolean valid.
     */
    protected boolean valid(int y, int x, Vertex[][] map){        
        if(x<0 || y < 0 ||x>=map[0].length || y>=map.length) return false;
        return map[y][x].isWalkable();
    }
    
    
    /**
     * Returns a random, valid, point on the map.
     * @param map
     * @return 
     */
    protected int[] randomPoint(Vertex[][] map){
        Random r = new Random();
        int x = r.nextInt(map[0].length);
        int y = r.nextInt(map.length);
        return closestValidCoordinate(map, new int[] {y,x});
    }
    
    
    /**
     * Checks that coordinate is valid, if not, returns the closest valid coordinate.
     * Moves coordinate to within the map and uses Dijkstra(Astar, NO_HEURISTIC) with utilitymode.
     * @param coord coordinate to be validated.
     * @return closest valid coordinate.
     */
    protected int[] closestValidCoordinate(Vertex[][] vertexMatrix, int[] coord){        
        if(vertexMatrix==null) return null;
        if(valid(coord[0], coord[1], vertexMatrix)) return coord;                
        
        int y = coord[0];
        int x = coord[1];
        
        //move coordinate within the map, if outside
        if(y<0) y=0;
        if(y>=vertexMatrix.length) y=vertexMatrix.length-1;
        if(x<0) x=0;
        if(x>=vertexMatrix[0].length) x=vertexMatrix[0].length-1;
        int[] newcoord = new int[] {y,x};
        
        //dijkstra
        Astar A = new Astar(vertexMatrix, newcoord, newcoord, Options.NO_HEURISTIC, true, true);
        ArrayDeque<Vertex> s = A.run();
        Vertex v = s.pop();
        y = v.getY();
        x = v.getX();
        
        //undo any changes made by dijkstra
        ArrayDeque<Vertex> utilityStack = A.getUtilityStack();
        while(!utilityStack.isEmpty()){
            Vertex vertex = utilityStack.pop();
            vertex.setClosed(false);
            vertex.setOnPath(false);
            vertex.setDistance(-1);
            vertex.setToGoal(-1);
            vertex.setOpened(false);
        }

        return new int[] {y, x};
        
    }
}
