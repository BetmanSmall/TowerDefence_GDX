package com.betmansmall.game.gameLogic.pathfinderAlgorithms.GridNav;

import java.util.ArrayDeque;
import java.util.PriorityQueue;

/**
 * Implementation of the Jump Point Search algorithm with self-made data structures.
 * @author Elias Nygren
 */
public class JPS {
    private PriorityQueue<Vertex> heap;
    private Vertex[][] map;
    private Vertex start;
    private Vertex goal;
    private String directions;
    private Options heuristics;
    private Tools Tools;
    
    
    protected JPS(Vertex[][] map, int[] start, int[] goal, Options heuristics, boolean diagonalMovement){
        Tools = new Tools();
        this.map = map;
        this.heap = new PriorityQueue<Vertex>(map.length*map[0].length);
        this.start = map[start[0]][start[1]];
        this.goal = map[goal[0]][goal[1]];
        this.heuristics=heuristics;
        
        this.start.setOnPath(true);
        this.goal.setOnPath(true);
        
        directions = "12345678";
    }
    

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
//            System.out.println(vertex);
            //vertex is closed when the algorithm has dealt with it
            vertex.setClosed(true);            
            //if v == target, stop algo, find the route from path matrix
            if(vertex.equals(goal)) return Tools.shortestPath(goal, start);
            

            //IDENTIFY SUCCESSORS:
            
