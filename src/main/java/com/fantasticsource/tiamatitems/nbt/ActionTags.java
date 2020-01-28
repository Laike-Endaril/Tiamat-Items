package com.fantasticsource.tiamatitems.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static com.fantasticsource.tiamatitems.TiamatItems.DOMAIN;

public class ActionTags
{
    public static void setItemAction1(ItemStack stack, String actionName)
    {
        if (actionName.equals("") || actionName.equals("New Action"))
        {
            clearItemAction1(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setString("action1", actionName);
    }

    public static String getItemAction1(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return "None";

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return "None";

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("action1")) return "None";

        return compound.getString("action1");
    }

    public static void clearItemAction1(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("action1")) return;

        compound.removeTag("action1");
        if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
    }


    public static void setItemAction2(ItemStack stack, String actionName)
    {
        if (actionName.equals("") || actionName.equals("New Action"))
        {
            clearItemAction1(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setString("action2", actionName);
    }

    public static String getItemAction2(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return "None";

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return "None";

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("action2")) return "None";

        return compound.getString("action2");
    }

    public static void clearItemAction2(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("action2")) return;

        compound.removeTag("action2");
        if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
    }
}
