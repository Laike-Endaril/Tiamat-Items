package com.fantasticsource.tiamatitems.trait.recalculable.element;

import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CRTraitElement_PartSlot extends CRecalculableTraitElement
{
    public String partSlotType = "";
    public int minCount = 0, maxCount = 1;
    public boolean required = false;


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
            return "Add " + Tools.max(0, minCount) + " to " + Tools.max(0, maxCount) + (required ? " required " : " optional ") + (partSlotType.equals("") ? "(undefined type)" : partSlotType) + " slots";
        }

        int count = minCount + (int) ((double) baseArgs.get(0) / Integer.MAX_VALUE * (maxCount - minCount + 1));
        return "Add " + count + (required ? " required " : " optional ") + partSlotType + " slots";
    }


    @Override
    public void applyToItem(ItemStack stack, int[] baseArgs, double[] multipliedArgs)
    {
        int count = minCount + (int) ((double) baseArgs[0] / Integer.MAX_VALUE * (maxCount - minCount + 1));
        for (; count > 0; count--) AssemblyTags.addPartSlot(stack, partSlotType, required);
    }


    @Override
    public CRTraitElement_PartSlot write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, partSlotType);
        buf.writeBoolean(required);

        return this;
    }

    @Override
    public CRTraitElement_PartSlot read(ByteBuf buf)
    {
        partSlotType = ByteBufUtils.readUTF8String(buf);
        required = buf.readBoolean();

        return this;
    }

    @Override
    public CRTraitElement_PartSlot save(OutputStream stream)
    {
        new CStringUTF8().set(partSlotType).save(stream);
        new CBoolean().set(required).save(stream);

        return this;
    }

    @Override
    public CRTraitElement_PartSlot load(InputStream stream)
    {
        partSlotType = new CStringUTF8().load(stream).value;
        required = new CBoolean().load(stream).value;

        return this;
    }
}
