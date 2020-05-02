package com.fantasticsource.tiamatitems.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;

import static com.fantasticsource.tiamatitems.api.TiamatItemsAPI.DOMAIN;

public class PassiveAttributeModTags
{
    //<attributeName>;<amount>;<operation>

    public static ArrayList<String> getPassiveMods(ItemStack stack)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!stack.hasTagCompound()) return result;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return result;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("passiveMods")) return result;

        NBTTagList list = compound.getTagList("passiveMods", Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++) result.add(list.getStringTagAt(i));

        return result;
    }

    public static void clearPassiveMods(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("passiveMods")) return;

        compound.removeTag("passiveMods");
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
    }

    public static void removePassiveMod(ItemStack stack, String attributeMod)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("passiveMods")) return;

        NBTTagList list = compound.getTagList("passiveMods", Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++)
        {
            if (list.getStringTagAt(i).equals(attributeMod))
            {
                list.removeTag(i);
                if (list.tagCount() == 0)
                {
                    compound.removeTag("passiveMods");
                    if (compound.hasNoTags())
                    {
                        mainTag.removeTag(DOMAIN);
                        if (mainTag.hasNoTags()) stack.setTagCompound(null);
                    }
                }
                break;
            }
        }
    }

    public static void addPassiveMod(ItemStack stack, String attributeMod)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("passiveMods")) compound.setTag("passiveMods", new NBTTagList());
        NBTTagList list = compound.getTagList("passiveMods", Constants.NBT.TAG_STRING);

        list.appendTag(new NBTTagString(attributeMod));
    }

    public static boolean itemHasPassiveMod(ItemStack stack, String attributeMod)
    {
        if (!stack.hasTagCompound()) return false;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return false;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("passiveMods")) return false;

        NBTTagList list = compound.getTagList("passiveMods", Constants.NBT.TAG_STRING);
        for (int i = list.tagCount() - 1; i >= 0; i--) if (list.getStringTagAt(i).equals(attributeMod)) return true;
        return false;
    }
}
