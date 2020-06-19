package com.fantasticsource.tiamatitems.trait.recalculable.element;

import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CInt;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CRTraitElement_InventorySlots extends CRecalculableTraitElement
{
    protected static final String TRPG_MODID = "tiamatrpg";

    public int minCount = 0, maxCount = 1;


    @Override
    public int requiredArgumentCount()
    {
        return 1;
    }


    @Override
    public String getDescription(ArrayList<Integer> baseArgs, double[] multipliedArgs)
    {
        if (baseArgs.size() == 0)
        {
            return "Add " + Tools.max(0, minCount) + " to " + Tools.max(0, maxCount) + " inventory slots";
        }

        int count = minCount + (int) ((double) baseArgs.get(0) / Integer.MAX_VALUE * (maxCount - minCount + 1));
        return "Add " + count + " inventory slots";
    }


    @Override
    public void applyToItem(ItemStack stack, int[] baseArgs, double[] multipliedArgs)
    {
        int count = minCount + (int) ((double) baseArgs[0] / Integer.MAX_VALUE * (maxCount - minCount + 1));


        //From Tiamat RPG ... SlotDataTags class
        if (count == 0)
        {
            if (!stack.hasTagCompound()) return;

            NBTTagCompound mainTag = stack.getTagCompound();
            if (!mainTag.hasKey(TRPG_MODID)) return;

            NBTTagCompound compound = mainTag.getCompoundTag(TRPG_MODID);
            if (!compound.hasKey("invSlotCount")) return;

            compound.removeTag("invSlotCount");
            if (compound.hasNoTags()) mainTag.removeTag(TRPG_MODID);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(TRPG_MODID)) compound.setTag(TRPG_MODID, new NBTTagCompound());
        compound = compound.getCompoundTag(TRPG_MODID);

        compound.setInteger("invSlotCount", count);
    }


    @Override
    public CRTraitElement_InventorySlots write(ByteBuf buf)
    {
        buf.writeInt(minCount);
        buf.writeInt(maxCount);

        return this;
    }

    @Override
    public CRTraitElement_InventorySlots read(ByteBuf buf)
    {
        minCount = buf.readInt();
        maxCount = buf.readInt();

        return this;
    }

    @Override
    public CRTraitElement_InventorySlots save(OutputStream stream)
    {
        new CInt().set(minCount).save(stream).set(maxCount).save(stream);

        return this;
    }

    @Override
    public CRTraitElement_InventorySlots load(InputStream stream)
    {
        CInt ci = new CInt();

        minCount = ci.load(stream).value;
        maxCount = ci.load(stream).value;

        return this;
    }
}
