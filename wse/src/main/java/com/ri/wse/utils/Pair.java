package com.ri.wse.utils;

public class Pair<U, V> {

    public final U first;
    public final V second;

    public Pair(U first, V second){
        this.first = first;
        this.second = second;
    }

    public U getKey(){
        return first;
    }

    public V getValue(){
        return second;
    }

}
