package com.fantasticsource.tiamatitems.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static com.fantasticsource.tiamatitems.TiamatItems.DOMAIN;

public class MiscTags
{
    public static void setItemLevel(ItemStack stack, int level)
    {
        if (level == 0)
        {
            clearItemLevel(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setInteger("level", level);
    }

    public static int getItemLevel(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return 0;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return 0;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("level")) return 0;

        return compound.getInteger("level");
    }

    public static void clearItemLevel(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("level")) return;

        compound.removeTag("level");
        if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
    }


    public static void setItemLevelReq(ItemStack stack, int levelReq)
    {
        if (levelReq == 0)
        {
            clearItemLevelReq(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setInteger("levelReq", levelReq);
    }

    public static int getItemLevelReq(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return 0;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return 0;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("levelReq")) return 0;

        return compound.getInteger("levelReq");
    }

    public static void clearItemLevelReq(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("levelReq")) return;

        compound.removeTag("levelReq");
        if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
    }


    public static void setItemValue(ItemStack stack, int value)
    {
        if (value == 0)
        {
            clearItemValue(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setInteger("value", value);
    }

    public static int getItemValue(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return 0;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return 0;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("value")) return 0;

        return compound.getInteger("value");
    }

    public static void clearItemValue(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("value")) return;

        compound.removeTag("value");
        if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
    }
}