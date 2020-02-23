package com.fantasticsource.tiamatitems.assembly;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatitems.globalsettings.CRarity;
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
        if (slot < 0 || core.isEmpty()) return new ArrayList<>();


        if (part.isEmpty()) return removePartFromSlot(core, slot, recalcIfChanged, level);


        ArrayList<ItemStack> result = new ArrayList<>();

        ArrayList<PartSlot> partSlots = AssemblyTags.getPartSlots(core);
        if (slot >= partSlots.size())
        {
            result.add(part);
            return result;
        }

        PartSlot partSlot = partSlots.get(slot);
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
        ArrayList<PartSlot> partSlots = AssemblyTags.getPartSlots(core);
        if (slot > partSlots.size()) return new ArrayList<>();

        PartSlot partSlot = partSlots.get(slot);
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
        if (core.isEmpty()) //If no internal core, create one
        {
            //Tell automatic recalc event not to recalc the core, by marking it with a special version number
            long version = MiscTags.getItemGenVersion(stack);
            MiscTags.setItemGenVersion(stack, Long.MAX_VALUE);

            //Copy the stack
            core = MCTools.cloneItemStack(stack);

            //Reset version on original and new stack
            MiscTags.setItemGenVersion(stack, version);
            MiscTags.setItemGenVersion(core, version);

            //Clear the part tags on the new core
            AssemblyTags.clearPartTags(core);
        }


        //Validate native traits on clean core and recalculate them if necessary
        if (!recalcEmptyPartTraits(core))
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
            result.addAll(recalc(part));

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
     * Validates and recalculates the traits of an *empty part*
     * Returns false if the part itself should be deleted (eg. if its item type no longer exists)
     */
    private static boolean recalcEmptyPartTraits(ItemStack stack)
    {
        //Validate itemtype/rarity
        CItemType itemType = CItemType.itemTypes.get(MiscTags.getItemTypeName(stack));
        if (itemType == null) return false;

        CRarity rarity = MiscTags.getItemRarity(stack);
        if (rarity == null) return false;


        //Recalc
        ItemStack newItem = itemType.generateItem(MiscTags.getItemLevel(stack), rarity, TraitTags.getTraitStrings(stack));

        //Set this stack's tag and return
        stack.setTagCompound(newItem.getTagCompound());
        return true;
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
            value += MiscTags.getItemValue(part);
            CItemType itemType = CItemType.itemTypes.get(MiscTags.getItemTypeName(part));

            for (String traitString : TraitTags.getTraitStrings(part))
            {
                String[] tokens = Tools.fixedSplit(traitString, ":");

                if (tokens.length == 3)
                {
                    //Static trait
                    CTrait trait = itemType.staticTraits.get(tokens[1]);
                    trait.applyToItem(core, "Static", itemType, MiscTags.getItemLevel(part), null, Integer.parseInt(tokens[2]));
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
                    trait.applyToItem(core, poolSetName, itemType, MiscTags.getItemLevel(part), pool, Integer.parseInt(tokens[3]));
                }
            }
        }
        MiscTags.setItemValue(core, value);
    }
}