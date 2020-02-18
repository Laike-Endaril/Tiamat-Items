package com.fantasticsource.tiamatitems.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static com.fantasticsource.tiamatitems.TiamatItems.DOMAIN;

public class ActionTags
{
    public static void setLeftClickAction(ItemStack stack, String actionName)
    {
        if (actionName.equals("") || actionName.equals("None"))
        {
            clearLeftClickAction(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setString("leftClick", actionName);
    }

    public static String getLeftClickAction(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return "None";

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return "None";

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("leftClick")) return "None";

        return compound.getString("leftClick");
    }

    public static void clearLeftClickAction(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("leftClick")) return;

        compound.removeTag("leftClick");
        if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
    }


    public static void setRightClickAction(ItemStack stack, String actionName)
    {
        if (actionName.equals("") || actionName.equals("None"))
        {
            clearLeftClickAction(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setString("rightClick", actionName);
    }

    public static String getRightClickAction(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return "None";

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return "None";

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("rightClick")) return "None";

        return compound.getString("rightClick");
    }

    public static void clearRightClickAction(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("rightClick")) return;

        compound.removeTag("rightClick");
        if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
    }
}
