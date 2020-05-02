package com.fantasticsource.tiamatitems.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static com.fantasticsource.tiamatitems.api.TiamatItemsAPI.DOMAIN;

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

        compound.setString("actionLC", actionName);
    }

    public static String getLeftClickAction(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return "None";

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return "None";

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("actionLC")) return "None";

        return compound.getString("actionLC");
    }

    public static void clearLeftClickAction(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("actionLC")) return;

        compound.removeTag("actionLC");
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
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

        compound.setString("actionRC", actionName);
    }

    public static String getRightClickAction(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return "None";

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return "None";

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("actionRC")) return "None";

        return compound.getString("actionRC");
    }

    public static void clearRightClickAction(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("actionRC")) return;

        compound.removeTag("actionRC");
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
    }
}
