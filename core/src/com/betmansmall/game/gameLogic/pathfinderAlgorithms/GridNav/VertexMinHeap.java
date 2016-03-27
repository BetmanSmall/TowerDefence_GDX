
package com.betmansmall.game.gameLogic.pathfinderAlgorithms.GridNav;

import java.util.Arrays;

/**
 * Binary VertexMinHeap implementation.
 * Uses an array as the underlying data structure.
 * Supports only vertices as the heap uses vertex.index to track index in 
 * heap which necessary for update operation.
 * @author Elias Nygren
 */
public class VertexMinHeap {

    private Vertex[] heap;
    private int length;
    private int heapSize;
    
    
    /**
     * Initializes VertexMinHeap with the given initial size.
     * @param size 
     */
    
    public VertexMinHeap(int size) {
        length = size + 1;
        heapSize = 0;
        heap = new Vertex[length];
        
        
    }

    /**
     * Insert value i to the heap.
     * @param v Vertex to be added.
     */
    public void add(Vertex v) {
        heapSize++;
        if (length-1 < heapSize) {
            resizeHeap();
        }
        heap[heapSize] = v;
        v.setIndex(heapSize);
                
        int index = heapSize;

        while (hasParent(index) && heap[index].compareTo(heap[parent(index)]) == -1) {
            swap(index, parent(index));
            index = parent(index);
        }   
    }

    /**
     * Remove the min value of the heap.
     * @return min value.
     */
    public Vertex poll(){
        Vertex r = heap[1];
        r.setIndex(-1);
        heap[1] = heap[heapSize--];
        heapify(1);
        return r;
    }
    
    /**
     * Size of the heap.
     * @return heap size.
     */
    
    public int size(){
        return heapSize;
    }
    
    /**
     * True if heap is empty.
     * @return heapSize==0.
     */
    public boolean isEmpty(){
        return heapSize == 0;
    }
    
    /**
     * Update the position of the given value in the heap.
     * @param v the value whose position is to be updated.
     */
    
    public void update(Vertex v){                
        int index = v.getIndex();
        
        while (hasParent(index) && heap[index].compareTo(heap[parent(index)]) == -1) {
            swap(index, parent(index));
            index = parent(index);
        }   
    }
    
    
    private void swap(int a, int b){
        Vertex tmp = heap[a];
        heap[a] = heap[b];        
        heap[b] = tmp;        
        heap[a].setIndex(a);
        tmp.setIndex(b);
    }
    
    private void heapify(int i){
        int l = left(i);
        int r = right(i);
        if(r <= heapSize){
            int largest = r;
            if(heap[l].compareTo(heap[r]) == -1) largest = l;
            if(heap[i].compareTo(heap[largest]) == 1){
                swap(i, largest);
                heapify(largest);
            }
        }else if(l == heapSize && heap[i].compareTo(heap[l])==1) swap(i,l);
    }
    

    private void resizeHeap() {
        heap = Arrays.copyOf(heap, heap.length * 2);
        length = heap.length;
    }


    
    private int left(int i) {
        return 2*i;
    }

    private int right(int i) {
        return 2*i + 1;
    }

    private int parent(int i) {
        return i/2;
    }
    
    private boolean hasParent(int i){
        return i>1;
    }

    /**
     * For testing.
     * Returns the underlying array data stucture.
     * @return 
     */
    public Vertex[] getHeap() {
        return heap;
    }
    
    
}
