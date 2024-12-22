package com.mygdx.game.utils;

public class HexagonUtils {
    public static float countXLayout(int i,int j){
        return 0.75f * i;
    }
    public static float countYLayout(int i, int j){
        float res = j;
        if((i&1)==0) res+=0.5f;
        return res;
    }
}
