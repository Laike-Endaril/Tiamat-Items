package com.fantasticsource.tiamatitems.nbt;

import com.fantasticsource.tools.Tools;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

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


    public static NBTTagCompound getPartSlotTag(ItemStack stack, String partSlot)
    {
        return getPartSlotTag(stack, partSlot, false);
    }

    public static NBTTagCompound getPartSlotTag(ItemStack stack, String partSlot, boolean generateIfMissing)
    {
        NBTTagCompound compound = stack.getTagCompound();
        if (!generateIfMissing)
        {
            if (!compound.hasKey(DOMAIN)) return null;

            compound = compound.getCompoundTag(DOMAIN);
            if (!compound.hasKey("parts")) return null;

            compound = compound.getCompoundTag("parts");
            if (!compound.hasKey(partSlot)) return null;
        }
        else
        {
            if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());

            compound = compound.getCompoundTag(DOMAIN);
            if (!compound.hasKey("parts")) compound.setTag("parts", new NBTTagCompound());

            compound = compound.getCompoundTag("parts");
            if (!compound.hasKey(partSlot)) compound.setTag(partSlot, new NBTTagCompound());
        }

        return compound.getCompoundTag(partSlot);
    }


    public static NBTTagCompound getPartTag(ItemStack stack, String partSlot)
    {
        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return null;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("parts")) return null;

        compound = compound.getCompoundTag("parts");
        if (!compound.hasKey(partSlot)) return null;

        compound = compound.getCompoundTag(partSlot);
        if (!compound.hasKey("part")) return null;

        return compound.getCompoundTag("part");
    }

    public static void setPartTag(ItemStack stack, String partSlot, NBTTagCompound partTag)
    {
        if (!itemHasPartSlot(stack, partSlot)) return;

        NBTTagCompound compound = stack.getTagCompound().getCompoundTag(DOMAIN).getCompoundTag("parts").getCompoundTag(partSlot);
        compound.setTag("part", partTag);
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
        return part != null && !part.isEmpty();
    }


    public static boolean isPartRequired(ItemStack stack, String partSlot)
    {
        NBTTagCompound compound = getPartSlotTag(stack, partSlot);
        return compound != null && compound.getBoolean("required");
    }

    public static void setPartRequired(ItemStack stack, String partSlot, boolean required)
    {
        NBTTagCompound compound = getPartSlotTag(stack, partSlot);
        if (compound != null)
        {
            if (required) compound.setBoolean("required", true);
            else compound.removeTag("required");
        }
    }


    public static String getItemType(ItemStack stack)
    {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null || !compound.hasKey(DOMAIN)) return "Any";

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("type")) return "Any";

        return compound.getString("type");
    }

    public static void setItemType(ItemStack stack, String type)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());

        compound = compound.getCompoundTag(DOMAIN);
        compound.setString("type", type);
    }


    public static ArrayList<String> getValidPartTypesForSlot(ItemStack stack, String partSlot)
    {
        ArrayList<String> result = new ArrayList<>();

        NBTTagCompound compound = getPartSlotTag(stack, partSlot);
        if (compound == null || !compound.hasKey("types")) return result;

        NBTTagList list = compound.getTagList("types", Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++)
        {
            result.add(list.getStringTagAt(i));
        }
        return result;
    }

    public static void setValidPartTypesForSlot(ItemStack stack, String partSlot, String... validPartTypes)
    {
        NBTTagCompound compound = getPartSlotTag(stack, partSlot, true);
        compound.setTag("types", new NBTTagList());

        NBTTagList list = compound.getTagList("types", Constants.NBT.TAG_STRING);
        for (String type : validPartTypes) list.appendTag(new NBTTagString(type));
    }

    public static void clearValidPartTypesForSlot(ItemStack stack, String partSlot)
    {
        setValidPartTypesForSlot(stack, partSlot);
    }

    public static void addValidPartTypesForSlot(ItemStack stack, String partSlot, String... validPartTypes)
    {
        NBTTagCompound compound = getPartSlotTag(stack, partSlot, true);
        if (!compound.hasKey("types")) compound.setTag("types", new NBTTagList());

        NBTTagList list = compound.getTagList("types", Constants.NBT.TAG_STRING);
        for (String type : validPartTypes) list.appendTag(new NBTTagString(type));
    }

    public static boolean partIsValidForSlot(ItemStack stack, String partSlot, ItemStack part)
    {
        return getValidPartTypesForSlot(stack, partSlot).contains(getItemType(part));
    }


    public static boolean orderedCombinationIsValid(ItemStack stack, ItemStack... parts)
    {
        ArrayList<String> partSlots = getPartSlots(stack);
        if (partSlots.size() != parts.length) return false;

        for (int i = 0; i < partSlots.size(); i++)
        {
            String partSlot = partSlots.get(i);
            ItemStack part = parts[i];
            if (itemHasPartInSlot(stack, partSlot))
            {
                if (part != null && !part.isEmpty()) return false;
            }
            else
            {
                if (isPartRequired(stack, partSlot))
                {
                    if (part == null || part.isEmpty() || !getValidPartTypesForSlot(stack, partSlot).contains(getItemType(part))) return false;
                }
                else
                {
                    if (part != null && !part.isEmpty() && !getValidPartTypesForSlot(stack, partSlot).contains(getItemType(part))) return false;
                }
            }
        }
        return true;
    }

    public static boolean combineOrdered(ItemStack stack, ItemStack... parts)
    {
        return combineOrdered(stack, true, parts);
    }

    public static boolean combineOrdered(ItemStack stack, boolean absorbParts, ItemStack... parts)
    {
        if (!orderedCombinationIsValid(stack, parts)) return false;

        int value = MiscTags.getItemValue(stack);
        int level = MiscTags.getItemLevel(stack);
        int levelReq = MiscTags.getItemLevelReq(stack);
        //TODO combine rarity (max)
        //TODO combine name / affixes (first of each found)
        //TODO combine actions (first of each found)
        //TODO combine passive attribute modifiers (complex)
        //TODO combine active attribute modifiers (complex)
        //TODO handle item graphic so blueprints don't have to look like finished items (alters base item?)

        ArrayList<String> partSlots = getPartSlots(stack);
        for (int i = 0; i < partSlots.size(); i++)
        {
            String partSlot = partSlots.get(i);
            ItemStack part = parts[i];
            if (itemHasPartInSlot(stack, partSlot)) continue;

            setPart(stack, partSlot, part);

            value += MiscTags.getItemValue(part);
            level = Tools.max(level, MiscTags.getItemLevel(part));
            levelReq = Tools.max(levelReq, MiscTags.getItemLevelReq(part));

            if (absorbParts) part.setCount(0);
        }

        //TODO preserve individual data for original blueprint before combining

        MiscTags.setItemValue(stack, value);
        MiscTags.setItemLevel(stack, level);
        MiscTags.setItemLevelReq(stack, levelReq);

        return true;
    }
}
