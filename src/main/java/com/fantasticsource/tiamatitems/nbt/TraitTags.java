package com.fantasticsource.tiamatitems.nbt;

import com.fantasticsource.tiamatitems.trait.CTrait;
import com.fantasticsource.tiamatitems.trait.CTraitGenPool;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;

import static com.fantasticsource.tiamatitems.TiamatItems.DOMAIN;

public class TraitTags
{
    public static void setItemTraitData(ItemStack stack, CTraitGenPool pool, CTrait traitGen, int wholeNumberPercentage)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("trait")) compound.setTag("trait", new NBTTagCompound());
        compound = compound.getCompoundTag("trait");

        String key = pool == null ? "null" : pool.name;
        if (!compound.hasKey(key)) compound.setTag(key, new NBTTagCompound());
        compound = compound.getCompoundTag(key);

        compound.setTag(traitGen.name, new NBTTagInt(wholeNumberPercentage));
    }

    //TODO getItemTraitData

    public static void clearItemTraits(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("trait")) return;

        compound.removeTag("trait");
        if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
    }
}
