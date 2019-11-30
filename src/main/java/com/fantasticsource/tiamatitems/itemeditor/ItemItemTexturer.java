package com.fantasticsource.tiamatitems.itemeditor;

import com.fantasticsource.tiamatitems.TiamatItems;
import net.minecraft.item.ItemBlock;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class ItemItemTexturer extends ItemBlock
{
    public ItemItemTexturer()
    {
        super(TiamatItems.blockItemTexturer);

        setUnlocalizedName(MODID + ":itemtexturer");
        setRegistryName("itemtexturer");
    }
}
