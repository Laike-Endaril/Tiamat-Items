package com.fantasticsource.tiamatitems.apinatives;

import com.fantasticsource.tiamatitems.api.IPartSlot;
import com.fantasticsource.tiamatitems.api.ITiamatItemsNatives;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
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

    @Override
    public boolean isUsable(ItemStack stack)
    {
        if (AssemblyTags.getState(stack) < AssemblyTags.STATE_USABLE) return false;
        if (MiscTags.hasItemDurability(stack) && MiscTags.getItemDamage(stack) >= MiscTags.getItemDurability(stack)) return false;
        return true;
    }
}
