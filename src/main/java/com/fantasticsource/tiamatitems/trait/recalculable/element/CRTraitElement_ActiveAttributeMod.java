package com.fantasticsource.tiamatitems.trait.recalculable.element;

import com.fantasticsource.tiamatitems.nbt.ActiveAttributeModTags;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CRTraitElement_ActiveAttributeMod extends CRecalculableTraitElement
{
    public String attributeName = "";
    public double minAmount = 0, maxAmount = 0;
    public boolean isGood = true; //Whether it's a good thing to have more of this attribute
    public int operation = 0;


    protected String getColorAndSign(double amount, int operation)
    {
        String sign;
        TextFormatting color;

        if (operation == 2)
        {
            sign = amount < 0 ? "-" : "";
            color = (amount < 1) == isGood ? TextFormatting.RED : TextFormatting.GREEN;
        }
        else
        {
            sign = amount < 0 ? "-" : "+";
            color = (amount < 0) == isGood ? TextFormatting.RED : TextFormatting.GREEN;
        }

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
            if (operation == 0) return (getColorAndSign(minAmount, operation) + Math.abs(minAmount) + TextFormatting.RESET + " to " + getColorAndSign(maxAmount, operation) + Math.abs(maxAmount) + TextFormatting.RESET + " " + I18n.translateToLocal("attribute.name." + attributeName)).replaceAll("[.]0([^0-9])", "$1");
            if (operation == 1) return (getColorAndSign(minAmount, operation) + (Math.abs(minAmount) * 100) + "%" + TextFormatting.RESET + " to " + getColorAndSign(maxAmount, operation) + (Math.abs(maxAmount) * 100) + "%" + TextFormatting.RESET + " " + I18n.translateToLocal("attribute.name." + attributeName)).replaceAll("[.]0([^0-9])", "$1");
            if (operation == 2) return (getColorAndSign(minAmount, operation) + Math.abs(minAmount) + "x" + TextFormatting.RESET + " to " + getColorAndSign(maxAmount, operation) + Math.abs(maxAmount) + "x" + TextFormatting.RESET + " " + I18n.translateToLocal("attribute.name." + attributeName)).replaceAll("[.]0([^0-9])", "$1");

            throw new IllegalArgumentException("Operation must be 0, 1, or 2, but is " + operation);
        }


        double amount = minAmount + (maxAmount - minAmount) * multipliedArgs[0];
        amount *= CSettings.attributeBalanceMultipliers.getOrDefault(attributeName, 1d);

        if (operation == 0) return (getColorAndSign(amount, operation) + Math.abs(amount) + " " + I18n.translateToLocal("attribute.name." + attributeName)).replaceAll("[.]0([^0-9])", "$1");
        if (operation == 1) return (getColorAndSign(amount, operation) + (Math.abs(amount) * 100) + "%" + " " + I18n.translateToLocal("attribute.name." + attributeName)).replaceAll("[.]0([^0-9])", "$1");
        if (operation == 2) return (getColorAndSign(amount, operation) + Math.abs(amount) + "x" + " " + I18n.translateToLocal("attribute.name." + attributeName)).replaceAll("[.]0([^0-9])", "$1");

        throw new IllegalArgumentException("Operation must be 0, 1, or 2, but is " + operation);
    }


    @Override
    public void applyToItem(ItemStack stack, int[] baseArgs, double[] multipliedArgs)
    {
        double amount = minAmount + (maxAmount - minAmount) * multipliedArgs[0];
        if (amount == 0) return;

        amount *= CSettings.attributeBalanceMultipliers.getOrDefault(attributeName, 1d);
        if (amount == 0) return;

        if (operation == 2) amount -= 1; //For internal calcs (above) and editing (minimum, maximum), treat operation 2 as a direct multiplier (2 means 2x as opposed to 3x)
        //TODO when editing, instead of giving direct access to operations, give these options: "Adjust Amount (+/-x)", "Adjust Percentage (+/-%)", "Mutliply"
        ActiveAttributeModTags.addActiveMod(stack, attributeName + ";" + amount + ";" + operation);
    }


    @Override
    public CRTraitElement_ActiveAttributeMod write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, attributeName);
        buf.writeDouble(minAmount);
        buf.writeDouble(maxAmount);
        buf.writeBoolean(isGood);
        buf.writeInt(operation);

        return this;
    }

    @Override
    public CRTraitElement_ActiveAttributeMod read(ByteBuf buf)
    {
        attributeName = ByteBufUtils.readUTF8String(buf);
        minAmount = buf.readDouble();
        maxAmount = buf.readDouble();
        isGood = buf.readBoolean();
        operation = buf.readInt();

        return this;
    }

    @Override
    public CRTraitElement_ActiveAttributeMod save(OutputStream stream)
    {
        new CStringUTF8().set(attributeName).save(stream);
        new CDouble().set(minAmount).save(stream).set(maxAmount).save(stream);
        new CBoolean().set(isGood).save(stream);
        new CInt().set(operation).save(stream);

        return this;
    }

    @Override
    public CRTraitElement_ActiveAttributeMod load(InputStream stream)
    {
        CDouble cd = new CDouble();
        attributeName = new CStringUTF8().load(stream).value;
        minAmount = cd.load(stream).value;
        maxAmount = cd.load(stream).value;
        isGood = new CBoolean().load(stream).value;
        operation = new CInt().load(stream).value;

        return this;
    }
}
