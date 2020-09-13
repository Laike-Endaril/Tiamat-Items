package com.fantasticsource.tiamatitems.assembly;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatitems.api.IPartSlot;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.settings.CRarity;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tools.Tools;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;

public class ItemAssembly
{
    /**
     * @return All removed parts, if any, and/or the parts passed in if they cannot be placed in a slot
     */
    public static ArrayList<ItemStack> assemble(ItemStack core, ItemStack... parts)
    {
        if (!MCTools.hosting()) throw new IllegalStateException("This method should not be run without a server running!");

        
        ArrayList<ItemStack> result = new ArrayList<>();
        for (ItemStack part : parts) result.addAll(putPartInEmptySlot(core, part));
        return result;
    }


    /**
     * @return All removed parts, if any, and/or the part passed in if it cannot be placed in a slot
     */
    public static ArrayList<ItemStack> putPartInEmptySlot(ItemStack core, ItemStack part)
    {
        return putPartInEmptySlot(core, part, true);
    }

    /**
     * @return All removed parts, if any, and/or the part passed in if it cannot be placed in a slot
     */
    public static ArrayList<ItemStack> putPartInEmptySlot(ItemStack core, ItemStack part, boolean recalcIfChanged)
    {
        return putPartInEmptySlot(core, part, recalcIfChanged, Integer.MAX_VALUE);
    }

    /**
     * @return All removed parts, if any, and/or the part passed in if it cannot be placed in a slot
     */
    public static ArrayList<ItemStack> putPartInEmptySlot(ItemStack core, ItemStack part, int level)
    {
        return putPartInEmptySlot(core, part, true, level);
    }

    /**
     * @return All removed parts, if any, and/or the part passed in if it cannot be placed in a slot
     */
    public static ArrayList<ItemStack> putPartInEmptySlot(ItemStack core, ItemStack part, boolean recalcIfChanged, int level)
    {
        if (!MCTools.hosting()) throw new IllegalStateException("This method should not be run without a server running!");


        if (part.isEmpty() || level < MiscTags.getItemLevelReq(core) + MiscTags.getItemLevelReq(part))
        {
            ArrayList<ItemStack> result = new ArrayList<>();
            result.add(part);
            return result;
        }


        int i = 0, optional = -1;
        for (IPartSlot slot : AssemblyTags.getPartSlots(core))
        {
            if (slot.getPart().isEmpty() && slot.partIsValidForSlot(part))
            {
                if (slot.getRequired()) return putPartInSlot(core, i, part, recalcIfChanged, level);
                else if (optional == -1) optional = i;
            }
            i++;
        }

        if (optional != -1) return putPartInSlot(core, optional, part, recalcIfChanged, level);

        ArrayList<ItemStack> result = new ArrayList<>();
        result.add(part);
        return result;
    }


    /**
     * @return All removed parts, if any, and/or the part passed in if it cannot be placed in the slot
     */
    public static ArrayList<ItemStack> putPartInSlot(ItemStack core, int slot, ItemStack part)
    {
        return putPartInSlot(core, slot, part, true);
    }

    /**
     * @return All removed parts, if any, and/or the part passed in if it cannot be placed in the slot
     */
    public static ArrayList<ItemStack> putPartInSlot(ItemStack core, int slot, ItemStack part, boolean recalcIfChanged)
    {
        return putPartInSlot(core, slot, part, recalcIfChanged, Integer.MAX_VALUE);
    }

    /**
     * @return All removed parts, if any, and/or the part passed in if it cannot be placed in the slot
     */
    public static ArrayList<ItemStack> putPartInSlot(ItemStack core, int slot, ItemStack part, int level)
    {
        return putPartInSlot(core, slot, part, true, level);
    }

