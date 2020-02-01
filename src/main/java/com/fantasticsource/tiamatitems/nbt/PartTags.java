package com.fantasticsource.tiamatitems.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;

import static com.fantasticsource.tiamatitems.TiamatItems.DOMAIN;

public class PartTags
{
    public static ArrayList<String> getPartSlots(ItemStack stack)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!stack.hasTagCompound()) return result;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return result;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("parts")) return result;

        compound = compound.getCompoundTag("parts");
        result.addAll(compound.getKeySet());

        return result;
    }

    public static void clearPartSlots(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("parts")) return;

        compound.removeTag("parts");
        if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
    }

    public static void removePartSlot(ItemStack stack, String partSlot)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("parts")) return;

        compound = compound.getCompoundTag("parts");
        compound.removeTag(partSlot);
    }

    public static void addPartSlot(ItemStack stack, String partSlot)
    {
        if (itemHasPartSlot(stack, partSlot)) return;

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("parts")) compound.setTag("parts", new NBTTagCompound());
        compound = compound.getCompoundTag("parts");

        compound.setTag(partSlot, new NBTTagCompound());
    }

    public static boolean itemHasPartSlot(ItemStack stack, String partSlot)
    {
        if (!stack.hasTagCompound()) return false;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return false;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("parts")) return false;

        compound = compound.getCompoundTag("parts");
        return compound.hasKey(partSlot);
    }


    public static NBTTagCompound getPartTag(ItemStack stack, String partSlot)
    {
        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return null;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("parts")) return null;

        compound = compound.getCompoundTag("parts");
        if (!compound.hasKey(partSlot)) return null;

        return compound.getCompoundTag(partSlot);
    }

    public static void setPartTag(ItemStack stack, String partSlot, NBTTagCompound partTag)
    {
        if (!itemHasPartSlot(stack, partSlot)) return;

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("parts")) compound.setTag("parts", new NBTTagCompound());
        compound = compound.getCompoundTag("parts");

        compound.setTag(partSlot, partTag);
    }


    public static ItemStack getPart(ItemStack stack, String partSlot)
    {
        NBTTagCompound compound = getPartTag(stack, partSlot);
        if (compound == null) return null;
        if (compound.hasNoTags()) return ItemStack.EMPTY;


        return new ItemStack(compound);
    }

    public static void clearParts(ItemStack stack)
    {
        for (String partSlot : getPartSlots(stack)) removePart(stack, partSlot);
    }

    public static void removePart(ItemStack stack, String partSlot)
    {
        setPartTag(stack, partSlot, new NBTTagCompound());
    }

    public static void setPart(ItemStack stack, String partSlot, ItemStack part)
    {
        setPartTag(stack, partSlot, part.serializeNBT());
    }

    public static boolean itemHasPartInSlot(ItemStack stack, String partSlot)
    {
        ItemStack part = getPart(stack, partSlot);
        return part != null && part != ItemStack.EMPTY;
    }
}
