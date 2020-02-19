package com.fantasticsource.tiamatitems.assembly;

import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.nbt.TraitTags;
import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tiamatitems.trait.CTrait;
import com.fantasticsource.tiamatitems.trait.CTraitPool;
import com.fantasticsource.tools.Tools;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ItemAssembly
{
    /**
     * @return ItemStack of removed part, if any, or the part passed in if it cannot be placed in the slot
     */
    public static ItemStack putPartInSlot(ItemStack core, int slot, ItemStack part, boolean recalcIfChanged)
    {
        return putPartInSlot(core, slot, part, recalcIfChanged, Integer.MAX_VALUE);
    }

    /**
     * @return ItemStack of removed part, if any, or the part passed in if it cannot be placed in the slot
     */
    public static ItemStack putPartInSlot(ItemStack core, int slot, ItemStack part, boolean recalcIfChanged, int level)
    {
        if (part.isEmpty()) return removePart(core, slot, recalcIfChanged, level);


        ArrayList<PartSlot> partSlots = AssemblyTags.getPartSlots(core);
        if (slot > partSlots.size()) return part;

        PartSlot partSlot = partSlots.get(slot);
        if (!partSlot.partIsValidForSlot(part)) return part;

        ItemStack oldPart = partSlot.part;
        int coreLevel = MiscTags.getItemLevelReq(core);
        if (level < coreLevel + Tools.max(MiscTags.getItemLevelReq(part), MiscTags.getItemLevelReq(oldPart))) return part;


        int empty = true;
        for (PartSlot partSlot2 : partSlots)
        {
            if (!partSlot2.part.isEmpty())
            {
                empty = false;
                break;
            }
        }
        if (empty)
        {
            //TODO Store the empty version of the core internally
        }
        //TODO Apply part to slot in NBT
        //TODO transform the core item
    }


    /**
     * @return ItemStack of removed part
     */
    public static ItemStack removePart(ItemStack core, int slot, boolean recalcIfChanged)
    {
        return removePart(core, slot, recalcIfChanged, Integer.MAX_VALUE);
    }

    /**
     * @return ItemStack of removed part
     */
    public static ItemStack removePart(ItemStack core, int slot, boolean recalcIfChanged, int level)
    {
        ArrayList<PartSlot> partSlots = AssemblyTags.getPartSlots(core);
        if (slot > partSlots.size()) return ItemStack.EMPTY;

        PartSlot partSlot = partSlots.get(slot);
        ItemStack part = partSlot.part;
        if (part.isEmpty() || level < MiscTags.getItemLevelReq(core) + MiscTags.getItemLevelReq(part)) return ItemStack.EMPTY;


        partSlot.part = ItemStack.EMPTY;
        if (recalcIfChanged) validateAndRecalc(core);
        return part;
    }


    /**
     * Completely recalculates an item (recursively)
     *
     * @return Any parts that can no longer be on the item due to part slot changes
     */
    public static ArrayList<ItemStack> validateAndRecalc(ItemStack stack)
    {
        ArrayList<ItemStack> result = new ArrayList<>();


        //Count and queue parts
        ArrayList<PartSlot> partSlots = AssemblyTags.getPartSlots(stack), oldPartSlots = new ArrayList<>();
        int partCount = 0;
        for (PartSlot partSlot : partSlots)
        {
            if (partSlot.part.isEmpty()) continue;

            partCount++;
            oldPartSlots.add(partSlot);
        }


        //Get clean core
        ItemStack core = AssemblyTags.getInternalCore(stack); //Internal core should always be clean
        if (core.isEmpty()) //If no internal core, there should never be any part traits applied yet
        {
            core = new ItemStack(stack.serializeNBT());
            AssemblyTags.clearPartTags(core); //So just clear the part tags on a copy of this item to get a clean core
        }


        //Validate native traits on clean core and recalculate them if necessary
        if (!validateAndRecalcEmptyPart(core))
        {
            //If the core itself is invalid, empty the stack and return all old parts that were on it
            stack.setTagCompound(null);
            stack.setCount(0);

            for (PartSlot partSlot : oldPartSlots) result.add(partSlot.part);
            return result;
        }


        //If there were no parts on the given stack, just construct a clean core with no internal core and return
        if (partCount == 0)
        {
            AssemblyTags.removeInternalCore(core);
            stack.setTagCompound(core.getTagCompound());
            return result;
        }


        //We have parts to apply

        //Set the core of the clean core to...well...itself
        AssemblyTags.saveInternalCore(core);

        //Recalc all parts and reinsert them into fully matching slots if possible
        partSlots = AssemblyTags.getPartSlots(core);
        for (PartSlot oldPartSlot : oldPartSlots.toArray(new PartSlot[0]))
        {
            ItemStack part = oldPartSlot.part;
            result.addAll(validateAndRecalc(part));

            for (PartSlot newPartSlot : partSlots)
            {
                if (!newPartSlot.part.isEmpty()) continue;

                if (newPartSlot.slotType.equals(oldPartSlot.slotType) && newPartSlot.required == oldPartSlot.required && newPartSlot.partIsValidForSlot(part))
                {
                    newPartSlot.part = part;
                    oldPartSlots.remove(oldPartSlot);
                    break;
                }
            }
        }

        //Reinsert remaining parts into slots that match type but not whether they're required or not, if possible
        for (PartSlot oldPartSlot : oldPartSlots.toArray(new PartSlot[0]))
        {
            ItemStack part = oldPartSlot.part;

            for (PartSlot newPartSlot : partSlots)
            {
                if (!newPartSlot.part.isEmpty()) continue;

                if (newPartSlot.slotType.equals(oldPartSlot.slotType) && newPartSlot.partIsValidForSlot(part))
                {
                    newPartSlot.part = part;
                    oldPartSlots.remove(oldPartSlot);
                    break;
                }
            }
        }

        //Reinsert remaining parts into slots that don't match but are valid, if possible
        for (PartSlot oldPartSlot : oldPartSlots.toArray(new PartSlot[0]))
        {
            ItemStack part = oldPartSlot.part;

            for (PartSlot newPartSlot : partSlots)
            {
                if (!newPartSlot.part.isEmpty()) continue;

                if (newPartSlot.partIsValidForSlot(part))
                {
                    newPartSlot.part = part;
                    oldPartSlots.remove(oldPartSlot);
                    break;
                }
            }
        }

        //Add remaining parts to leftovers list
        for (PartSlot oldPartSlot : oldPartSlots)
        {
            result.add(oldPartSlot.part);
        }

        //Set part tags
        AssemblyTags.setPartSlots(core, partSlots);


        //Apply traits from parts
        for (PartSlot partSlot : partSlots)
        {
            applyTraits(core, partSlot.part);
        }


        //Set current stack to new calculated one
        stack.setTagCompound(core.getTagCompound());

        //Return removed parts
        return result;
    }


    /**
     * Validates the traits of an *empty part* and recalculates them if necessary
     * Returns false if the part itself should be deleted (eg. if its item type no longer exists)
     */
    private static boolean validateAndRecalcEmptyPart(ItemStack stack)
    {
        //TODO
    }


    /**
     * Applies traits from a part to a core
     * Should only be used for validated cores and parts
     */
    private static void applyTraits(ItemStack core, ItemStack... parts)
    {
        int value = MiscTags.getItemValue(core);
        for (ItemStack part : parts)
        {
            CItemType itemType = CItemType.itemTypes.get(MiscTags.getItemType(part));

            for (String traitString : TraitTags.getTraitStrings(part))
            {
                String[] tokens = Tools.fixedSplit(traitString, ":");

                if (tokens.length == 3)
                {
                    //Static trait
                    CTrait trait = itemType.staticTraits.get(tokens[1]);
                    value += trait.applyToItem(core, "Static", itemType, MiscTags.getItemLevel(part), null, Integer.parseInt(tokens[2]));
                }
                else
                {
                    //Weighted trait
                    String poolSetName = tokens[0];
                    LinkedHashMap<String, CTraitPool> poolSet = itemType.randomTraitPoolSets.get(poolSetName);
                    CTraitPool pool = poolSet.get(tokens[1]);
                    CTrait trait = null;
                    for (CTrait trait2 : pool.traitGenWeights.keySet())
                    {
                        if (trait2.name.equals(tokens[2]))
                        {
                            trait = trait2;
                            break;
                        }
                    }
                    value += trait.applyToItem(core, poolSetName, itemType, MiscTags.getItemLevel(part), pool, Integer.parseInt(tokens[3]));
                }
            }
        }
    }
}
