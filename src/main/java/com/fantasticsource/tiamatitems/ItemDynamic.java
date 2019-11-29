package com.fantasticsource.tiamatitems;

import net.minecraft.item.Item;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class ItemDynamic extends Item
{
    public ItemDynamic()
    {
        setCreativeTab(TiamatItems.creativeTab);

        setUnlocalizedName(MODID + ":dynamicitem");
        setRegistryName("dynamicitem");
    }
}
