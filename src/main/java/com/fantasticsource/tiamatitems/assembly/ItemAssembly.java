package com.fantasticsource.tiamatitems.assembly;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatitems.api.IPartSlot;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.nbt.TraitTags;
import com.fantasticsource.tiamatitems.settings.CRarity;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTrait;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;

public class ItemAssembly
{
    public static final boolean VERBOSE_DEBUG = false;


    /**
     * @return All removed parts, if any, and/or the parts passed in if they cannot be placed in a slot
     */
    public static ArrayList<ItemStack> assemble(ItemStack mainPart, ItemStack... otherParts)
    {
        if (!MCTools.hosting()) throw new IllegalStateException("This method should not be run without a server running!");


        ArrayList<ItemStack> result = new ArrayList<>();
        for (ItemStack part : otherParts) result.addAll(putPartInEmptySlot(mainPart, part, false));
        result.addAll(recalc(mainPart));
        return result;
    }

    /**
     * @return All removed parts, if any
     */
    public static ArrayList<ItemStack> disassemble(ItemStack assembly)
    {
        if (!MCTools.hosting()) throw new IllegalStateException("This method should not be run without a server running!");


        ArrayList<ItemStack> result = new ArrayList<>();
        int size = AssemblyTags.getPartSlots(assembly).size();
        for (int i = 0; i < size; i++) result.addAll(removePartFromSlot(assembly, i, false));
        result.addAll(recalc(assembly));
        return result;
    }


    /**
     * @return All removed parts, if any, and/or the part passed in if it cannot be placed in a slot
     */
    public static ArrayList<ItemStack> putPartInEmptySlot(ItemStack mainPart, ItemStack otherPart)
    {
        return putPartInEmptySlot(mainPart, otherPart, true);
    }

    /**
     * @return All removed parts, if any, and/or the part passed in if it cannot be placed in a slot
     */
    public static ArrayList<ItemStack> putPartInEmptySlot(ItemStack mainPart, ItemStack otherPart, boolean recalcIfChanged)
    {
        return putPartInEmptySlot(mainPart, otherPart, recalcIfChanged, Integer.MAX_VALUE);
    }

    /**
     * @return All removed parts, if any, and/or the part passed in if it cannot be placed in a slot
     */
    public static ArrayList<ItemStack> putPartInEmptySlot(ItemStack mainPart, ItemStack otherPart, int level)
    {
        return putPartInEmptySlot(mainPart, otherPart, true, level);
    }

    /**
     * @return All removed parts, if any, and/or the part passed in if it cannot be placed in a slot
     */
    public static ArrayList<ItemStack> putPartInEmptySlot(ItemStack mainPart, ItemStack otherPart, boolean recalcIfChanged, int level)
    {
        if (!MCTools.hosting()) throw new IllegalStateException("This method should not be run without a server running!");


        if (otherPart.isEmpty() || level < MiscTags.getItemLevelReq(mainPart) + MiscTags.getItemLevelReq(otherPart))
        {
            ArrayList<ItemStack> result = new ArrayList<>();
            result.add(otherPart);
            return result;
        }


        int i = 0, optional = -1;
        for (IPartSlot slot : AssemblyTags.getPartSlots(mainPart))
        {
            if (slot.getPart().isEmpty() && slot.partIsValidForSlot(otherPart))
            {
                if (slot.getRequired()) return putPartInSlot(mainPart, i, otherPart, recalcIfChanged, level);
                else if (optional == -1) optional = i;
            }
            i++;
        }

        if (optional != -1) return putPartInSlot(mainPart, optional, otherPart, recalcIfChanged, level);

        ArrayList<ItemStack> result = new ArrayList<>();
        result.add(otherPart);
        return result;
    }


    /**
     * @return All removed parts, if any, and/or the part passed in if it cannot be placed in the slot
     */
    public static ArrayList<ItemStack> putPartInSlot(ItemStack mainPart, int slot, ItemStack otherPart)
    {
        return putPartInSlot(mainPart, slot, otherPart, true);
    }

    /**
     * @return All removed parts, if any, and/or the part passed in if it cannot be placed in the slot
     */
    public static ArrayList<ItemStack> putPartInSlot(ItemStack mainPart, int slot, ItemStack otherPart, boolean recalcIfChanged)
    {
        return putPartInSlot(mainPart, slot, otherPart, recalcIfChanged, Integer.MAX_VALUE);
    }

