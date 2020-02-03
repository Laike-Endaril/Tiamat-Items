package com.fantasticsource.tiamatitems.globaleditor;

import com.fantasticsource.tiamatitems.TiamatItems;
import net.minecraft.item.ItemBlock;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class ItemGlobalEditor extends ItemBlock
{
    public ItemGlobalEditor()
    {
        super(TiamatItems.blockGlobalEditor);

        setUnlocalizedName(MODID + ":globaleditor");
        setRegistryName("globaleditor");
    }
}
