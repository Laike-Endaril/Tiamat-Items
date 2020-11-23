package com.fantasticsource.tiamatitems.trait.recalculable.element;

import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tools.component.CInt;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CRTraitElement_StackLimit extends CRecalculableTraitElement
{
    public int limit = 1;


    @Override
    public int requiredArgumentCount()
    {
        return 0;
    }


    @Override
    public String getDescriptionInternal(ArrayList<Integer> baseArgs, double[] multipliedArgs)
    {
        return "Set the item stack size limit to " + limit;
    }


    @Override
    public void applyToItemInternal(ItemStack stack, int[] baseArgs, double[] multipliedArgs)
    {
        MiscTags.setTiamatItemStackLimit(stack, limit);
    }


    @Override
    public CRTraitElement_StackLimit write(ByteBuf buf)
    {
        super.write(buf);

        buf.writeInt(limit);

        return this;
    }

    @Override
    public CRTraitElement_StackLimit read(ByteBuf buf)
    {
        super.read(buf);

        limit = buf.readInt();

        return this;
    }

    @Override
    public CRTraitElement_StackLimit save(OutputStream stream)
    {
        super.save(stream);

        new CInt().set(limit).save(stream);

        return this;
    }

    @Override
    public CRTraitElement_StackLimit load(InputStream stream)
    {
        super.load(stream);

        limit = new CInt().load(stream).value;

        return this;
    }
}
