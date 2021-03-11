package com.fantasticsource.tiamatitems.assembly;

import com.fantasticsource.tiamatitems.EffectiveData;
import com.fantasticsource.tiamatitems.api.IPartSlot;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class PartSlot implements IPartSlot
{
    protected String slotType;
    protected boolean required;
    protected ItemStack part;


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
        return EffectiveData.slotTypes.getOrDefault(slotType, new LinkedHashSet<>());
    }

    public boolean partIsValidForSlot(ItemStack part)
    {
        return getValidItemTypes().contains(MiscTags.getItemTypeName(part));
    }


    @Override
    public String getSlotType()
    {
        return slotType;
    }

    @Override
    public void setSlotType(String slotType)
    {
        this.slotType = slotType;
    }

    @Override
    public boolean getRequired()
    {
        return required;
    }

    @Override
    public void setRequired(boolean required)
    {
        this.required = required;
    }

    @Override
    public ItemStack getPart()
    {
        return part;
    }

    @Override
    public void setPart(ItemStack part)
    {
        this.part = part;
    }
}
