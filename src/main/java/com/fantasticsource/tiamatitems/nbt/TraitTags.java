package com.fantasticsource.tiamatitems.nbt;

import com.fantasticsource.tiamatitems.trait.CTrait;
import com.fantasticsource.tiamatitems.trait.CTraitGenPool;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;

import static com.fantasticsource.tiamatitems.TiamatItems.DOMAIN;

public class TraitTags
{
    public static void addTraitTag(ItemStack stack, String poolSetName, CTraitGenPool pool, CTrait traitGen, int wholeNumberPercentage)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("traits")) compound.setTag("traits", new NBTTagList());
        NBTTagList traitList = compound.getTagList("traits", Constants.NBT.TAG_STRING);

        traitList.appendTag(new NBTTagString(poolSetName + ":" + (pool == null ? "" : pool.name + ":") + traitGen.name + ":" + wholeNumberPercentage));
    }

    public static void clearTraitTags(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("traits")) return;

        compound.removeTag("traits");
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
    }

    public static ArrayList<String> getTraitStrings(ItemStack stack)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!stack.hasTagCompound()) return result;
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) return result;
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("traits")) return result;
        NBTTagList traitList = compound.getTagList("traits", Constants.NBT.TAG_STRING);

        for (int i = 0; i < traitList.tagCount(); i++)
        {
            result.add(traitList.getStringTagAt(i));
        }

        return result;
    }

    public static void setTraitTags(ItemStack stack, ArrayList<String> traitStrings)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        NBTTagList traitList = new NBTTagList();
        compound.setTag("traits", traitList);

        for (String traitString : traitStrings) traitList.appendTag(new NBTTagString(traitString));
    }
}
