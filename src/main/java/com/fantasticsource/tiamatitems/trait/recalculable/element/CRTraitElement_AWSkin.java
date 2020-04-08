package com.fantasticsource.tiamatitems.trait.recalculable.element;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.aw.AWSkinGenerator;
import com.fantasticsource.mctools.aw.TransientAWSkinHandler;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
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
import java.util.ArrayList;

public class CRTraitElement_AWSkin extends CRecalculableTraitElement
{
    public static final String AW_SKIN_LIBRARY_DIR = MCTools.getConfigDir() + ".." + File.separator + "armourers_workshop" + File.separator + "skin-library" + File.separator;

    //This trait may not work correctly if your skin folders have non-skin files in them (anything besides folders and skins)
    public String libraryFileOrFolder = "", skinType = "";
    public boolean isTransient = false, isRandomFromFolder = false;
    public int indexWithinSkinTypeIfTransient = 0;
    public ArrayList<CRandomRGB> dyeChannels = new ArrayList<>();


    protected static File getSkinOrFolder(String filename)
    {
        File file = new File(filename);
        if (file.isDirectory()) return file;

        file = new File(filename + ".armour");
        if (!file.exists() || file.isDirectory()) return null;
        return file;
    }

    protected static void addAWSkin(ItemStack stack, String filename, String skinType, Color[] dyes)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());

        NBTTagCompound compound = new NBTTagCompound();
        stack.getTagCompound().setTag("armourersWorkshop", compound);

        NBTTagCompound compound2 = new NBTTagCompound();
        compound.setTag("identifier", compound2);

        compound2.setString("libraryFile", filename.replace(".armour", ""));
        compound2.setString("skinType", skinType);

        compound2 = new NBTTagCompound();
        compound.setTag("dyeData", compound2);

        int i = 0;
        for (Color dye : dyes)
        {
            compound2.setByte("dye" + i + "r", (byte) dye.r());
            compound2.setByte("dye" + i + "g", (byte) dye.g());
            compound2.setByte("dye" + i + "b", (byte) dye.b());
            compound2.setByte("dye" + i + "t", (byte) dye.a());
            i++;
        }
    }


    @Override
    public int requiredArgumentCount()
    {
        return 0;
    }


    @Override
    public String getDescription(ArrayList<Integer> baseArgs, double[] multipliedArgs)
    {
        String folderString = AW_SKIN_LIBRARY_DIR + Tools.fixFileSeparators(libraryFileOrFolder);
        if (isRandomFromFolder) return isTransient ? "Random transient AW skin(s) from folder: " + folderString : "Random AW skin(s) from folder: " + folderString;


        File file = new File(folderString);
        boolean isfolder = file.exists() && file.isDirectory();

        if (isTransient)
        {
            if (isfolder) return "Transient AW Skins from folder: " + libraryFileOrFolder;
            return "Transient AW Skin: " + libraryFileOrFolder;
        }

        if (isfolder) return "AW Skins from folder: " + libraryFileOrFolder;
        return "AW Skin: " + libraryFileOrFolder;
    }


    @Override
    public void applyToItem(ItemStack stack, int[] baseArgs, double[] multipliedArgs)
    {
        //Dyes
        Color[] dyes = new Color[dyeChannels.size()];
        int i = 0;
        for (CRandomRGB randomRGB : dyeChannels) dyes[i++] = randomRGB.generate();


        //Skin file(s)
        File file = getSkinOrFolder(AW_SKIN_LIBRARY_DIR + Tools.fixFileSeparators(libraryFileOrFolder));
        if (file == null) return;


        if (isRandomFromFolder)
        {
            if (!file.isDirectory()) return;

            File[] files = file.listFiles();
            if (files == null || files.length == 0) return;


            file = files[(int) (Math.random() * files.length)];
        }


        //At this point, "file" is either a skin file, or a folder (either way, it exists)
        if (isTransient)
        {
            ItemStack skinStack = AWSkinGenerator.generate(libraryFileOrFolder, skinType, dyes);
            TransientAWSkinHandler.addTransientAWSkin(stack, skinType, indexWithinSkinTypeIfTransient, skinStack);
        }
        else addAWSkin(stack, getSkinOrSkinFolderDir(file.getAbsolutePath()), skinType, dyes);
    }

    protected String getSkinOrSkinFolderDir(String fullDir)
    {
        return fullDir.replace(AW_SKIN_LIBRARY_DIR, "");
    }


    @Override
    public CRTraitElement_AWSkin write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, libraryFileOrFolder);
        ByteBufUtils.writeUTF8String(buf, skinType);
        buf.writeBoolean(isTransient);
        buf.writeInt(indexWithinSkinTypeIfTransient);

        buf.writeInt(dyeChannels.size());
        for (CRandomRGB dyeChannel : dyeChannels) dyeChannel.write(buf);

        return this;
    }

    @Override
    public CRTraitElement_AWSkin read(ByteBuf buf)
    {
        libraryFileOrFolder = ByteBufUtils.readUTF8String(buf);
        skinType = ByteBufUtils.readUTF8String(buf);
        isTransient = buf.readBoolean();
        indexWithinSkinTypeIfTransient = buf.readInt();

        dyeChannels.clear();
        for (int i = buf.readInt(); i > 0; i--) dyeChannels.add(new CRandomRGB().read(buf));

        return this;
    }

    @Override
    public CRTraitElement_AWSkin save(OutputStream stream)
    {
        new CStringUTF8().set(libraryFileOrFolder).save(stream).set(skinType).save(stream);
        new CBoolean().set(isTransient).save(stream);
        CInt ci = new CInt().set(indexWithinSkinTypeIfTransient).save(stream);

        ci.set(dyeChannels.size()).save(stream);
        for (CRandomRGB dyeChannel : dyeChannels) dyeChannel.save(stream);

        return this;
    }

    @Override
    public CRTraitElement_AWSkin load(InputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8();
        CInt ci = new CInt();
        libraryFileOrFolder = cs.load(stream).value;
        skinType = cs.load(stream).value;
        isTransient = new CBoolean().load(stream).value;
        indexWithinSkinTypeIfTransient = ci.load(stream).value;

        dyeChannels.clear();
        for (int i = ci.load(stream).value; i > 0; i--) dyeChannels.add(new CRandomRGB().load(stream));

        return this;
    }
}
