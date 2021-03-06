package com.fantasticsource.tiamatitems.trait.recalculable.element;

import com.fantasticsource.tiamatitems.nbt.TextureTags;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CRTraitElement_TextureLayers extends CRecalculableTraitElement
{
    //See AssemblyTags.STATE_
    public boolean cacheLayers = false, cacheTextures = true;
    public LinkedHashMap<Integer, ArrayList<String>> layerGroups = new LinkedHashMap<>();

    @Override
    public int requiredArgumentCount()
    {
        return 0;
    }

    @Override
    public void applyToItem(ItemStack stack, int[] args, double itemTypeAndLevelMultiplier)
    {
        if (cacheLayers) TextureTags.addItemLayerCacheTag(stack);
        else TextureTags.removeItemLayerCacheTag(stack);

        if (cacheTextures) TextureTags.addItemTextureCacheTag(stack);
        else TextureTags.removeItemTextureCacheTag(stack);

        for (Map.Entry<Integer, ArrayList<String>> entry : layerGroups.entrySet())
        {
            int state = entry.getKey();
            for (String layer : entry.getValue())
            {
                TextureTags.addItemLayer(stack, state, layer);
            }
        }
    }

    @Override
    public String getDescription(ItemStack stack, int[] args, double itemTypeAndLevelMultiplier)
    {
        return "Adds textures to the itemstack";
    }


    @Override
    public CRTraitElement_TextureLayers write(ByteBuf buf)
    {
        super.write(buf);

        buf.writeBoolean(cacheLayers);
        buf.writeBoolean(cacheTextures);

        buf.writeInt(layerGroups.size());
        for (Map.Entry<Integer, ArrayList<String>> entry : layerGroups.entrySet())
        {
            buf.writeInt(entry.getKey());

            buf.writeInt(entry.getValue().size());
            for (String layer : entry.getValue()) ByteBufUtils.writeUTF8String(buf, layer);
        }

        return this;
    }

    @Override
    public CRTraitElement_TextureLayers read(ByteBuf buf)
    {
        super.read(buf);

        cacheLayers = buf.readBoolean();
        cacheTextures = buf.readBoolean();

        layerGroups.clear();
        for (int i = buf.readInt(); i > 0; i--)
        {
            ArrayList<String> layerGroup = new ArrayList<>();
            layerGroups.put(buf.readInt(), layerGroup);

            for (int i2 = buf.readInt(); i2 > 0; i2--) layerGroup.add(ByteBufUtils.readUTF8String(buf));
        }

        return this;
    }

    @Override
    public CRTraitElement_TextureLayers save(OutputStream stream)
    {
        super.save(stream);

        new CBoolean().set(cacheLayers).save(stream).set(cacheTextures).save(stream);

        CStringUTF8 cs = new CStringUTF8();
        CInt ci = new CInt().set(layerGroups.size()).save(stream);
        for (Map.Entry<Integer, ArrayList<String>> entry : layerGroups.entrySet())
        {
            ci.set(entry.getKey()).save(stream).set(entry.getValue().size()).save(stream);
            for (String layer : entry.getValue()) cs.set(layer).save(stream);
        }

        return this;
    }

    @Override
    public CRTraitElement_TextureLayers load(InputStream stream)
    {
        super.load(stream);

        CBoolean cb = new CBoolean();
        cacheLayers = cb.load(stream).value;
        cacheTextures = cb.load(stream).value;

        CStringUTF8 cs = new CStringUTF8();
        CInt ci = new CInt();

        layerGroups.clear();
        for (int i = ci.load(stream).value; i > 0; i--)
        {
            ArrayList<String> layerGroup = new ArrayList<>();
            layerGroups.put(ci.load(stream).value, layerGroup);

            for (int i2 = ci.load(stream).value; i2 > 0; i2--) layerGroup.add(cs.load(stream).value);
        }

        return this;
    }
}
