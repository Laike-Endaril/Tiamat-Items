package com.fantasticsource.tiamatitems.cache;

import com.fantasticsource.tiamatitems.ProcessedTexture;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class ProcessedTextureCache
{
    public static LinkedHashMap<Reference, ProcessedTexture> textures = new LinkedHashMap<>();

    public static void clear(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        textures.clear();
    }

    public static class Reference
    {
        public HashSet<String> layers = new HashSet<>();

        public Reference(String... layers)
        {
            this.layers.addAll(Arrays.asList(layers));
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof Reference)) return false;

            Reference other = (Reference) obj;
            if (other.layers.size() != layers.size()) return false;

            for (String layer : layers) if (!other.layers.contains(layer)) return false;
            return true;
        }
    }
}
