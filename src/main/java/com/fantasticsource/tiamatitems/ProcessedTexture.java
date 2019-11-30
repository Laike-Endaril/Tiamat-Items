package com.fantasticsource.tiamatitems;

public class ProcessedTexture
{
    public final int width, height;
    public int[][][] pixels;

    public ProcessedTexture(int width, int height)
    {
        this.width = width;
        this.height = height;

        pixels = new int[width][][];
        for (int x = 0; x < width; x++)
        {
            pixels[x] = new int[height][];
            for (int y = 0; y < height; y++)
            {
                pixels[x][y] = new int[]{(int) (255 * ((double) x / width)), 255, (int) (255 * ((double) y / height)), 90};
            }
        }
    }
}
