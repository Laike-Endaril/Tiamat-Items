package com.fantasticsource.tiamatitems.apinatives;

import com.fantasticsource.tiamatitems.api.IPartSlot;
import com.fantasticsource.tiamatitems.api.ITiamatItemsNatives;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class TiamatItemsNatives implements ITiamatItemsNatives
{
    public static TiamatItemsNatives NATIVES = new TiamatItemsNatives();

    @Override
    public ArrayList<IPartSlot> getPartSlots(ItemStack stack)
    {
        return AssemblyTags.getPartSlots(stack);
    }
}