    /**
     * @return All removed parts, if any, and/or the part passed in if it cannot be placed in the slot
     */
    public static ArrayList<ItemStack> putPartInSlot(ItemStack core, int slot, ItemStack part, boolean recalcIfChanged, int level)
    {
        if (!MCTools.hosting()) throw new IllegalStateException("This method should not be run without a server running!");


        ArrayList<ItemStack> result = new ArrayList<>();
        if (slot < 0 || core.isEmpty())
        {
            result.add(part);
            return result;
        }


        if (part.isEmpty()) return removePartFromSlot(core, slot, recalcIfChanged, level);


        ArrayList<IPartSlot> partSlots = AssemblyTags.getPartSlots(core);
        if (slot >= partSlots.size())
        {
            result.add(part);
            return result;
        }

        PartSlot partSlot = (PartSlot) partSlots.get(slot);
        if (!partSlot.partIsValidForSlot(part))
        {
            result.add(part);
            return result;
        }

        ItemStack oldPart = partSlot.part;
        int coreLevel = MiscTags.getItemLevelReq(core);
        if (level < coreLevel + Tools.max(MiscTags.getItemLevelReq(part), MiscTags.getItemLevelReq(oldPart)))
        {
            result.add(part);
            return result;
        }


        //Put part into transient data, then save that data to tag
        partSlot.part = part;
        AssemblyTags.setPartSlots(core, partSlots);

        //Disassociate the original part's tag from the original part stack, then delete the original part stack by setting its count to 0
        part.setTagCompound(null);
        part.setCount(0);


        if (!oldPart.isEmpty()) result.add(oldPart);


        if (recalcIfChanged) result.addAll(recalc(core));


        return result;
    }


    /**
     * @return All removed parts, if any
     */
    public static ArrayList<ItemStack> removePartFromSlot(ItemStack core, int slot, boolean recalcIfChanged)
    {
        return removePartFromSlot(core, slot, recalcIfChanged, Integer.MAX_VALUE);
    }

    /**
     * @return All removed parts, if any
     */
    public static ArrayList<ItemStack> removePartFromSlot(ItemStack core, int slot, boolean recalcIfChanged, int level)
    {
        if (!MCTools.hosting()) throw new IllegalStateException("This method should not be run without a server running!");


        ArrayList<IPartSlot> partSlots = AssemblyTags.getPartSlots(core);
        if (slot > partSlots.size()) return new ArrayList<>();

        PartSlot partSlot = (PartSlot) partSlots.get(slot);
        ItemStack part = partSlot.part;
        if (part.isEmpty() || level < MiscTags.getItemLevelReq(core) + MiscTags.getItemLevelReq(part)) return new ArrayList<>();


        partSlot.part = ItemStack.EMPTY;
        ArrayList<ItemStack> result = recalcIfChanged ? recalc(core) : new ArrayList<>();
        result.add(part);
        return result;
    }


    /**
     * Completely recalculates an item (recursively)
     *
     * @return Any parts that can no longer be on the item due to part slot changes or the item being invalid
     */
    public static ArrayList<ItemStack> recalc(ItemStack stack)
    {
        return recalc(stack, false);
    }