    /**
     * @return All removed parts, if any, and/or the part passed in if it cannot be placed in the slot
     */
    public static ArrayList<ItemStack> putPartInSlot(ItemStack mainPart, int slot, ItemStack otherPart, int level)
    {
        return putPartInSlot(mainPart, slot, otherPart, true, level);
    }

    /**
     * @return All removed parts, if any, and/or the part passed in if it cannot be placed in the slot
     */
    public static ArrayList<ItemStack> putPartInSlot(ItemStack mainPart, int slot, ItemStack otherPart, boolean recalcIfChanged, int level)
    {
        if (!MCTools.hosting()) throw new IllegalStateException("This method should not be run without a server running!");


        ArrayList<ItemStack> result = new ArrayList<>();
        if (slot < 0 || mainPart.isEmpty())
        {
            result.add(otherPart);
            return result;
        }


        if (otherPart.isEmpty()) return removePartFromSlot(mainPart, slot, recalcIfChanged, level);


        ArrayList<IPartSlot> partSlots = AssemblyTags.getPartSlots(mainPart);
        if (slot >= partSlots.size())
        {
            result.add(otherPart);
            return result;
        }

        PartSlot partSlot = (PartSlot) partSlots.get(slot);
        if (!partSlot.partIsValidForSlot(otherPart))
        {
            result.add(otherPart);
            return result;
        }

        ItemStack oldPart = partSlot.part;
        int mainLevel = MiscTags.getItemLevelReq(mainPart);
        if (level < mainLevel + Tools.max(MiscTags.getItemLevelReq(otherPart), MiscTags.getItemLevelReq(oldPart)))
        {
            result.add(otherPart);
            return result;
        }


        //Put part into transient data, then save that data to tag
        partSlot.part = otherPart;
        AssemblyTags.setPartSlots(mainPart, partSlots);

        //Disassociate the original part's tag from the original part stack, then delete the original part stack by setting its count to 0
        otherPart.setTagCompound(null);
        otherPart.setCount(0);


        if (!oldPart.isEmpty()) result.add(oldPart);


        if (recalcIfChanged) result.addAll(recalc(mainPart));


        return result;
    }


    /**
     * @return All removed parts, if any
     */
    public static ArrayList<ItemStack> removePartFromSlot(ItemStack assembly, int slot)
    {
        return removePartFromSlot(assembly, slot, true);
    }

    /**
     * @return All removed parts, if any
     */
    public static ArrayList<ItemStack> removePartFromSlot(ItemStack assembly, int slot, boolean recalcIfChanged)
    {
        return removePartFromSlot(assembly, slot, recalcIfChanged, Integer.MAX_VALUE);
    }

    /**
     * @return All removed parts, if any
     */
    public static ArrayList<ItemStack> removePartFromSlot(ItemStack assembly, int slot, boolean recalcIfChanged, int level)
    {
        if (!MCTools.hosting()) throw new IllegalStateException("This method should not be run without a server running!");

        ArrayList<IPartSlot> partSlots = AssemblyTags.getPartSlots(assembly);
        if (slot > partSlots.size()) return new ArrayList<>();

        PartSlot partSlot = (PartSlot) partSlots.get(slot);
        ItemStack part = partSlot.part;
        if (part.isEmpty() || level < MiscTags.getItemLevelReq(assembly) + MiscTags.getItemLevelReq(part)) return new ArrayList<>();

        partSlot.part = ItemStack.EMPTY;
        AssemblyTags.setPartSlots(assembly, partSlots);

        ArrayList<ItemStack> result = recalcIfChanged ? recalc(assembly) : new ArrayList<>();
        result.add(part);
        return result;
    }


    public static void validate(EntityPlayerMP player, ItemStack stack, boolean returnPartsIfValid)
    {
        if (!returnPartsIfValid) validate(stack);
        else for (ItemStack stack1 : validate(stack)) MCTools.give(player, stack1);
    }


    public static ArrayList<ItemStack> validate(ItemStack stack)
    {
        ArrayList<ItemStack> result = new ArrayList<>();

        if (stack.isEmpty() || !stack.hasTagCompound()) return result;

        String itemTypeName = MiscTags.getItemTypeName(stack);
        if (itemTypeName.equals("")) return result;

        if (MiscTags.getItemGenVersion(stack) == CSettings.LOCAL_SETTINGS.getVersion()) return result;


        return recalc(stack);
    }


