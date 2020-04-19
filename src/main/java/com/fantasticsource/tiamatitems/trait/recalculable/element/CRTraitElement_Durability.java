package com.fantasticsource.tiamatitems.trait.recalculable.element;

import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tools.component.CDouble;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CRTraitElement_Durability extends CRecalculableTraitElement
{
    public double minAmount = 0, maxAmount = 0;


    protected String getColorAndSign(double amount)
    {
        String sign;
        TextFormatting color;

        sign = amount < 0 ? "-" : "+";
        color = (amount < 0) ? TextFormatting.RED : TextFormatting.GREEN;

        return color + sign;
    }


    @Override
    public int requiredArgumentCount()
    {
        return 1;
    }


    @Override
    public String getDescription(ArrayList<Integer> baseArgs, double[] multipliedArgs)
    {
        if (multipliedArgs.length == 0)
        {
            return (getColorAndSign(minAmount) + Math.abs(minAmount) + TextFormatting.RESET + " to " + getColorAndSign(maxAmount) + Math.abs(maxAmount) + TextFormatting.RESET + " Durability");
        }


        int amount = (int) (minAmount + (maxAmount - minAmount) * multipliedArgs[0]);
        return (getColorAndSign(amount) + Math.abs(amount) + " Durability");
    }


    @Override
    public void applyToItem(ItemStack stack, int[] baseArgs, double[] multipliedArgs)
    {
        int amount = (int) (minAmount + (maxAmount - minAmount) * multipliedArgs[0]);
        if (amount == 0) return;

        MiscTags.setItemDurability(stack, amount);
    }


    @Override
    public CRTraitElement_Durability write(ByteBuf buf)
    {
        buf.writeDouble(minAmount);
        buf.writeDouble(maxAmount);

        return this;
    }

    @Override
    public CRTraitElement_Durability read(ByteBuf buf)
    {
        minAmount = buf.readDouble();
        maxAmount = buf.readDouble();

        return this;
    }

    @Override
    public CRTraitElement_Durability save(OutputStream stream)
    {
        new CDouble().set(minAmount).save(stream).set(maxAmount).save(stream);

        return this;
    }

    @Override
    public CRTraitElement_Durability load(InputStream stream)
    {
        CDouble cd = new CDouble();
        minAmount = cd.load(stream).value;
        maxAmount = cd.load(stream).value;

        return this;
    }
}
