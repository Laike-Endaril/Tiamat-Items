package com.fantasticsource.tiamatitems.trait.unrecalculable.element;

import com.fantasticsource.tiamatitems.dyes.CRandomRGB;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTraitElement;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.datastructures.Color;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CUTraitElement_AWDyeChannelOverride extends CUnrecalculableTraitElement
{
    public LinkedHashMap<Integer, CRandomRGB> dyeChannels = new LinkedHashMap<>();

    @Override
    public int requiredArgumentCount()
    {
        return 0;
    }

    @Override
    public String getDescription(ArrayList<Integer> baseArgs, double[] multipliedArgs)
    {
        return "AW Dye Channel Override";
    }

    @Override
    public void applyToItem(ItemStack stack, int[] baseArgs, double[] multipliedArgs)
    {
        //Dyes
        LinkedHashMap<Integer, Color> dyes = new LinkedHashMap<>();
        for (Map.Entry<Integer, CRandomRGB> entry : dyeChannels.entrySet()) dyes.put(entry.getKey(), entry.getValue().generate());

        MiscTags.setDyeOverrides(stack, dyes);

        String stackString = stack.serializeNBT().toString();
        for (Map.Entry<Integer, Color> entry : dyes.entrySet())
        {
            int channel = entry.getKey();
            Color color = entry.getValue();
            stackString.replaceAll("dye" + channel + "r:[^b]*b", "dye" + channel + "r:" + (byte) color.r() + "b");
            stackString.replaceAll("dye" + channel + "g:[^b]*b", "dye" + channel + "g:" + (byte) color.g() + "b");
            stackString.replaceAll("dye" + channel + "b:[^b]*b", "dye" + channel + "b:" + (byte) color.b() + "b");
            stackString.replaceAll("dye" + channel + "t:[^b]*b", "dye" + channel + "t:" + (byte) color.a() + "b");
        }

        try
        {
            stack.setTagCompound(JsonToNBT.getTagFromJson(stackString));
        }
        catch (NBTException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public CUTraitElement_AWDyeChannelOverride write(ByteBuf buf)
    {
        buf.writeInt(dyeChannels.size());
        for (Map.Entry<Integer, CRandomRGB> entry : dyeChannels.entrySet())
        {
            buf.writeInt(entry.getKey());
            entry.getValue().write(buf);
        }

        return this;
    }

    @Override
    public CUTraitElement_AWDyeChannelOverride read(ByteBuf buf)
    {
        dyeChannels.clear();
        for (int i = buf.readInt(); i > 0; i--) dyeChannels.put(buf.readInt(), new CRandomRGB().read(buf));

        return this;
    }

    @Override
    public CUTraitElement_AWDyeChannelOverride save(OutputStream stream)
    {
        CInt ci = new CInt();
        ci.set(dyeChannels.size()).save(stream);
        for (Map.Entry<Integer, CRandomRGB> entry : dyeChannels.entrySet())
        {
            ci.set(entry.getKey()).save(stream);
            entry.getValue().save(stream);
        }

        return this;
    }

    @Override
    public CUTraitElement_AWDyeChannelOverride load(InputStream stream)
    {
        CInt ci = new CInt();
        dyeChannels.clear();
        for (int i = ci.load(stream).value; i > 0; i--) dyeChannels.put(ci.load(stream).value, new CRandomRGB().load(stream));

        return this;
    }
}
