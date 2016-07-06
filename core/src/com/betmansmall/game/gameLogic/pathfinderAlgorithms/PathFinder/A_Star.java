package com.betmansmall.game.gameLogic.pathfinderAlgorithms.PathFinder;

import java.util.ArrayDeque;
import java.util.PriorityQueue;

/**
 * Created by betmansmall on 08.02.2016.
 */
public class A_Star {
    private Tools tools;
    private Node[][] map;
    private Node startPoint;
    private Node finishPoint;
    private Options heuristics;
    private PriorityQueue<Node> heap;

    private ArrayDeque<Node> utilityStack;
    private boolean utilitymode = false;

    private String directions;

    protected A_Star(Node[][] map, int[] startPoint, int[] finishPoint, Options heuristics, boolean diagonalMovement){
        this.tools = new Tools();
        this.map = map;
        this.startPoint = map[startPoint[0]][startPoint[1]];
        this.finishPoint = map[finishPoint[0]][finishPoint[1]];
        this.heuristics = heuristics;
        this.heap = new PriorityQueue<Node>(map.length * map[0].length);
        
        this.startPoint.setOnPath(true);
        this.finishPoint.setOnPath(true);
        
        if(diagonalMovement) {
            directions = "12345678";
        } else {
            directions = "1357";
        }
    }

    protected A_Star(Node[][] map, int[] startPoint, int[] finishPoint, Options heuristics, boolean diagonalMovement, boolean utilitymode) {
        this(map, startPoint, finishPoint, heuristics, diagonalMovement);
        this.utilitymode = utilitymode;
        this.utilityStack = new ArrayDeque<Node>();
        this.utilityStack.push(this.startPoint);
        this.utilityStack.push(this.finishPoint);
    }

    protected ArrayDeque<Node> run() {
        startPoint.setDistance(0);
        heap.add(startPoint);
        startPoint.setOpened(true);
        Node node;
        ArrayDeque<Node> ngbrs;

        while(!heap.isEmpty()) {
            node = heap.poll();
            node.setClosed(true);

            if(utilitymode) {
                if(tools.valid(node.getY(), node.getX(), map)) {
                    ArrayDeque<Node> s = new ArrayDeque<Node>();
                    s.push(node);
                    return s;
                }            
            } else {
                if(node.equals(finishPoint)) {
                    return tools.shortestPath(finishPoint, startPoint);
                }
            }

            ngbrs = tools.getNeighbors(map, node, directions);
            if(utilitymode) {
                ngbrs = tools.getAllNeighbors(map, node);
                while(!ngbrs.isEmpty()){
                    Node v = ngbrs.poll();
                    this.utilityStack.push(v);
                }
                ngbrs = tools.getAllNeighbors(map, node);
            }
          
            while(!ngbrs.isEmpty()) {
                Node ngbr = ngbrs.poll();
                if(ngbr.isClosed()) {
                    continue;
                }
                double distance = node.getDistance() + ((ngbr.getX() - node.getX() == 0 || ngbr.getY() - node.getY() == 0) ? 1 : Math.sqrt(2));

                if(!ngbr.isOpened() || ngbr.getDistance()>distance) {
                    ngbr.setDistance(distance);
                    if(ngbr.getToGoal() == -1) {
                        ngbr.setToGoal(tools.heuristics(ngbr.getY(), ngbr.getX(), this.heuristics, finishPoint));
                    }
                    
                    ngbr.setPath(node);

                    if(!ngbr.isOpened()) {
                        heap.add(ngbr);
                        ngbr.setOpened(true);
                    } else {                        
                        boolean wasremoved = heap.remove(ngbr);
                        if(wasremoved) {
                            heap.add(ngbr);
                        }
                    }                    
                }                                
            }
        }
        return null;
    }

    protected ArrayDeque<Node> getUtilityStack() {
        return utilityStack;
    }
}
