package com.fantasticsource.tiamatitems.nbt;

import com.fantasticsource.tiamatitems.trait.CTrait;
import com.fantasticsource.tiamatitems.trait.CTraitGenPool;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import static com.fantasticsource.tiamatitems.TiamatItems.DOMAIN;

public class TraitTags
{
    public static void addItemTrait(ItemStack stack, String poolSetName, CTraitGenPool pool, CTrait traitGen, int wholeNumberPercentage)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("traits")) compound.setTag("traits", new NBTTagCompound());
        compound = compound.getCompoundTag("traits");

        NBTTagList traitList;
        if (poolSetName.equals("Static"))
        {
            if (!compound.hasKey(poolSetName)) compound.setTag(poolSetName, new NBTTagList());
            traitList = compound.getTagList(poolSetName, Constants.NBT.TAG_STRING);
        }
        else
        {
            if (!compound.hasKey(poolSetName)) compound.setTag(poolSetName, new NBTTagCompound());
            compound = compound.getCompoundTag(poolSetName);

            if (!compound.hasKey(pool.name)) compound.setTag(pool.name, new NBTTagList());
            traitList = compound.getTagList(pool.name, Constants.NBT.TAG_STRING);
        }
        traitList.appendTag(new NBTTagString(traitGen.name + ":" + wholeNumberPercentage));
    }

    public static void clearItemTraits(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("traits")) return;

        compound.removeTag("traits");
        if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
    }
}
