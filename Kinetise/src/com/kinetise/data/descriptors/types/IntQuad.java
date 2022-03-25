package com.kinetise.data.descriptors.types;

public class IntQuad {
    public int top;
    public int bottom;
    public int right;
    public int left;

    public boolean isAllZeros(){
        return (top == 0 && bottom == 0 && right == 0 && left == 0);
    }
}
