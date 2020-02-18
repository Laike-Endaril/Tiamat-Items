package com.fantasticsource.tiamatitems.nbt;

import com.fantasticsource.tiamatitems.trait.CTrait;
import com.fantasticsource.tiamatitems.trait.CTraitGenPool;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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

        if (!compound.hasKey(poolSetName)) compound.setTag(poolSetName, new NBTTagCompound());
        compound = compound.getCompoundTag(poolSetName);

        String poolKey = pool == null ? "null" : pool.name;
        if (!compound.hasKey(poolKey)) compound.setTag(poolKey, new NBTTagList());
        NBTTagList traitList = compound.getTagList(poolKey, Constants.NBT.TAG_COMPOUND);

        compound = new NBTTagCompound();
        compound.setString("name", traitGen.name);
        compound.setInteger("percent", wholeNumberPercentage);

        traitList.appendTag(compound);
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