    public static void recalc(EntityPlayerMP player, ItemStack stack, boolean returnPartsIfValid)
    {
        if (!returnPartsIfValid) recalc(stack);
        else for (ItemStack stack1 : recalc(stack)) MCTools.give(player, stack1);
    }


    /**
     * Completely recalculates an item (recursively)
     *
     * @return Any parts that can no longer be on the item due to part slot changes or the item being invalid
     */
    protected static ArrayList<ItemStack> recalc(ItemStack stack)
    {
        if (!MCTools.hosting()) throw new IllegalStateException("This method should not be run without a server running!");

        ArrayList<ItemStack> result = new ArrayList<>();


        //Recalc parts, and if they still exist, count and queue them
        ArrayList<IPartSlot> partSlots = AssemblyTags.getPartSlots(stack), oldPartSlots = new ArrayList<>();
        int partCount = 0;
        for (IPartSlot partSlot : partSlots)
        {
            ItemStack part = partSlot.getPart();
            if (part.isEmpty()) continue;

            result.addAll(recalc(part));
            if (part.isEmpty()) continue;

            partCount++;
            oldPartSlots.add(partSlot);
        }


        if (VERBOSE_DEBUG)
        {
            debug("Part count: " + partCount);
            for (IPartSlot partSlot : oldPartSlots)
            {
                debug("");
                debug("Type: " + partSlot.getSlotType());
                debug("Is required: " + partSlot.getRequired());
                debug("Contains valid part: " + partSlot.partIsValidForSlot(partSlot.getPart()));
                debug("Part: " + partSlot.getPart().getDisplayName());
            }
        }


        //Check for valid item type and rarity
        CItemType itemType = CSettings.LOCAL_SETTINGS.itemTypes.get(MiscTags.getItemTypeName(stack));
        CRarity rarity = MiscTags.getItemRarity(stack);
        if (itemType == null || rarity == null)
        {
            if (VERBOSE_DEBUG)
            {
                debug("");
                if (itemType == null) debug("Item type is null: " + MiscTags.getItemTypeName(stack) + " for itemstack: " + stack.getDisplayName());
                else debug("Rarity is null: " + MiscTags.getItemRarity(stack) + " for itemstack: " + stack.getDisplayName());
            }


            MCTools.destroyItemStack(stack);
            for (IPartSlot partSlot : oldPartSlots) result.add(partSlot.getPart());
            return result;
        }


        //Get internal core (version of item with all its own trait NBT, but without recalculable traits applied and without NBT from parts)
        ItemStack assembly = AssemblyTags.hasInternalCore(stack) ? AssemblyTags.getInternalCore(stack) : stack;
        if (assembly.isEmpty())
        {
            if (VERBOSE_DEBUG)
            {
                debug("");
                debug("Core is empty for itemstack: " + stack.getDisplayName());
            }


            //If the core itself is invalid, empty the stack and return all old parts that were on it
            MCTools.destroyItemStack(stack);
            for (IPartSlot partSlot : oldPartSlots) result.add(partSlot.getPart());
            return result;
        }


        //Validate native traits on core and recalculate them if necessary, applying recalculable traits
        if (!recalcEmptyPartTraits(assembly))
        {
            if (VERBOSE_DEBUG)
            {
                debug("");
                debug("Failed to recalc empty part traits for itemstack: " + stack.getDisplayName());
            }


            //If the core itself is invalid, empty the stack and return all old parts that were on it
            MCTools.destroyItemStack(stack);
            for (IPartSlot partSlot : oldPartSlots) result.add(partSlot.getPart());
            return result;
        }


        //If we're now destroyable and at 0 or less remaining durability, destroy the item
        if (MiscTags.isDestroyable(assembly) && MiscTags.getItemDamage(stack) >= MiscTags.getItemDurability(stack))
        {
            MCTools.destroyItemStack(stack);
            for (IPartSlot partSlot : oldPartSlots) result.add(partSlot.getPart());
            return result;
        }


        //If there were no parts on the given stack, we can return now
        if (partCount == 0)
        {
            if (VERBOSE_DEBUG)
            {
                debug("");
                debug("Return assembly with no parts in slots");
            }


            stack.setTagCompound(assembly.getTagCompound());
            return result;
        }


        //We have parts to apply

        //Reinsert all parts into fully matching slots if possible
        partSlots = AssemblyTags.getPartSlots(assembly);
        for (PartSlot oldPartSlot : oldPartSlots.toArray(new PartSlot[0]))
        {
            ItemStack part = oldPartSlot.part;

            for (IPartSlot newPartSlot : partSlots)
            {
                if (!newPartSlot.getPart().isEmpty()) continue;

                if (newPartSlot.getSlotType().equals(oldPartSlot.slotType) && newPartSlot.getRequired() == oldPartSlot.required && newPartSlot.partIsValidForSlot(part))
                {
                    if (VERBOSE_DEBUG)
                    {
                        debug("");
                        debug("Found full match to reinsert part");
                        debug("");
                        debug("Old...");
                        debug("Type: " + oldPartSlot.getSlotType());
                        debug("Is required: " + oldPartSlot.getRequired());
                        debug("Contains valid part: " + oldPartSlot.partIsValidForSlot(oldPartSlot.getPart()));
                        debug("Part: " + oldPartSlot.getPart().getDisplayName());
                        debug("");
                        debug("New...");
                        debug("Type: " + newPartSlot.getSlotType());
                        debug("Is required: " + newPartSlot.getRequired());
                        debug("Contains valid part: " + newPartSlot.partIsValidForSlot(newPartSlot.getPart()));
                        debug("Part: " + newPartSlot.getPart().getDisplayName());
                    }


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
                    if (VERBOSE_DEBUG)
                    {
                        debug("");
                        debug("Found partial match to reinsert part");
                        debug("");
                        debug("Old...");
                        debug("Type: " + oldPartSlot.getSlotType());
                        debug("Is required: " + oldPartSlot.getRequired());
                        debug("Contains valid part: " + oldPartSlot.partIsValidForSlot(oldPartSlot.getPart()));
                        debug("Part: " + oldPartSlot.getPart().getDisplayName());
                        debug("");
                        debug("New...");
                        debug("Type: " + newPartSlot.getSlotType());
                        debug("Is required: " + newPartSlot.getRequired());
                        debug("Contains valid part: " + newPartSlot.partIsValidForSlot(newPartSlot.getPart()));
                        debug("Part: " + newPartSlot.getPart().getDisplayName());
                    }


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
                    if (VERBOSE_DEBUG)
                    {
                        debug("");
                        debug("Found mismatch to reinsert part");
                        debug("");
                        debug("Old...");
                        debug("Type: " + oldPartSlot.getSlotType());
                        debug("Is required: " + oldPartSlot.getRequired());
                        debug("Contains valid part: " + oldPartSlot.partIsValidForSlot(oldPartSlot.getPart()));
                        debug("Part: " + oldPartSlot.getPart().getDisplayName());
                        debug("");
                        debug("New...");
                        debug("Type: " + newPartSlot.getSlotType());
                        debug("Is required: " + newPartSlot.getRequired());
                        debug("Contains valid part: " + newPartSlot.partIsValidForSlot(newPartSlot.getPart()));
                        debug("Part: " + newPartSlot.getPart().getDisplayName());
                    }


                    newPartSlot.setPart(part);
                    oldPartSlots.remove(oldPartSlot);
                    break;
                }
            }
        }

        //Add remaining parts to list of resulting leftovers
        for (IPartSlot oldPartSlot : oldPartSlots)
        {
            if (VERBOSE_DEBUG)
            {
                debug("");
                debug("Did not find anywhere to reinsert part");
                debug("");
                debug("Old...");
                debug("Type: " + oldPartSlot.getSlotType());
                debug("Is required: " + oldPartSlot.getRequired());
                debug("Contains valid part: " + oldPartSlot.partIsValidForSlot(oldPartSlot.getPart()));
                debug("Part: " + oldPartSlot.getPart().getDisplayName());
            }


            result.add(oldPartSlot.getPart());
        }


        //Set part tags
        AssemblyTags.setPartSlots(assembly, partSlots);


        //Apply recalculable traits from parts
        if (!assembly.hasTagCompound()) assembly.setTagCompound(new NBTTagCompound());
        int value = MiscTags.getItemValue(assembly);
        for (ItemStack part : AssemblyTags.getNonEmptyPartsRecursive(assembly, false))
        {
            CItemType partItemType = CSettings.LOCAL_SETTINGS.itemTypes.get(MiscTags.getItemTypeName(part));
            CRarity partRarity = MiscTags.getItemRarity(part);
            if (partItemType == null || partRarity == null) continue;

            double itemTypeAndLevelMultiplier = partItemType.traitLevelMultiplier * (CSettings.LOCAL_SETTINGS.baseMultiplier + (CSettings.LOCAL_SETTINGS.multiplierBonusPerLevel * partRarity.itemLevelModifier + MiscTags.getItemLevel(part)));

            for (String traitString : TraitTags.getTraitStrings(part))
            {
                String[] tokens = Tools.fixedSplit(traitString, ":");
                if (tokens[0].equals("Static"))
                {
                    CRecalculableTrait trait = partItemType.staticRecalculableTraits.get(tokens[1]);
                    if (!trait.addToAssemblyFromPart) continue;

                    int[] baseArgs = new int[tokens.length - 2];
                    for (int i = 0; i < baseArgs.length; i++) baseArgs[i] = Integer.parseInt(tokens[i + 2]);

                    trait.applyToItem(assembly, itemTypeAndLevelMultiplier, baseArgs);
                }
                else
                {
                    CRecalculableTrait trait = null;
                    for (CRecalculableTrait trait2 : CSettings.LOCAL_SETTINGS.recalcTraitPools.get(tokens[1]).traitGenWeights.keySet())
                    {
                        if (trait2.name.equals(tokens[2]))
                        {
                            trait = trait2;
                            break;
                        }
                    }

                    if (trait == null || !trait.addToAssemblyFromPart) continue;


                    int[] baseArgs = new int[tokens.length - 3];
                    for (int i = 0; i < baseArgs.length; i++) baseArgs[i] = Integer.parseInt(tokens[i + 3]);

                    trait.applyToItem(assembly, itemTypeAndLevelMultiplier, baseArgs);
                }
            }
            value += MiscTags.getItemValue(part);
        }
        MiscTags.setItemValue(assembly, value);


        //Set current stack to new calculated one
        stack.setTagCompound(assembly.getTagCompound());


        //Set assembly rarity
        for (IPartSlot partSlot : partSlots)
        {
            CRarity partRarity = MiscTags.getItemRarity(partSlot.getPart());
            if (partRarity != null && partRarity.ordering > rarity.ordering) rarity = partRarity;
        }
        MiscTags.setItemRarity(stack, rarity);


        //Set assembly name
        String name = AssemblyTags.getState(stack) >= AssemblyTags.STATE_USABLE ? MiscTags.getAssemblyNameOverride(stack) : null;
        if (name == null) name = itemType.name;
        stack.setStackDisplayName(rarity.textColor + name);
        //TODO generate assembly affixes


        if (VERBOSE_DEBUG)
        {
            debug("");
            debug("Final part check...");
            for (IPartSlot partSlot : AssemblyTags.getPartSlots(stack))
            {
                debug("");
                debug("Type: " + partSlot.getSlotType());
                debug("Is required: " + partSlot.getRequired());
                debug("Contains valid part: " + partSlot.partIsValidForSlot(partSlot.getPart()));
                debug("Part: " + partSlot.getPart().getDisplayName());
            }
            debug("");
            debug("Removed parts...");
            for (ItemStack removed : result) debug(removed.getDisplayName());
        }


        //Return removed parts
        return result;
    }


    /**
     * Validates and recalculates the traits of an *empty part*
     * Returns false if the part itself should be deleted (eg. if its item type no longer exists)
     */
    protected static boolean recalcEmptyPartTraits(ItemStack stack)
    {
        if (!MCTools.hosting()) throw new IllegalStateException("This method should not be run without a server running!");


        //Validate itemtype/rarity
        CItemType itemType = CSettings.LOCAL_SETTINGS.itemTypes.get(MiscTags.getItemTypeName(stack));
        if (itemType == null) return false;

        CRarity rarity = MiscTags.getItemRarity(stack);
        if (rarity == null) return false;


        //Re-apply item type and return
        itemType.applyItemType(stack, MiscTags.getItemLevel(stack), rarity);
        return true;
    }


    protected static void debug(String message)
    {
        System.out.println(TextFormatting.LIGHT_PURPLE + message);
    }
}