    /**
     * Completely recalculates an item (recursively)
     *
     * @return Any parts that can no longer be on the item due to part slot changes or the item being invalid
     */
    public static ArrayList<ItemStack> recalc(ItemStack stack, boolean recursive)
    {
        if (!MCTools.hosting()) throw new IllegalStateException("This method should not be run without a server running!");


        ArrayList<ItemStack> result = new ArrayList<>();


        //Recalc parts, and if they still exist, count and queue them
        ArrayList<IPartSlot> partSlots = AssemblyTags.getPartSlots(stack), oldPartSlots = new ArrayList<>();
        int partCount = 0;
        for (IPartSlot partSlot : partSlots)
        {
            ItemStack part = partSlot.getPart();
            result.addAll(recalc(part, true));
            if (part.isEmpty()) continue;

            partCount++;
            oldPartSlots.add(partSlot);
        }


        //Get internal core (version of item with all trait NBT, but without recalculable traits applied yet)
        ItemStack core = AssemblyTags.getInternalCore(stack);
        if (core.isEmpty())
        {
            //If the core itself is invalid, empty the stack and return all old parts that were on it
            stack.setTagCompound(null);
            stack.setCount(0);

            for (IPartSlot partSlot : oldPartSlots) result.add(partSlot.getPart());
            return result;
        }


        //Validate native traits on core and recalculate them if necessary, applying recalculable traits
        if (!recalcEmptyPartTraits(core, recursive))
        {
            //If the core itself is invalid, empty the stack and return all old parts that were on it
            stack.setTagCompound(null);
            stack.setCount(0);

            for (IPartSlot partSlot : oldPartSlots) result.add(partSlot.getPart());
            return result;
        }


        //At this point, we have our new core, so save it
        AssemblyTags.saveInternalCore(core);


        //If there were no parts on the given stack, we can return now
        if (partCount == 0)
        {
            stack.setTagCompound(core.getTagCompound());
            return result;
        }


        //We have parts to apply

        //Reinsert all parts into fully matching slots if possible
        partSlots = AssemblyTags.getPartSlots(core);
        for (PartSlot oldPartSlot : oldPartSlots.toArray(new PartSlot[0]))
        {
            ItemStack part = oldPartSlot.part;

            for (IPartSlot newPartSlot : partSlots)
            {
                if (!newPartSlot.getPart().isEmpty()) continue;

                if (newPartSlot.getSlotType().equals(oldPartSlot.slotType) && newPartSlot.getRequired() == oldPartSlot.required && newPartSlot.partIsValidForSlot(part))
                {
                    newPartSlot.setPart(part);
                    oldPartSlots.remove(oldPartSlot);
                    break;
                }
            }
        }

        //Reinsert remaining parts into slots that match type but not whether they're required or not, if possible
        for (PartSlot oldPartSlot : oldPartSlots.toArray(new PartSlot[0]))
        {
            ItemStack part = oldPartSlot.part;

            for (IPartSlot newPartSlot : partSlots)
            {
                if (!newPartSlot.getPart().isEmpty()) continue;

                if (newPartSlot.getSlotType().equals(oldPartSlot.slotType) && newPartSlot.partIsValidForSlot(part))
                {
                    newPartSlot.setPart(part);
                    oldPartSlots.remove(oldPartSlot);
                    break;
                }
            }
        }

        //Reinsert remaining parts into slots that don't match but are valid, if possible
        for (PartSlot oldPartSlot : oldPartSlots.toArray(new PartSlot[0]))
        {
            ItemStack part = oldPartSlot.part;

            for (IPartSlot newPartSlot : partSlots)
            {
                if (!newPartSlot.getPart().isEmpty()) continue;

                if (newPartSlot.partIsValidForSlot(part))
                {
                    newPartSlot.setPart(part);
                    oldPartSlots.remove(oldPartSlot);
                    break;
                }
            }
        }

        //Add remaining parts to list of resulting leftovers
        for (IPartSlot oldPartSlot : oldPartSlots)
        {
            result.add(oldPartSlot.getPart());
        }


        //Apply NBT and value from parts to core
        if (!core.hasTagCompound()) core.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = core.getTagCompound();
        int value = MiscTags.getItemValue(core);
        for (IPartSlot partSlot : partSlots)
        {
            ItemStack part = partSlot.getPart();
            MCTools.mergeNBT(compound, false, part.getTagCompound());
            value += MiscTags.getItemValue(part);
        }
        MiscTags.setItemValue(core, value);


        //Set part tags
        AssemblyTags.setPartSlots(core, partSlots);


        //Set current stack to new calculated one
        stack.setTagCompound(core.getTagCompound());


        //Return removed parts
        return result;
    }


    /**
     * Validates and recalculates the traits of an *empty part*
     * Returns false if the part itself should be deleted (eg. if its item type no longer exists)
     */
    private static boolean recalcEmptyPartTraits(ItemStack stack, boolean recursive)
    {
        if (!MCTools.hosting()) throw new IllegalStateException("This method should not be run without a server running!");


        //Validate itemtype/rarity
        CItemType itemType = CSettings.SETTINGS.itemTypes.get(MiscTags.getItemTypeName(stack));
        if (itemType == null) return false;

        CRarity rarity = MiscTags.getItemRarity(stack);
        if (rarity == null) return false;


        //Re-apply item type and return
        itemType.applyItemType(stack, MiscTags.getItemLevel(stack), rarity, recursive);
        return true;
    }
}