            //for all neighbours
            ngbrs = getNeighbors(vertex);
            while(!ngbrs.isEmpty()){
                Vertex ngbr = ngbrs.poll();
                //find next jumpPoint
                int[] jumpCoord = jump(ngbr.getX(), ngbr.getY(), vertex.getX(), vertex.getY());

                
                if(jumpCoord!=null){
                    Vertex jumpPoint = map[jumpCoord[1]][jumpCoord[0]];

                    //no need to process a jumpPoint that has already been dealt with
                    if(jumpPoint.isClosed()) continue;
                                        
                    //distance == distance of parent and from parent to jumpPoint                                        
                    double distance = Tools.heuristics(jumpPoint.getY(), jumpPoint.getX(), Options.DIAGONAL_HEURISTIC, vertex) + vertex.getDistance();
                    
                    //relax IF vertex is not opened (not placed to heap yet) OR shorter distance to it has been found
                    if(!jumpPoint.isOpened() || jumpPoint.getDistance()>distance){
                        jumpPoint.setDistance(distance);
                        
                        //use appropriate heuristic if necessary (-1 is the default value of distance to goal, so heuristic not used if still -1)
                        if(jumpPoint.getToGoal() == -1) jumpPoint.setToGoal(Tools.heuristics(jumpPoint.getY(), jumpPoint.getX(), this.heuristics, goal));
                        
                        jumpPoint.setPath(vertex);


                        //if vertex was not yet opened, open it and place to heap. Else update its position in heap.
                        if(!jumpPoint.isOpened()){                            
                            heap.add(jumpPoint);
                            jumpPoint.setOpened(true);
                        } else {
                            heap.remove(jumpPoint);
                            heap.add(jumpPoint);
                        }                    
                    }
                }                            
            }
        }
        //no route found
        return null;
    }
    
    
    /**
     * Find next jump point
     * @param y neighbor y.
     * @param x neighbor x.
     * @param py vertex (parent of neighbor) y.
     * @param px vertex (parent of neighbor) x.
     * @return jump point y, x in array
     */
    private int[] jump(int x, int y, int px, int py){
        if (!Tools.valid(y, x, map)) {
            return null;
        }    
                
        if(map[y][x].equals(goal)) {
            return new int[] {x, y};
        }
        
        int dx = x - px; int dy = y - py;
        
        //diagonal search
        if (dx != 0 && dy != 0){
            if((Tools.valid(y+dy, x-dx, map) && !Tools.valid(y,x-dx, map))||
               (Tools.valid(y-dy,x+dx, map) && !Tools.valid(y-dy,x, map))){
                return new int[] {x, y};
            }
        } else { //vertical search
            if( dx != 0 ) { 
                //jumpnode if has forced neighbor
                if((Tools.valid(y+1,x+dx, map) && !Tools.valid(y+1,x, map))||
                   (Tools.valid(y-1,x+dx, map) && !Tools.valid(y-1,x, map))){
                    return new int[] {x, y};
                }                
            } else { //horizontal search
                //jupmnode if has forced neighbor
                if((Tools.valid(y+dy,x+1, map) && !Tools.valid(y,x+1, map))||
                   (Tools.valid(y+dy,x-1, map) && !Tools.valid(y,x-1, map))){
                    return new int[] {x, y};
             }
            }
        }
            
        //when moving diagonally, must perform horizontal and vertical search
        if (dx != 0 && dy != 0) {
            if(jump(x + dx, y, x, y)!=null) return new int[] {x, y};
            if(jump(x, y + dy, x, y)!=null) return new int[] {x, y};
        }

        //diagonal search recursively
        if(Tools.valid(y,x+dx, map)|| Tools.valid(y+dy,x, map)){
            return jump(x+dx, y+dy, x, y);
        } else {
            return null;
        }
    }
    
    
    
    

    
    /**
    * Get the neighbors of the vertex.
    * No parent (first vertex) -> return all neighbors, otherwise prune.
    * @return neighbors in queue.
    */    
    private ArrayDeque<Vertex> getNeighbors(Vertex u){
        ArrayDeque<Vertex> ngbrs = new ArrayDeque<Vertex>();
        Vertex parent = u.getPath();
        
        if(parent!=null){             
            //get direction of movement
            int dy = (u.getY() - parent.getY()) / Math.max(Math.abs(u.getY() - parent.getY()), 1);
            int dx = (u.getX() - parent.getX()) / Math.max(Math.abs(u.getX() - parent.getX()), 1);
            int y = u.getY();
            int x = u.getX();
            
            //helper booleans, optimization
            boolean validY=false;
            boolean validX=false;
            
            //CHECK NEIGHBORS
            
            //diagonally
             if(dx!=0 && dy!=0){        
                 
                //natural neighbors
                               if(Tools.valid(y + dy,x, map)) {
                    ngbrs.add(map[y+dy][x]);
                    validY=true;
                }
                if(Tools.valid(y,x+dx, map)){
                    ngbrs.add(map[y][x+dx]);
                    validX=true;
                }
                if(validY || validX){
                    if(Tools.valid(y+dy,x+dx, map)) { //caused nullpointer without check at one point, no harm in making sure...
                        ngbrs.add(map[y+dy][x+dx]);
                    }                    
                }
                
                //forced neighbors
                if(!Tools.valid(y,x-dx, map) && validY){
                    ngbrs.add(map[y+dy][x-dx]);
                }
                if(!Tools.valid(y-dy,x, map) && validX){
                    ngbrs.add(map[y-dy][x+dx]);
                }
            //vertically                   
            } else {
                if(dx==0){
                    if (Tools.valid(y + dy,x, map)) {
                        //natural neighbor
//                        if (Tools.valid(y + dy,x, map)) {
                            ngbrs.add(map[y+dy][x]);
//                        }
                        //forced neigbors
                        if (!Tools.valid(y,x+1, map)) {
                            ngbrs.add(map[y+dy][x+1]);
                        }
                        if (!Tools.valid(y,x-1, map)) {
                            ngbrs.add(map[y+dy][x-1]);
                        }
                    }
                } else {//horizontally
                    //natural neighbors
                    if (Tools.valid(y,x + dx, map)) {
//                        if (Tools.valid(y,x+dx, map)) {
                        ngbrs.add(map[y][x+dx]);
//                        }
                        
                        //forced neighbors
                        if (!Tools.valid(y+1,x, map)) {
                            ngbrs.add(map[y+1][x+dx]);
                        }
                        if (!Tools.valid(y-1,x, map)) {
                            ngbrs.add(map[y-1][x+dx]);
                        }
                    }
                }
                
            }        
        } else {
            //no pruning - get all ngbrs normally    
            ngbrs = Tools.getNeighbors(map, u, directions);
        }
        return ngbrs;
    }
    
}
