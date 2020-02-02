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
        if (compound.getSize() == 0) clearPartSlots(stack);
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


    public static void setAssembly(ItemStack stack, ItemStack assembly)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());

        compound = compound.getCompoundTag(DOMAIN);
        compound.setTag("assembly", assembly.serializeNBT());
    }

    public static ItemStack getAssembly(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return null;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return null;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("assembly")) return null;

        return new ItemStack(compound.getCompoundTag("assembly"));
    }

    public static void clearAssembly(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("assembly")) return;

        compound.removeTag("assembly");
        if (compound.getSize() == 0) mainTag.removeTag(DOMAIN);
    }

    public static boolean itemIsBlueprint(ItemStack stack)
    {
        ItemStack assembly = getAssembly(stack);
        return assembly != null && !assembly.isEmpty();
    }


    public static void setBlueprint(ItemStack stack, ItemStack blueprint)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());

        compound = compound.getCompoundTag(DOMAIN);
        compound.setTag("blueprint", blueprint.serializeNBT());
    }

    public static ItemStack getBlueprint(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return null;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return null;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("blueprint")) return null;

        return new ItemStack(compound.getCompoundTag("blueprint"));
    }

    public static void clearBlueprint(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("blueprint")) return;

        compound.removeTag("blueprint");
        if (compound.getSize() == 0) mainTag.removeTag(DOMAIN);
    }

    public static boolean itemIsAssembly(ItemStack stack)
    {
        ItemStack blueprint = getBlueprint(stack);
        return blueprint != null && !blueprint.isEmpty();
    }


    public static boolean simpleOrderedCombinationIsValid(ItemStack blueprint, ItemStack... parts)
    {
        if (!itemIsBlueprint(blueprint)) return false;

        ArrayList<String> partSlots = getPartSlots(blueprint);
        if (partSlots.size() != parts.length) return false;

        for (int i = 0; i < partSlots.size(); i++)
        {
            String partSlot = partSlots.get(i);
            ItemStack part = parts[i];

            if (itemHasPartInSlot(blueprint, partSlot)) return false;

            if (isPartRequired(blueprint, partSlot))
            {
                if (part == null || part.isEmpty() || !getValidPartTypesForSlot(blueprint, partSlot).contains(getItemType(part))) return false;
            }
            else
            {
                if (part != null && !part.isEmpty() && !getValidPartTypesForSlot(blueprint, partSlot).contains(getItemType(part))) return false;
            }
        }
        return true;
    }

    public static ItemStack combineSimpleOrdered(ItemStack blueprint, ItemStack... parts)
    {
        if (!simpleOrderedCombinationIsValid(blueprint, parts)) return ItemStack.EMPTY;


        int value = MiscTags.getItemValue(blueprint);
        int level = MiscTags.getItemLevel(blueprint);
        int levelReq = MiscTags.getItemLevelReq(blueprint);
        //TODO combine rarity (max)
        //TODO combine name / affixes (rarity priority)
        //TODO combine actions (rarity priority)


        ArrayList<String> partSlots = getPartSlots(blueprint);
        for (int i = 0; i < partSlots.size(); i++)
        {
            ItemStack part = parts[i];

            setPart(blueprint, partSlots.get(i), part);

            value += MiscTags.getItemValue(part);
            level = Tools.max(level, MiscTags.getItemLevel(part));
            levelReq = Tools.max(levelReq, MiscTags.getItemLevelReq(part));

            part.setCount(0);
        }


        //Preserve individual data for original blueprint before combining
        ItemStack result = getAssembly(blueprint);
        setBlueprint(result, blueprint);


        //Set assembled item parameters
        MiscTags.setItemValue(result, value);
        MiscTags.setItemLevel(result, level);
        MiscTags.setItemLevelReq(result, levelReq);


        return result;
    }
}
