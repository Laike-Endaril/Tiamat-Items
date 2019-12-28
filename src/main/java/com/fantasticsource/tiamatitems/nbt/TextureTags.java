package com.fantasticsource.tiamatitems.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;

import static com.fantasticsource.tiamatitems.TiamatItems.DOMAIN;

public class TextureTags
{
    public static ArrayList<String> getItemLayers(ItemStack stack)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!stack.hasTagCompound()) return result;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return result;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("layers")) return result;

        NBTTagList list = compound.getTagList("layers", Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++) result.add(list.getStringTagAt(i));

        return result;
    }

    public static void clearItemLayers(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("layers")) return;

        compound.removeTag("layers");
        if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
    }

    public static void removeItemLayer(ItemStack stack, String layer)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("layers")) return;

        NBTTagList list = compound.getTagList("layers", Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++)
        {
            if (list.getStringTagAt(i).equals(layer))
            {
                list.removeTag(i);
                if (list.tagCount() == 0)
                {
                    compound.removeTag("layers");
                    if (compound.hasNoTags()) mainTag.removeTag(DOMAIN);
                }
                break;
            }
        }
    }

    public static void addItemLayer(ItemStack stack, String layer)
    {
        if (itemHasLayer(stack, layer)) return;

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("layers")) compound.setTag("layers", new NBTTagList());
        NBTTagList list = compound.getTagList("layers", Constants.NBT.TAG_STRING);

        list.appendTag(new NBTTagString(layer));
    }

    public static boolean itemHasLayer(ItemStack stack, String layer)
    {
        if (!stack.hasTagCompound()) return false;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return false;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("layers")) return false;

        NBTTagList list = compound.getTagList("layers", Constants.NBT.TAG_STRING);
        for (int i = list.tagCount() - 1; i >= 0; i--) if (list.getStringTagAt(i).equals(layer)) return true;
        return false;
    }

    public static boolean itemHasMainLayerTag(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return false;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return false;

        compound = compound.getCompoundTag(DOMAIN);
        return compound.hasKey("layers");
    }


    public static void removeItemLayerCacheTag(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("cacheLayers")) return;

        compound.removeTag("cacheLayers");
    }

    public static void addItemLayerCacheTag(ItemStack stack)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("cacheLayers")) compound.setBoolean("cacheLayers", true);
    }

    public static boolean itemHasLayerCacheTag(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return false;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return false;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("cacheLayers")) return false;

        return compound.getBoolean("cacheLayers");
    }


    public static void removeItemTextureCacheTag(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("cacheTexture")) return;

        compound.removeTag("cacheTexture");
    }

    public static void addItemTextureCacheTag(ItemStack stack)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("cacheTexture")) compound.setBoolean("cacheTexture", true);
    }

    public static boolean itemHasTextureCacheTag(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return false;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return false;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("cacheTexture")) return false;

        return compound.getBoolean("cacheTexture");
    }
}
