package com.fantasticsource.tiamatitems.nbt;

import com.fantasticsource.tiamatitems.assembly.PartSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;

import static com.fantasticsource.tiamatitems.TiamatItems.DOMAIN;

public class AssemblyTags
{
    public static void addPartSlot(ItemStack stack, String slotType)
    {
        addPartSlot(stack, slotType, false);
    }

    public static void addPartSlot(ItemStack stack, String slotType, boolean required)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("partSlots")) compound.setTag("partSlots", new NBTTagList());
        NBTTagList list = compound.getTagList("partSlots", Constants.NBT.TAG_COMPOUND);

        compound = new NBTTagCompound();
        list.appendTag(compound);

        compound.setString("type", slotType);
        if (required) compound.setBoolean("required", true);
    }

    public static void clearPartSlots(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("partSlots")) return;

        compound.removeTag("partSlots");
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
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
                    if (compound.hasNoTags())
                    {
                        mainTag.removeTag(DOMAIN);
                        if (mainTag.hasNoTags()) stack.setTagCompound(null);
                    }
                }
                return;
            }
        }
    }


    public static void clearPartTags(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("partSlots")) return;

        NBTTagList list = compound.getTagList("partSlots", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++)
        {
            list.getCompoundTagAt(i).removeTag("part");
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
            partCompound.setString("type", partSlot.slotType);
            if (partSlot.required) partCompound.setBoolean("required", true);
            if (!partSlot.part.isEmpty()) partCompound.setTag("part", partSlot.part.serializeNBT());

            list.appendTag(partCompound);
        }

        compound.setTag("partSlots", list);
    }


    public static boolean isUsable(ItemStack stack)
    {
        for (PartSlot partSlot : getPartSlots(stack))
        {
            if (!partSlot.required) continue;

            if (partSlot.part.isEmpty() || !isUsable(partSlot.part)) return false;
        }

        return true;
    }


    public static void saveInternalCore(ItemStack core)
    {
        saveInternalCore(core, core);
    }

    public static boolean saveInternalCore(ItemStack assembly, ItemStack core)
    {
        if (core.isEmpty())
        {
            removeInternalCore(assembly);
            return true;
        }


        if (!assembly.hasTagCompound()) assembly.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = assembly.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        try
        {
            compound.setTag("core", JsonToNBT.getTagFromJson(core.serializeNBT().toString()));
            return true;
        }
        catch (NBTException e)
        {
            return false;
        }
    }

    public static ItemStack getInternalCore(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return ItemStack.EMPTY;
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) return ItemStack.EMPTY;
        compound = compound.getCompoundTag(DOMAIN);

        return new ItemStack(compound.getCompoundTag("core"));
    }

    public static void removeInternalCore(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;
        NBTTagCompound mainTag = stack.getTagCompound();

        if (!mainTag.hasKey(DOMAIN)) return;
        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);

        compound.removeTag("core");
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
    }
}
