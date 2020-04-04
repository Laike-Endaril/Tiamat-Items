package com.fantasticsource.tiamatitems;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.PNG;
import com.fantasticsource.tools.Tools;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class TextureCache
{
    public static LinkedHashMap<String, Texture> textures = new LinkedHashMap<>();
    private static HashSet<String> rawTextures = new HashSet<>();

    public static void clear(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        textures.entrySet().removeIf(o -> !rawTextures.contains(o.getKey()));
    }


    public static void addRawTextureLayers()
    {
        String mainTextureDir = MCTools.getConfigDir() + MODID;

        int loaded = 0;
        File file = new File(mainTextureDir);
        if (!file.exists()) file.mkdir();
        for (String relativeFilename : Tools.allRecursiveRelativeFilenames(mainTextureDir))
        {
            if (!relativeFilename.substring(relativeFilename.lastIndexOf(".") + 1).equals("png")) continue;


            String fullFilePathAndName = mainTextureDir + '/' + relativeFilename;

            PNG png = PNG.load(fullFilePathAndName);
            if (!png.isLoaded()) continue;


            int width = png.getWidth(), height = png.getHeight();
            int min = Tools.min(width, height);
            int max = Tools.max(width, height);
            if (max % min != 0 || !Tools.isPowerOfTwo(min))
            {
                png.free();
                System.err.println("Dynamic textures should be a single row or single column, and each cell of a dynamic texture should be a power of two!  Problem file: " + fullFilePathAndName);
                continue;
            }


            int count = max / min;
            Texture[] textures = new Texture[count];
            for (int i = 0; i < count; i++) textures[i] = new Texture(min, min);

            ByteBuffer buffer = png.getDirectBuffer();

            for (int y = 0; y < png.getHeight(); y++)
            {
                for (int x = 0; x < png.getWidth(); x++)
                {
                    for (int i = 0; i < 4; i++)
                    {
                        textures[x / min + y / min].colors[x % min][y % min][i] = buffer.get() & 0xff;
                    }
                }
            }

            int i = 0;
            for (Texture texture : textures)
            {
                String textureName = relativeFilename.substring(0, relativeFilename.length() - 4) + ":" + i++ + ":ffffffff";
                TextureCache.rawTextures.add(textureName);
                TextureCache.textures.put(textureName, texture);
            }

            png.free();

            loaded += count;
        }

        System.out.println("Cached " + loaded + " raw texture" + (loaded == 1 ? "" : "s"));
    }

    public static String[] getUncoloredTextureNames()
    {
        String[] result = rawTextures.toArray(new String[0]);
        for (int i = 0; i < result.length; i++)
        {
            result[i] = result[i].substring(0, result[i].lastIndexOf(':'));
        }
        return result;
    }
}
