package com.example.greengiant.canopy2;

import java.util.ArrayList;

/**
 * Created by Justin on 4/12/2016.
 */
public class VoltageHeap {
    private ArrayList<VoltageReading> heap;

    private static final int FRONT = 0;

    public VoltageHeap() {
        heap = new ArrayList<VoltageReading>();
    }

    private int parent(int pos)
    {
        return pos / 2;
    }

    private int leftChild(int pos)
    {
        return (2 * pos);
    }

    private int rightChild(int pos)
    {
        return (2 * pos) + 1;
    }

    private boolean isLeaf(int pos)
    {
        if (pos >=  (heap.size() / 2)  &&  pos <= heap.size())
        {
            return true;
        }
        return false;
    }

    private void swap(int fpos, int spos)
    {
        VoltageReading tmp;
        tmp = heap.get(fpos);
        heap.set(fpos, heap.get(spos));
        heap.set(spos, tmp);
    }

    private void minHeapify(int pos)
    {
        if (!isLeaf(pos))
        {
            if ( heap.get(pos).getTimestampAsLong() > heap.get(leftChild(pos)).getTimestampAsLong()  || heap.get(pos).getTimestampAsLong() > heap.get(rightChild(pos)).getTimestampAsLong())
            {
                if (heap.get(leftChild(pos)).getTimestampAsLong() < heap.get(rightChild(pos)).getTimestampAsLong())
                {
                    swap(pos, leftChild(pos));
                    minHeapify(leftChild(pos));
                }else
                {
                    swap(pos, rightChild(pos));
                    minHeapify(rightChild(pos));
                }
            }
        }
    }

    public void insert(VoltageReading element)
    {
        heap.add(element);
        int current = heap.size()-1;

        while (heap.get(current).getTimestampAsLong() < heap.get(parent(current)).getTimestampAsLong())
        {
            swap(current,parent(current));
            current = parent(current);
        }
    }

    public int size(){
        return heap.size();
    }

    public void minHeap()
    {
        for (int pos = (heap.size() / 2); pos >= 1 ; pos--)
        {
            minHeapify(pos);
        }
    }

    public VoltageReading remove()
    {
        VoltageReading popped = heap.get(FRONT);
        if(heap.size() > 0) {
            heap.set(FRONT, heap.get(heap.size() - 1));
            heap.remove(heap.size() - 1);
            minHeapify(FRONT);
        }
        return popped;
    }
}
