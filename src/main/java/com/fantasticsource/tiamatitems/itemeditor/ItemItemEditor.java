package com.fantasticsource.tiamatitems.itemeditor;

import com.fantasticsource.tiamatitems.TiamatItems;
import net.minecraft.item.ItemBlock;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class ItemItemEditor extends ItemBlock
{
    public ItemItemEditor()
    {
        super(TiamatItems.blockItemEditor);

        setUnlocalizedName(MODID + ":itemeditor");
        setRegistryName("itemeditor");
    }
}
