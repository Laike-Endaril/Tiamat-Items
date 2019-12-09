package com.fantasticsource.tiamatitems.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class LayerTags
{
    public static ArrayList<String> getItemLayers(ItemStack stack)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!stack.hasTagCompound()) return result;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(MODID)) return result;

        compound = compound.getCompoundTag(MODID);
        if (!compound.hasKey("layers")) return result;

        NBTTagList list = compound.getTagList("layers", Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++) result.add(list.getStringTagAt(i));

        return result;
    }

    public static void clearItemLayers(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(MODID)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(MODID);
        if (!compound.hasKey("layers")) return;

        compound.removeTag("layers");
        if (compound.hasNoTags()) mainTag.removeTag(MODID);
    }

    public static void removeItemLayer(ItemStack stack, String layer)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(MODID)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(MODID);
        if (!compound.hasKey("layers")) return;

        NBTTagList list = compound.getTagList("layers", Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++)
        {
            if (list.getStringTagAt(i).equals(layer))
            {
                list.removeTag(i);
                if (list.tagCount() == 0)
                {
                    compound.removeTag("layers");
                    if (compound.hasNoTags()) mainTag.removeTag(MODID);
                }
                break;
            }
        }
    }

    public static void addItemLayer(ItemStack stack, String layer)
    {
        if (itemHasLayer(stack, layer)) return;

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(MODID)) compound.setTag(MODID, new NBTTagCompound());
        compound = compound.getCompoundTag(MODID);

        if (!compound.hasKey("layers")) compound.setTag("layers", new NBTTagList());
        NBTTagList list = compound.getTagList("layers", Constants.NBT.TAG_STRING);

        list.appendTag(new NBTTagString(layer));
    }

    public static boolean itemHasLayer(ItemStack stack, String layer)
    {
        if (!stack.hasTagCompound()) return false;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(MODID)) return false;

        compound = compound.getCompoundTag(MODID);
        if (!compound.hasKey("layers")) return false;

        NBTTagList list = compound.getTagList("layers", Constants.NBT.TAG_STRING);
        for (int i = list.tagCount() - 1; i >= 0; i--) if (list.getStringTagAt(i).equals(layer)) return true;
        return false;
    }
}
