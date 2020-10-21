package com.fantasticsource.tiamatitems.nbt;

import com.fantasticsource.tiamatitems.settings.CRarity;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.tiamatitems.TiamatItems.DOMAIN;

public class MiscTags
{
    public static void setItemType(ItemStack stack, String typeName)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());

        compound = compound.getCompoundTag(DOMAIN);
        compound.setString("type", typeName);
    }

    public static String getItemTypeName(ItemStack stack)
    {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null || !compound.hasKey(DOMAIN)) return "";

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("type")) return "";

        return compound.getString("type");
    }


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
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
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
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
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

        CRarity rarity = CSettings.SETTINGS.rarities.get(compound.getString("rarity"));
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
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
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
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
    }


    public static void setItemValueMod(ItemStack stack, int value)
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

        compound.setInteger("valuemod", value);
    }

    public static int getItemValueMod(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return 0;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return 0;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("valuemod")) return 0;

        return compound.getInteger("valuemod");
    }

    public static void clearItemValueMod(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("valuemod")) return;

        compound.removeTag("valuemod");
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
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
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
    }

    public static boolean stackFitsSlot(ItemStack stack, String slotting)
    {
        String itemSlotting = getItemSlotting(stack);
        if (itemSlotting.equals("Any") || itemSlotting.equals(slotting)) return true;

        switch (slotting)
        {
            case "Head":
            case "Chest":
            case "Legs":
            case "Feet":
            case "Tiamat Shoulders":
            case "Tiamat Cape":
            case "Armor":
                return itemSlotting.equals("Armor");

            case "Mainhand":
            case "Offhand":
            case "Hand":
                return itemSlotting.equals("Hand") || itemSlotting.equals("Tiamat 2H");

            case "Baubles Amulet":
            case "Baubles Ring":
            case "Baubles Belt":
            case "Baubles Head":
            case "Baubles Body":
            case "Baubles Charm":
                return itemSlotting.equals("Baubles Trinket");

            case "Tiamat Skill":
                return itemSlotting.equals("Tiamat Active Skill");
        }

        return false;
    }

    public static boolean isTwoHanded(ItemStack stack)
    {
        return stack.getItem() == Items.BOW || getItemSlotting(stack).equals("Tiamat 2H");
    }


    public static void setItemGenVersion(ItemStack stack, long version)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setLong("version", version);
    }

    public static long getItemGenVersion(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return -1;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return -1;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("version")) return -1;

        return compound.getLong("version");
    }

    public static void clearItemGenVersion(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("version")) return;

        compound.removeTag("version");
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
    }


    public static void setTiamatItemStackLimit(ItemStack stack, int limit)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setInteger("limit", limit);
    }

    public static int getTiamatItemStackLimit(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return 64;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return 64;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("limit")) return 64;

        return compound.getInteger("limit");
    }


    public static void setItemDurability(ItemStack stack, int durability)
    {
        if (durability == 0)
        {
            clearItemDurability(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setInteger("durability", durability);
    }

    public static int getItemDurability(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return 0;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return 0;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("durability")) return 0;

        return compound.getInteger("durability");
    }

    public static void clearItemDurability(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("durability")) return;

        compound.removeTag("durability");
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
    }


    public static void setDyeOverrides(ItemStack stack, LinkedHashMap<Integer, Color> dyes)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("dyeOverrides")) compound.setTag("dyeOverrides", new NBTTagCompound());
        compound = compound.getCompoundTag("dyeOverrides");

        for (Map.Entry<Integer, Color> entry : dyes.entrySet())
        {
            Color color = entry.getValue();
            NBTTagCompound dye = new NBTTagCompound();
            dye.setByte("r", (byte) color.r());
            dye.setByte("g", (byte) color.g());
            dye.setByte("b", (byte) color.b());
            dye.setByte("t", (byte) color.a());

            compound.setTag("" + entry.getKey(), dye);
        }
    }

    public static LinkedHashMap<Integer, Color> getDyeOverrides(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return null;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return null;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("dyeOverrides")) return null;

        compound = compound.getCompoundTag("dyeOverrides");
        LinkedHashMap<Integer, Color> dyes = new LinkedHashMap<>();
        int channel;
        for (String key : compound.getKeySet())
        {
            try
            {
                channel = Integer.parseInt(key);
            }
            catch (NumberFormatException e)
            {
                continue;
            }

            NBTTagCompound dye = compound.getCompoundTag(key);
            dyes.put(channel, new Color(dye.getByte("r") & 0xff, dye.getByte("g") & 0xff, dye.getByte("b") & 0xff, dye.getByte("t") & 0xff));
        }

        return dyes;
    }

    public static void clearDyeOverrides(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("dyeOverrides")) return;

        compound.removeTag("dyeOverrides");
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
    }
}
