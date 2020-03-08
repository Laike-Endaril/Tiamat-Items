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
    //TODO this trait may not work correctly if your skin folders have non-skin files in them (anything besides folders and skins)
    public String libraryFileOrFolder = "", skinType = "";
    public boolean isTransient = false, isRandomFromFolder = false;
    public ArrayList<CRandomRGB> dyeChannels = new ArrayList<>();
    //TODO limit dye channel count to 8 max
    //TODO when editing, get paint types from PaintRegistry.REGISTERED_TYPES (use the alpha of the Color for the paint type)


    @Override
    public String getDescription()
    {
        String folderString = (MCTools.getConfigDir() + ".." + File.separator + "armourers_workshop" + File.separator + "skin-library" + File.separator + libraryFileOrFolder.replaceAll("[/\\\\]", Matcher.quoteReplacement(File.separator)));
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
    public double applyToItem(ItemStack stack, double itemTypeAndLevelMultiplier)
    {
        //Dyes
        Color[] dyes = new Color[dyeChannels.size()];
        int i = 0;
        for (CRandomRGB randomRGB : dyeChannels) dyes[i++] = randomRGB.generate();


        //Skin file(s)
        File file = getSkinOrFolder(MCTools.getConfigDir() + ".." + File.separator + "armourers_workshop" + File.separator + "skin-library" + File.separator + libraryFileOrFolder.replaceAll("[/\\\\]", Matcher.quoteReplacement(File.separator)));
        if (file == null) return -1;


        if (isRandomFromFolder)
        {
            if (!file.isDirectory()) return -1;

            File[] files = file.listFiles();
            if (files == null || files.length == 0) return -1;


            double percentage = itemTypeAndLevelMultiplier / CItemType.maxItemLevel;

            file = files[(int) (Math.random() * files.length * percentage)];
        }


        //At this point, "file" is either a skin file, or a folder (either way, it exists)


        //If it's a skin file, just add it and be done
        if (!file.isDirectory())
        {
            if (isTransient) TransientAWSkinHandler.addTransientAWSkin(stack, getSkinOrSkinFolderDir(file.getAbsolutePath()), skinType, dyes);
            else addAWSkin(stack, getSkinOrSkinFolderDir(file.getAbsolutePath()), skinType, dyes);
            return 1;
        }


        //It's a folder; this skin may be using multiple parts, possibly with render channels
        File[] files = file.listFiles();
        if (files == null || files.length == 0) return -1;

        boolean skinAdded = false;
        for (File skinOrRenderModeChannel : files)
        {
            if (!skinOrRenderModeChannel.isDirectory())
            {
                //Skin not in a render channel folder; always add as transient to prevent the wardrobe from conflicting with the native item skin
                skinAdded = true;
                TransientAWSkinHandler.addTransientAWSkin(stack, getSkinOrSkinFolderDir(skinOrRenderModeChannel.getAbsolutePath()), skinType, dyes);
            }
            else
            {
                //Folder; possibly a render channel folder
                File[] renderModeFiles = skinOrRenderModeChannel.listFiles();
                if (renderModeFiles == null || renderModeFiles.length == 0) continue;

                for (File renderModeFile : renderModeFiles)
                {
                    if (renderModeFile.isDirectory()) continue;

                    skinAdded = true;
                    TransientAWSkinHandler.addTransientAWSkin(stack, getSkinOrSkinFolderDir(renderModeFile.getAbsolutePath()), skinType, skinOrRenderModeChannel.getName(), renderModeFile.getName().replace(".armour", ""), dyes);
                }
            }
        }

        return skinAdded ? 1 : -1;
    }


    protected String getSkinOrSkinFolderDir(String fullDir)
    {
        return fullDir.replace(MCTools.getConfigDir() + ".." + File.separator + "armourers_workshop" + File.separator + "skin-library" + File.separator, "");
    }

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
    public CUTraitElement_AWSkin write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, libraryFileOrFolder);
        ByteBufUtils.writeUTF8String(buf, skinType);
        buf.writeBoolean(isTransient);
        buf.writeBoolean(isRandomFromFolder);

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
        isRandomFromFolder = buf.readBoolean();

        dyeChannels.clear();
        for (int i = buf.readInt(); i > 0; i--) dyeChannels.add(new CRandomRGB().read(buf));

        return this;
    }

    @Override
    public CUTraitElement_AWSkin save(OutputStream stream)
    {
        new CStringUTF8().set(libraryFileOrFolder).save(stream).set(skinType).save(stream);
        new CBoolean().set(isTransient).save(stream).set(isRandomFromFolder).save(stream);

        new CInt().set(dyeChannels.size()).save(stream);
        for (CRandomRGB dyeChannel : dyeChannels) dyeChannel.save(stream);

        return this;
    }

    @Override
    public CUTraitElement_AWSkin load(InputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8();
        CBoolean cb = new CBoolean();

        libraryFileOrFolder = cs.load(stream).value;
        skinType = cs.load(stream).value;
        isTransient = cb.load(stream).value;
        isRandomFromFolder = cb.load(stream).value;

        dyeChannels.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) dyeChannels.add(new CRandomRGB().load(stream));

        return this;
    }
}
