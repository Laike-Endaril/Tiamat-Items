package com.fantasticsource.tiamatitems.trait.recalculable.element;

import com.fantasticsource.mctools.aw.AWSkinGenerator;
import com.fantasticsource.mctools.aw.TransientAWSkinHandler;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tools.component.CBoolean;
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

public class CRTraitElement_AWSkin extends CRecalculableTraitElement
{
    public String libraryFile = "", skinType = "";
    public boolean isTransient = false;
    public int indexWithinSkinTypeIfTransient = 0;
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
        if (isTransient) return "Transient AW Skin: " + (libraryFile.equals("") ? "No Skin" : libraryFile);
        return "AW Skin: " + (libraryFile.equals("") ? "No Skin" : libraryFile);
    }


    @Override
    public void applyToItem(ItemStack stack, int[] baseArgs, double[] multipliedArgs)
    {
        if (isTransient)
        {
            ItemStack skinStack = AWSkinGenerator.generate(libraryFile, skinType, dyes.toArray(new Color[0]));
            TransientAWSkinHandler.addTransientAWSkin(stack, skinType, indexWithinSkinTypeIfTransient, skinStack);
            return;
        }


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
    public CRTraitElement_AWSkin write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, libraryFile);
        ByteBufUtils.writeUTF8String(buf, skinType);
        buf.writeBoolean(isTransient);
        buf.writeInt(indexWithinSkinTypeIfTransient);

        buf.writeInt(dyes.size());
        for (Color color : dyes) buf.writeInt(color.color());

        return this;
    }

    @Override
    public CRTraitElement_AWSkin read(ByteBuf buf)
    {
        libraryFile = ByteBufUtils.readUTF8String(buf);
        skinType = ByteBufUtils.readUTF8String(buf);
        isTransient = buf.readBoolean();
        indexWithinSkinTypeIfTransient = buf.readInt();

        dyes.clear();
        for (int i = buf.readInt(); i > 0; i--) dyes.add(new Color(buf.readInt()));

        return this;
    }

    @Override
    public CRTraitElement_AWSkin save(OutputStream stream)
    {
        new CStringUTF8().set(libraryFile).save(stream).set(skinType).save(stream);
        new CBoolean().set(isTransient).save(stream);
        CInt ci = new CInt().set(indexWithinSkinTypeIfTransient).save(stream);

        ci.set(dyes.size()).save(stream);
        for (Color color : dyes) ci.set(color.color()).save(stream);

        return this;
    }

    @Override
    public CRTraitElement_AWSkin load(InputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8();
        CInt ci = new CInt();
        libraryFile = cs.load(stream).value;
        skinType = cs.load(stream).value;
        isTransient = new CBoolean().load(stream).value;
        indexWithinSkinTypeIfTransient = ci.load(stream).value;

        dyes.clear();
        for (int i = ci.load(stream).value; i > 0; i--) dyes.add(new Color(ci.load(stream).value));

        return this;
    }
}
