package com.fantasticsource.tiamatitems;

public class TextureLayer
{
    public final int width, height;
    public int xScale, yScale;
    public int[][][] colors;

    public TextureLayer(int width, int height)
    {
        this.width = width;
        this.height = height;

        colors = new int[width][][];
        for (int x = 0; x < width; x++)
        {
            colors[x] = new int[height][];
            for (int y = 0; y < height; y++) colors[x][y] = new int[4];
        }
    }
}
