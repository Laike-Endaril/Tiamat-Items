package com.fantasticsource.tiamatitems.trait.unrecalculable.element;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.aw.AWSkinGenerator;
import com.fantasticsource.mctools.aw.TransientAWSkinHandler;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTraitElement;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes.CRandomRGB;
import com.fantasticsource.tools.Tools;
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
import java.util.LinkedHashMap;
import java.util.Map;

public class CUTraitElement_AWSkin extends CUnrecalculableTraitElement
{
    public static final String AW_SKIN_LIBRARY_DIR = MCTools.getConfigDir() + ".." + File.separator + "armourers_workshop" + File.separator + "skin-library" + File.separator;

    //This trait may not work correctly if your skin folders have non-skin files in them (anything besides folders and skins)
    public String libraryFileOrFolder = "", skinType = "";
    public boolean isTransient = false, isRandomFromFolder = false;
    public int indexWithinSkinTypeIfTransient = 0;
    public LinkedHashMap<Integer, CRandomRGB> dyeChannels = new LinkedHashMap<>();


    @Override
    public String getDescription()
    {
        String folderString = AW_SKIN_LIBRARY_DIR + Tools.fixFileSeparators(libraryFileOrFolder);
        if (isRandomFromFolder) return isTransient ? "Random transient AW skin(s) from folder: " + folderString : "Random AW skin(s) from folder: " + folderString;


        File file = new File(folderString);
        boolean isfolder = file.exists() && file.isDirectory();

        if (isTransient)
        {
            if (isfolder) return "Transient AW Skins from folder: " + folderString;
            return "Transient AW Skin: " + folderString;
        }

        if (isfolder) return "AW Skins from folder: " + folderString;
        return "AW Skin: " + folderString;
    }

    @Override
    public double applyToItem(ItemStack stack, double itemTypeAndLevelMultiplier)
    {
        //Dyes
        LinkedHashMap<Integer, Color> dyes = new LinkedHashMap<>();
        for (Map.Entry<Integer, CRandomRGB> entry : dyeChannels.entrySet()) dyes.put(entry.getKey(), entry.getValue().generate());


        //Skin file(s)
        File file = getSkinOrFolder(AW_SKIN_LIBRARY_DIR + Tools.fixFileSeparators(libraryFileOrFolder));
        if (file == null) return -1;


        if (isRandomFromFolder)
        {
            if (!file.isDirectory()) return -1;

            File[] files = file.listFiles();
            if (files == null || files.length == 0) return -1;
        }


        //At this point, "file" is either a skin file, or a folder (either way, it exists)
        ItemStack skinStack = AWSkinGenerator.generate(libraryFileOrFolder, skinType, dyes);
        if (isTransient) TransientAWSkinHandler.addTransientAWSkin(stack, skinType, indexWithinSkinTypeIfTransient, skinStack);
        else setAWSkin(stack, skinStack);
        return 1;
    }

    protected static File getSkinOrFolder(String filename)
    {
        File file = new File(filename);
        if (file.isDirectory()) return file;

        file = new File(filename + ".armour");
        if (!file.exists() || file.isDirectory()) return null;
        return file;
    }

    protected static void setAWSkin(ItemStack stack, ItemStack skinStack)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setTag("armourersWorkshop", MCTools.cloneItemStack(skinStack).getTagCompound().getCompoundTag("armourersWorkshop"));
    }


    @Override
    public CUTraitElement_AWSkin write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, libraryFileOrFolder);
        ByteBufUtils.writeUTF8String(buf, skinType);
        buf.writeBoolean(isRandomFromFolder);
        buf.writeBoolean(isTransient);
        buf.writeInt(indexWithinSkinTypeIfTransient);

        buf.writeInt(dyeChannels.size());
        for (Map.Entry<Integer, CRandomRGB> entry : dyeChannels.entrySet())
        {
            buf.writeInt(entry.getKey());
            entry.getValue().write(buf);
        }

        return this;
    }

    @Override
    public CUTraitElement_AWSkin read(ByteBuf buf)
    {
        libraryFileOrFolder = ByteBufUtils.readUTF8String(buf);
        skinType = ByteBufUtils.readUTF8String(buf);
        isRandomFromFolder = buf.readBoolean();
        isTransient = buf.readBoolean();
        indexWithinSkinTypeIfTransient = buf.readInt();

        dyeChannels.clear();
        for (int i = buf.readInt(); i > 0; i--) dyeChannels.put(buf.readInt(), new CRandomRGB().read(buf));

        return this;
    }

    @Override
    public CUTraitElement_AWSkin save(OutputStream stream)
    {
        new CStringUTF8().set(libraryFileOrFolder).save(stream).set(skinType).save(stream);
        new CBoolean().set(isRandomFromFolder).save(stream).set(isTransient).save(stream);
        new CBoolean().set(isTransient).save(stream);
        CInt ci = new CInt().set(indexWithinSkinTypeIfTransient).save(stream);

        ci.set(dyeChannels.size()).save(stream);
        for (Map.Entry<Integer, CRandomRGB> entry : dyeChannels.entrySet())
        {
            ci.set(entry.getKey()).save(stream);
            entry.getValue().save(stream);
        }

        return this;
    }

    @Override
    public CUTraitElement_AWSkin load(InputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8();
        CInt ci = new CInt();
        CBoolean cb = new CBoolean();
        libraryFileOrFolder = cs.load(stream).value;
        skinType = cs.load(stream).value;
        isRandomFromFolder = cb.load(stream).value;
        isTransient = cb.load(stream).value;
        indexWithinSkinTypeIfTransient = ci.load(stream).value;

        dyeChannels.clear();
        for (int i = ci.load(stream).value; i > 0; i--) dyeChannels.put(ci.load(stream).value, new CRandomRGB().load(stream));

        return this;
    }
}
