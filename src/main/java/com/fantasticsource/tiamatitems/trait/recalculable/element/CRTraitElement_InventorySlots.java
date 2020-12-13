package com.fantasticsource.tiamatitems.trait.recalculable.element;

import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CInt;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.InputStream;
import java.io.OutputStream;

public class CRTraitElement_InventorySlots extends CRecalculableTraitElement
{
    protected static final String TIAMAT_INVENTORY_MODID = "tiamatinventory";

    public int minCount = 0, maxCount = 1;


    @Override
    public int requiredArgumentCount()
    {
        return 1;
    }


    @Override
    public String getDescription(ItemStack stack, int[] args, double itemTypeAndLevelMultiplier)
    {
        if (args == null)
        {
            return "Add " + Tools.max(0, minCount) + " to " + Tools.max(0, maxCount) + " inventory slots";
        }

        int count = getStandardCount(args, 0, minCount, maxCount, itemTypeAndLevelMultiplier);
        return "Add " + count + " inventory slots";
    }


    @Override
    public void applyToItem(ItemStack stack, int[] args, double itemTypeAndLevelMultiplier)
    {
        int count = getStandardCount(args, 0, minCount, maxCount, itemTypeAndLevelMultiplier);

        //From Tiamat Inventory ... SlotDataTags class
        if (count == 0)
        {
            if (!stack.hasTagCompound()) return;

            NBTTagCompound mainTag = stack.getTagCompound();
            if (!mainTag.hasKey(TIAMAT_INVENTORY_MODID)) return;

            NBTTagCompound compound = mainTag.getCompoundTag(TIAMAT_INVENTORY_MODID);
            if (!compound.hasKey("invSlotCount")) return;

            compound.removeTag("invSlotCount");
            if (compound.hasNoTags()) mainTag.removeTag(TIAMAT_INVENTORY_MODID);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(TIAMAT_INVENTORY_MODID)) compound.setTag(TIAMAT_INVENTORY_MODID, new NBTTagCompound());
        compound = compound.getCompoundTag(TIAMAT_INVENTORY_MODID);

        compound.setInteger("invSlotCount", count);
    }


    @Override
    public CRTraitElement_InventorySlots write(ByteBuf buf)
    {
        super.write(buf);

        buf.writeInt(minCount);
        buf.writeInt(maxCount);

        return this;
    }

    @Override
    public CRTraitElement_InventorySlots read(ByteBuf buf)
    {
        super.read(buf);

        minCount = buf.readInt();
        maxCount = buf.readInt();

        return this;
    }

    @Override
    public CRTraitElement_InventorySlots save(OutputStream stream)
    {
        super.save(stream);

        new CInt().set(minCount).save(stream).set(maxCount).save(stream);

        return this;
    }

    @Override
    public CRTraitElement_InventorySlots load(InputStream stream)
    {
        super.load(stream);

        CInt ci = new CInt();

        minCount = ci.load(stream).value;
        maxCount = ci.load(stream).value;

        return this;
    }
}
