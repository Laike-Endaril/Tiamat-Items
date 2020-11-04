package com.fantasticsource.tiamatitems.trait.unrecalculable.element;

import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTraitElement;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes.CRandomRGB;
import com.fantasticsource.tools.Tools;
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
import java.util.regex.Pattern;

public class CUTraitElement_AWDyeChannelOverride extends CUnrecalculableTraitElement
{
    public LinkedHashMap<Integer, CRandomRGB> dyeChannels = new LinkedHashMap<>();

    @Override
    public int requiredArgumentCount()
    {
        return 0;
    }

    @Override
    public String getDescriptionInternal(ArrayList<Integer> baseArgs, double[] multipliedArgs)
    {
        return "AW Dye Channel Override";
    }

    @Override
    public void applyToItemInternal(ItemStack stack, int[] baseArgs, double[] multipliedArgs)
    {
        //Dyes
        LinkedHashMap<Integer, Color> dyes = new LinkedHashMap<>();
        for (Map.Entry<Integer, CRandomRGB> entry : dyeChannels.entrySet()) dyes.put(entry.getKey(), entry.getValue().generate());


        String stackString = stack.getTagCompound().toString();
        String[] tokens = Tools.preservedSplit(stackString, "dyeData:" + Pattern.quote("{") + "[^" + Pattern.quote("}") + "]*" + Pattern.quote("}"), true);

        StringBuilder newStackString = new StringBuilder();
        int i = 0;
        for (String token : tokens)
        {
            if (i++ % 2 == 0)
            {
                newStackString.append(token);
            }
            else
            {
                token = token.substring(0, token.length() - 1).replaceAll("dyeData:" + Pattern.quote("{"), "");
                newStackString.append("dyeData:{");

                LinkedHashMap<Integer, Color> dyesCopy = new LinkedHashMap<>(dyes);
                String[] oldDyeStrings = Tools.fixedSplit(token, ",");
                boolean started = false;
                if (oldDyeStrings.length >= 4)
                {
                    for (String oldDyeString : oldDyeStrings)
                    {
                        int colonIndex = oldDyeString.indexOf(":");

                        int dyeChannel = Integer.parseInt(oldDyeString.substring(0, colonIndex - 1).substring(3));
                        Color color = dyesCopy.remove(dyeChannel);
                        if (color == null) color = dyes.get(dyeChannel); //Because we might've already removed the color, since we only grab 1 of 4 values from that color at a time (rgbt)
                        if (color == null) continue;

                        String dyeLetter = oldDyeString.substring(colonIndex - 1, colonIndex - 1);
                        newStackString.append(started ? ",dye" : "dye").append(dyeChannel).append(dyeLetter).append(":");
                        started = true;

                        switch (dyeLetter)
                        {
                            case "r":
                                newStackString.append((byte) color.r());
                                break;

                            case "g":
                                newStackString.append((byte) color.g());
                                break;

                            case "b":
                                newStackString.append((byte) color.b());
                                break;

                            case "t":
                                newStackString.append((byte) color.a());
                                break;
                        }
                    }
                }

                for (Map.Entry<Integer, Color> entry : dyesCopy.entrySet())
                {
                    int dyeChannel = entry.getKey();
                    Color color = entry.getValue();
                    newStackString.append(started ? ",dye" : "dye").append(dyeChannel).append("r:").append((byte) color.r());
                    started = true;
                    newStackString.append(",dye").append(dyeChannel).append("g:").append((byte) color.g());
                    newStackString.append(",dye").append(dyeChannel).append("b:").append((byte) color.b());
                    newStackString.append(",dye").append(dyeChannel).append("t:").append((byte) color.a());
                }

                newStackString.append("}");
            }
        }


        try
        {
            stack.setTagCompound(JsonToNBT.getTagFromJson(newStackString.toString()));
        }
        catch (NBTException e)
        {
            e.printStackTrace();
            return;
        }


        MiscTags.setDyeOverrides(stack, dyes);
    }

    @Override
    public CUTraitElement_AWDyeChannelOverride write(ByteBuf buf)
    {
        super.write(buf);

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
        super.read(buf);

        dyeChannels.clear();
        for (int i = buf.readInt(); i > 0; i--) dyeChannels.put(buf.readInt(), new CRandomRGB().read(buf));

        return this;
    }

    @Override
    public CUTraitElement_AWDyeChannelOverride save(OutputStream stream)
    {
        super.save(stream);

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
        super.load(stream);

        CInt ci = new CInt();
        dyeChannels.clear();
        for (int i = ci.load(stream).value; i > 0; i--) dyeChannels.put(ci.load(stream).value, new CRandomRGB().load(stream));

        return this;
    }
}
