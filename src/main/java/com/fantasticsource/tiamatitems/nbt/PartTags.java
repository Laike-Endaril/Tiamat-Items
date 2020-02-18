package com.fantasticsource.tiamatitems.nbt;

import com.fantasticsource.tiamatitems.globalsettings.PartSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;

import static com.fantasticsource.tiamatitems.TiamatItems.DOMAIN;

public class PartTags
{
    public static void addPartSlot(ItemStack stack, String slotType)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("partSlots")) compound.setTag("partSlots", new NBTTagList());
        NBTTagList list = compound.getTagList("partSlots", Constants.NBT.TAG_COMPOUND);

        compound = new NBTTagCompound();
        list.appendTag(compound);

        compound.setTag("type", new NBTTagString(slotType));
    }

    public static void clearPartSlots(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("partSlots")) return;

        compound.removeTag("partSlots");
        if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
    }

    public static void removePartSlot(ItemStack stack, String slotType)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("partSlots")) return;

        NBTTagList list = compound.getTagList("partSlots", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound slotCompound = list.getCompoundTagAt(i);
            if (slotCompound.getString("type").equals(slotType))
            {
                list.removeTag(i);
                if (list.tagCount() == 0)
                {
                    compound.removeTag("partSlots");
                    if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
                }
                return;
            }
        }
    }


    public static ArrayList<PartSlot> getPartSlots(ItemStack stack)
    {
        ArrayList<PartSlot> result = new ArrayList<>();

        if (!stack.hasTagCompound()) return result;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return result;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("partSlots")) return result;

        NBTTagList list = compound.getTagList("partSlots", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++)
        {
            compound = list.getCompoundTagAt(i);

            PartSlot slot = new PartSlot(compound.getString("type"));
            slot.required = compound.getBoolean("required");
            slot.part = new ItemStack(compound.getCompoundTag("part"));

            result.add(slot);
        }

        return result;
    }

    public static void setPartSlots(ItemStack stack, ArrayList<PartSlot> partSlots)
    {
        if (partSlots.size() == 0)
        {
            clearPartSlots(stack);
            return;
        }


        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());

        compound = compound.getCompoundTag(DOMAIN);

        NBTTagList list = new NBTTagList();
        for (PartSlot partSlot : partSlots)
        {
            NBTTagCompound partCompound = new NBTTagCompound();
            partCompound.setTag("type", new NBTTagString(partSlot.slotType));
            if (partSlot.required) partCompound.setBoolean("required", true);
            if (!partSlot.part.isEmpty()) partCompound.setTag("part", partSlot.part.serializeNBT());

            list.appendTag(partCompound);
        }

        compound.setTag("partSlots", list);
    }
}
