package com.fantasticsource.tiamatitems.nbt;

import com.fantasticsource.tiamatitems.globalsettings.CRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static com.fantasticsource.tiamatitems.TiamatItems.DOMAIN;

public class MiscTags
{
    public static void setItemLevel(ItemStack stack, int level)
    {
        if (level == 0)
        {
            clearItemLevel(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setInteger("level", level);
    }

    public static int getItemLevel(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return 0;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return 0;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("level")) return 0;

        return compound.getInteger("level");
    }

    public static void clearItemLevel(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("level")) return;

        compound.removeTag("level");
        if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
    }


    public static void setItemLevelReq(ItemStack stack, int levelReq)
    {
        if (levelReq == 0)
        {
            clearItemLevelReq(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setInteger("levelReq", levelReq);
    }

    public static int getItemLevelReq(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return 0;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return 0;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("levelReq")) return 0;

        return compound.getInteger("levelReq");
    }

    public static void clearItemLevelReq(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("levelReq")) return;

        compound.removeTag("levelReq");
        if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
    }


    public static void setItemRarity(ItemStack stack, CRarity rarity)
    {
        if (rarity == null)
        {
            clearItemRarity(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setString("rarity", rarity.name);
    }

    public static CRarity getItemRarity(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return null;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return null;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("rarity")) return null;

        CRarity rarity = CRarity.rarities.get(compound.getString("rarity"));
        if (rarity == null) clearItemRarity(stack);

        return rarity;
    }

    public static void clearItemRarity(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("rarity")) return;

        compound.removeTag("rarity");
        if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
    }


    public static void setItemValue(ItemStack stack, int value)
    {
        if (value == 0)
        {
            clearItemValue(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setInteger("value", value);
    }

    public static int getItemValue(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return 0;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return 0;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("value")) return 0;

        return compound.getInteger("value");
    }

    public static void clearItemValue(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("value")) return;

        compound.removeTag("value");
        if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
    }


    public static void setItemSlotting(ItemStack stack, String slotting)
    {
        if (slotting == null || slotting.equals("") || slotting.equals("None"))
        {
            clearItemSlotting(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setString("slotting", slotting);
    }

    public static String getItemSlotting(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return "None";

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return "None";

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("slotting")) return "None";

        return compound.getString("slotting");
    }

    public static void clearItemSlotting(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("slotting")) return;

        compound.removeTag("slotting");
        if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
    }
}
