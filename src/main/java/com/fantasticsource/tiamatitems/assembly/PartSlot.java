package com.fantasticsource.tiamatitems.assembly;

import com.fantasticsource.tiamatitems.nbt.MiscTags;
import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.LinkedHashMap;

public class PartSlot
{
    public static LinkedHashMap<String, HashSet<String>> validItemTypes = new LinkedHashMap<>();


    public String slotType;
    public boolean required;
    public ItemStack part;


    public PartSlot(String slotType)
    {
        this(slotType, ItemStack.EMPTY);
    }

    public PartSlot(String slotType, boolean required)
    {
        this(slotType, required, ItemStack.EMPTY);
    }

    public PartSlot(String slotType, ItemStack part)
    {
        this(slotType, false, part);
    }

    public PartSlot(String slotType, boolean required, ItemStack part)
    {
        this.slotType = slotType;
        this.required = required;
        this.part = part;
    }


    public HashSet<String> getValidItemTypes()
    {
        return validItemTypes.getOrDefault(slotType, new HashSet<>());
    }

    public boolean partIsValidForSlot(ItemStack part)
    {
        System.out.println(MiscTags.getItemType(part));
        for (String itemType : getValidItemTypes()) System.out.println(itemType);
        return getValidItemTypes().contains(MiscTags.getItemType(part));
    }
}
