package com.fantasticsource.tiamatitems.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;

import static com.fantasticsource.tiamatitems.TiamatItems.DOMAIN;

public class CategoryTags
{
    public static boolean itemHasCategoryTag(ItemStack stack, String category, String tag)
    {
        if (!stack.hasTagCompound()) return false;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return false;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("categories")) return false;

        compound = compound.getCompoundTag("categories");
        if (!compound.hasKey(category)) return false;

        NBTTagList list = compound.getTagList(category, Constants.NBT.TAG_STRING);
        for (int i = list.tagCount() - 1; i >= 0; i--) if (list.getStringTagAt(i).equals(tag)) return true;
        return false;
    }

    public static ArrayList<String> getItemCategoryTags(ItemStack stack, String category)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!stack.hasTagCompound()) return result;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return result;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("categories")) return result;

        compound = compound.getCompoundTag("categories");
        if (!compound.hasKey(category)) return result;

        NBTTagList list = compound.getTagList(category, Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++) result.add(list.getStringTagAt(i));

        return result;
    }

    public static ArrayList<String> getItemCategories(ItemStack stack)
    {
        ArrayList<String> result = new ArrayList<>();

        if (!stack.hasTagCompound()) return result;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return result;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("categories")) return result;

        compound = compound.getCompoundTag("categories");
        result.addAll(compound.getKeySet());

        return result;
    }

    public static void addItemCategoryTag(ItemStack stack, String category, String tag)
    {
        if (itemHasCategoryTag(stack, category, tag)) return;

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("categories")) compound.setTag("categories", new NBTTagCompound());
        compound = compound.getCompoundTag("categories");

        if (!compound.hasKey(category)) compound.setTag(category, new NBTTagList());
        NBTTagList list = compound.getTagList(category, Constants.NBT.TAG_STRING);

        list.appendTag(new NBTTagString(tag));
    }


    public static void removeItemCategoryTag(ItemStack stack, String category, String tag)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("categories")) return;

        compound = compound.getCompoundTag("categories");
        if (!compound.hasKey(category)) return;

        NBTTagList list = compound.getTagList(category, Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++)
        {
            if (list.getStringTagAt(i).equals(tag))
            {
                list.removeTag(i);
                if (list.tagCount() == 0) removeItemCategory(stack, category);
                break;
            }
        }
    }

    public static void clearItemCategories(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("categories")) return;

        compound.removeTag("categories");
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
    }

    public static void removeItemCategory(ItemStack stack, String category)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("categories")) return;

        NBTTagCompound categories = compound.getCompoundTag("categories");
        if (!categories.hasKey(category)) return;

        categories.removeTag(category);

        if (categories.getKeySet().size() == 0)
        {
            compound.removeTag("categories");
            if (compound.hasNoTags())
            {
                mainTag.removeTag(DOMAIN);
                if (mainTag.hasNoTags()) stack.setTagCompound(null);
            }
        }
    }

    public static void renameItemCategory(ItemStack stack, String oldCategory, String newCategory)
    {
        if (oldCategory.equals(newCategory)) return;

        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("categories")) return;

        NBTTagCompound categories = compound.getCompoundTag("categories");
        if (!categories.hasKey(oldCategory)) return;

        categories.setTag(newCategory, categories.getTag(oldCategory));
        categories.removeTag(oldCategory);
    }
}
