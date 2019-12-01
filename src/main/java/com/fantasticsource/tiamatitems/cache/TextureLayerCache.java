package com.fantasticsource.tiamatitems.cache;

import com.fantasticsource.tiamatitems.TextureLayer;
import com.fantasticsource.tools.PNG;
import com.fantasticsource.tools.Tools;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;

public class TextureLayerCache
{
    public static LinkedHashMap<String, TextureLayer> layers = new LinkedHashMap<>();

    public static int addTextureLayers(String fullFilePathAndName, String name)
    {
        PNG png = PNG.load(fullFilePathAndName);
        if (!png.isLoaded()) return 0;


        int width = png.getWidth(), height = png.getHeight();
        int min = Tools.min(width, height);
        int max = Tools.max(width, height);
        if (max % min != 0 || !Tools.isPowerOfTwo(min))
        {
            png.free();
            System.err.println("Dynamic textures should be a single row or single column, and 3ach cell of a dynamic texture should be a power of two!  Problem file: " + fullFilePathAndName);
            return 0;
        }


        int count = max / min;
        TextureLayer[] layers = new TextureLayer[count];
        for (int i = 0; i < count; i++) layers[i] = new TextureLayer(min, min);

        ByteBuffer buffer = png.getDirectBuffer();

        for (int y = 0; y < png.getHeight(); y++)
        {
            for (int x = 0; x < png.getWidth(); x++)
            {
                for (int i = 0; i < 4; i++)
                {
                    layers[x / min + y / min].colors[x % min][y % min][i] = buffer.get() & 0xff;
                }
            }
        }

        int i = 0;
        for (TextureLayer layer : layers)
        {
            TextureLayerCache.layers.put(name.substring(0, name.length() - 4) + ":" + i++ + ":ffffffff", layer);
        }

        png.free();

        return i;
    }
}
