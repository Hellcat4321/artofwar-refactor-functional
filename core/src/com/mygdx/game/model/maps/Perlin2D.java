package com.mygdx.game.model.maps;

import java.util.Random;

public class Perlin2D {
    private Random random;
    private double degree=2;
    public Perlin2D(double seed) {
        random = new Random((long)seed);
    }
    public void setRandom(double seed){
        this.random = new Random((long) seed);
    }

    public double getNoiseStandart(double x, double y) {
        int left = (int)x;
        int top = (int)y;

        double localX = x - left;
        double localY = y - top;

        // ��������� ����������� ������� ��� ���� ������ ��������:
        Vector topLeftGradient     = getPseudoRandomGradientVector(left,   top  );
        Vector topRightGradient    = getPseudoRandomGradientVector(left+1, top  );
        Vector bottomLeftGradient  = getPseudoRandomGradientVector(left,   top+1);
        Vector bottomRightGradient = getPseudoRandomGradientVector(left+1, top+1);

        // ������� �� ������ �������� �� ����� ������ ��������:
        Vector distanceToTopLeft     = new Vector(localX,   localY);
        Vector distanceToTopRight    = new Vector(localX-1, localY);
        Vector distanceToBottomLeft  = new Vector(localX,   localY-1);
        Vector distanceToBottomRight = new Vector(localX-1, localY-1);

        // ������� ��������� ������������ ����� �������� ����� ���������������
        double tx1 = dot(distanceToTopLeft,     topLeftGradient);
        double tx2 = dot(distanceToTopRight,    topRightGradient);
        double bx1 = dot(distanceToBottomLeft,  bottomLeftGradient);
        double bx2 = dot(distanceToBottomRight, bottomRightGradient);

        // ����������, ������������:
        double tx = lerp(tx1, tx2, localX);
        double bx = lerp(bx1, bx2, localX);
        double tb = lerp(tx, bx, localY);

        return tb;
    }

    private double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private double dot(Vector a, Vector b) {
        return a.x * b.x + a.y * b.y;
    }

    private Vector getPseudoRandomGradientVector(int x, int y) {
        // ������-��������� ����� �� 0 �� 3 ������� ������ ��������� ��� ������ x � y
        int v = random.nextInt(4);

        switch (v)
        {
            case 0:  return new Vector(1, 0);
            case 1:  return new Vector(-1, 0);
            case 2:  return new Vector(0, 1);
            default: return new Vector(0,-1);
        }
    }

    private static class Vector {
        double x;
        double y;
        Vector(double x, double y) {
            this.x = x;
            this.y = y;
        }

    }

    public double getNoise(double fx, double fy, int octaves, double persistence) {
        double amplitude = 1;
        double max = 0;
        double result = 0;

        while (octaves-- > 0)
        {
            max += amplitude;
            result += getNoiseStandart(fx, fy) * amplitude;
            amplitude *= persistence;
            fx *= 2;
            fy *= 2;
        }
        return result/max;
    }
}