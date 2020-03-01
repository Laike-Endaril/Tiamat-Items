package com.fantasticsource.tiamatitems.trait.unrecalculable.element;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.aw.TransientAWSkinHandler;
import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTraitElement;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes.CRandomRGB;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.datastructures.Color;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;

public class CUTraitElement_AWSkin extends CUnrecalculableTraitElement
{
    public String libraryFileOrFolder = "", skinType = "";
    public boolean isTransient = false;
    public ArrayList<CRandomRGB> dyeChannels = new ArrayList<>();
    //TODO limit dye channel count to 8 max
    //TODO when editing, get paint types from PaintRegistry.REGISTERED_TYPES (use the alpha of the Color for the paint type)


    @Override
    public String getDescription()
    {
        String folderString = (MCTools.getConfigDir() + ".." + File.separator + "armourers_workshop" + File.separator + "skin-library" + File.separator + libraryFileOrFolder.replaceAll("[/\\\\]", Matcher.quoteReplacement(File.separator)));
        File file = new File(folderString);
        boolean isfolder = file.exists() && file.isDirectory();

        if (isTransient)
        {
            if (isfolder) return "Transient AW Skin from folder: " + libraryFileOrFolder;
            return "Transient AW Skin: " + libraryFileOrFolder;
        }

        if (isfolder) return "AW Skin from folder: " + libraryFileOrFolder;
        return "AW Skin: " + libraryFileOrFolder;
    }


    @Override
    public double applyToItem(ItemStack stack, double itemTypeAndLevelMultiplier)
    {
        String folderString = (MCTools.getConfigDir() + ".." + File.separator + "armourers_workshop" + File.separator + "skin-library" + File.separator + libraryFileOrFolder.replaceAll("[/\\\\]", Matcher.quoteReplacement(File.separator)));
        File file = new File(folderString);
        boolean isfolder = file.isDirectory();

        if (isfolder)
        {
            double percentage = itemTypeAndLevelMultiplier / CItemType.maxItemLevel;
            File[] files = file.listFiles();
            if (files == null) return -1;

            file = files[(int) (Math.random() * files.length * percentage)];
        }
        else file = new File(folderString + ".armour");


        if (!file.exists()) return -1;


        String filename = isfolder ? libraryFileOrFolder + "/" + file.getName().replace(".armour", "") : libraryFileOrFolder;


        Color[] dyes = new Color[dyeChannels.size()];
        int i = 0;
        for (CRandomRGB randomRGB : dyeChannels) dyes[i++] = randomRGB.generate();


        if (isTransient)
        {
            TransientAWSkinHandler.addTransientAWSkin(stack, filename, skinType, dyes);
            return 1;
        }


        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());

        NBTTagCompound compound = new NBTTagCompound();
        stack.getTagCompound().setTag("armourersWorkshop", compound);

        NBTTagCompound compound2 = new NBTTagCompound();
        compound.setTag("identifier", compound2);

        compound2.setString("libraryFile", filename);
        compound2.setString("skinType", skinType);

        compound2 = new NBTTagCompound();
        compound.setTag("dyeData", compound2);

        i = 0;
        for (Color dye : dyes)
        {
            compound2.setByte("dye" + i + "r", (byte) dye.r());
            compound2.setByte("dye" + i + "g", (byte) dye.g());
            compound2.setByte("dye" + i + "b", (byte) dye.b());
            compound2.setByte("dye" + i + "t", (byte) dye.a());
            i++;
        }

        return 1;
    }


    @Override
    public CUTraitElement_AWSkin write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, libraryFileOrFolder);
        ByteBufUtils.writeUTF8String(buf, skinType);
        buf.writeBoolean(isTransient);

        buf.writeInt(dyeChannels.size());
        for (CRandomRGB dyeChannel : dyeChannels) dyeChannel.write(buf);

        return this;
    }

    @Override
    public CUTraitElement_AWSkin read(ByteBuf buf)
    {
        libraryFileOrFolder = ByteBufUtils.readUTF8String(buf);
        skinType = ByteBufUtils.readUTF8String(buf);
        isTransient = buf.readBoolean();

        dyeChannels.clear();
        for (int i = buf.readInt(); i > 0; i--) dyeChannels.add(new CRandomRGB().read(buf));

        return this;
    }

    @Override
    public CUTraitElement_AWSkin save(OutputStream stream)
    {
        new CStringUTF8().set(libraryFileOrFolder).save(stream).set(skinType).save(stream);
        new CBoolean().set(isTransient).save(stream);

        new CInt().set(dyeChannels.size()).save(stream);
        for (CRandomRGB dyeChannel : dyeChannels) dyeChannel.save(stream);

        return this;
    }

    @Override
    public CUTraitElement_AWSkin load(InputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8();
        libraryFileOrFolder = cs.load(stream).value;
        skinType = cs.load(stream).value;
        isTransient = new CBoolean().load(stream).value;

        dyeChannels.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) dyeChannels.add(new CRandomRGB().load(stream));

        return this;
    }
}