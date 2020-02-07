package com.fantasticsource.tiamatitems.globalsettings;

import com.fantasticsource.tiamatitems.TiamatItems;
import net.minecraft.item.ItemBlock;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class ItemGlobalSettings extends ItemBlock
{
    public ItemGlobalSettings()
    {
        super(TiamatItems.blockGlobalSettings);

        setUnlocalizedName(MODID + ":globalsettings");
        setRegistryName("globalsettings");
    }
}
