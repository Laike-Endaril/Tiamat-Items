package com.fantasticsource.tiamatitems.trait.element;

import com.fantasticsource.tiamatitems.trait.CTraitElement;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.datastructures.Color;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CTraitElement_AWSkin extends CTraitElement
{
    public String libraryFile = "", skinType = "";
    public ArrayList<Color> dyes = new ArrayList<>(); //The alpha of these colors is used for the AW paint type
    //TODO when editing, get paint types from PaintRegistry.REGISTERED_TYPES (use the alpha of the Color for the paint type)


    @Override
    public int requiredArgumentCount()
    {
        return 0;
    }


    @Override
    public String getDescription(ArrayList<Integer> baseArgs, double[] multipliedArgs)
    {
        return "AW Skin: " + libraryFile;
    }


    @Override
    public void applyToItem(ItemStack stack, ArrayList<Integer> baseArgs, double[] multipliedArgs)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());

        NBTTagCompound compound = new NBTTagCompound();
        stack.getTagCompound().setTag("armourersWorkshop", compound);

        NBTTagCompound compound2 = new NBTTagCompound();
        compound.setTag("identifier", compound2);

        compound2.setString("libraryFile", libraryFile);
        compound2.setString("skinType", skinType);

        compound2 = new NBTTagCompound();
        compound.setTag("dyeData", compound2);

        int i = 0;
        for (Color color : dyes)
        {
            compound2.setByte("dye" + i + "r", (byte) color.r());
            compound2.setByte("dye" + i + "g", (byte) color.g());
            compound2.setByte("dye" + i + "b", (byte) color.b());
            compound2.setByte("dye" + i + "t", (byte) color.a());
            i++;
        }
    }


    @Override
    public CTraitElement_AWSkin write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, libraryFile);
        ByteBufUtils.writeUTF8String(buf, skinType);

        buf.writeInt(dyes.size());
        for (Color color : dyes) buf.writeInt(color.color());

        return this;
    }

    @Override
    public CTraitElement_AWSkin read(ByteBuf buf)
    {
        libraryFile = ByteBufUtils.readUTF8String(buf);
        skinType = ByteBufUtils.readUTF8String(buf);

        dyes.clear();
        for (int i = buf.readInt(); i > 0; i--) dyes.add(new Color(buf.readInt()));

        return this;
    }

    @Override
    public CTraitElement_AWSkin save(OutputStream stream)
    {
        new CStringUTF8().set(libraryFile).save(stream).set(skinType).save(stream);

        CInt ci = new CInt().set(dyes.size()).save(stream);
        for (Color color : dyes) ci.set(color.color()).save(stream);

        return this;
    }

    @Override
    public CTraitElement_AWSkin load(InputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8();
        libraryFile = cs.load(stream).value;
        skinType = cs.load(stream).value;

        CInt ci = new CInt();
        dyes.clear();
        for (int i = ci.load(stream).value; i > 0; i--) dyes.add(new Color(ci.load(stream).value));

        return this;
    }
}
