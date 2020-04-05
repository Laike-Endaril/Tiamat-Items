package com.fantasticsource.tiamatitems.settings;

import com.fantasticsource.tiamatitems.TiamatItems;
import net.minecraft.item.ItemBlock;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class ItemSettings extends ItemBlock
{
    public ItemSettings()
    {
        super(TiamatItems.blockSettings);

        setUnlocalizedName(MODID + ":settings");
        setRegistryName("settings");
    }
}
