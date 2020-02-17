package com.fantasticsource.tiamatitems.traitgen;

import com.fantasticsource.tiamatitems.globalsettings.CGlobalSettings;
import com.fantasticsource.tiamatitems.nbt.PassiveAttributeModTags;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;

public class CTraitElement_PassiveAttributeMod extends CTraitElement
{
    public String attributeName = "";
    public boolean isGood = true; //Whether it's a good thing to have more of this attribute
    public int operation = 0;

    protected String getSignAndColor(double amount, int operation)
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
    public String getDescription()
    {
        if (operation == 0) return (getSignAndColor(minimum, operation) + Math.abs(minimum) + TextFormatting.RESET + " to " + getSignAndColor(maximum, operation) + Math.abs(maximum) + TextFormatting.RESET + " " + I18n.translateToLocal("attribute.name." + attributeName)).replaceAll("[.]0([^0-9])", "$1");
        if (operation == 1) return (getSignAndColor(minimum, operation) + (Math.abs(minimum) * 100) + "%" + TextFormatting.RESET + " to " + getSignAndColor(maximum, operation) + (Math.abs(maximum) * 100) + "%" + TextFormatting.RESET + " " + I18n.translateToLocal("attribute.name." + attributeName)).replaceAll("[.]0([^0-9])", "$1");
        if (operation == 2) return (getSignAndColor(minimum, operation) + Math.abs(minimum) + "x" + TextFormatting.RESET + " to " + getSignAndColor(maximum, operation) + Math.abs(maximum) + "x" + TextFormatting.RESET + " " + I18n.translateToLocal("attribute.name." + attributeName)).replaceAll("[.]0([^0-9])", "$1");

        throw new IllegalArgumentException("Operation must be 0, 1, or 2, but is " + operation);
    }

    @Override
    public String getDescription(double percentage)
    {
        if (operation == 0) return (getSignAndColor(percentage, operation) + Math.abs(percentage) + TextFormatting.RESET + " " + I18n.translateToLocal("attribute.name." + attributeName)).replaceAll("[.]0([^0-9])", "$1");
        if (operation == 1) return (getSignAndColor(percentage, operation) + (Math.abs(percentage) * 100) + "%" + TextFormatting.RESET + " " + I18n.translateToLocal("attribute.name." + attributeName)).replaceAll("[.]0([^0-9])", "$1");
        if (operation == 2) return (getSignAndColor(percentage, operation) + Math.abs(percentage) + "x" + TextFormatting.RESET + " " + I18n.translateToLocal("attribute.name." + attributeName)).replaceAll("[.]0([^0-9])", "$1");

        throw new IllegalArgumentException("Operation must be 0, 1, or 2, but is " + operation);
    }

    @Override
    public void applyToItem(ItemStack stack, int wholeNumberPercentage)
    {
        double amount = minimum + (maximum - minimum) * wholeNumberPercentage / 100;
        if (amount == 0) return;

        amount *= CGlobalSettings.attributeBalanceMultipliers.getOrDefault(attributeName, 1d);
        if (amount == 0) return;

        if (operation == 2) amount -= 1; //For internal calcs (above) and editing (minimum, maximum), treat operation 2 as a direct multiplier (2 means 2x as opposed to 3x)
        //TODO when editing, instead of giving direct access to operations, give these options: "Adjust Amount (+/-x)", "Adjust Percentage (+/-%)", "Mutliply"
        PassiveAttributeModTags.addPassiveMod(stack, attributeName + ";" + amount + ";" + operation);
    }


    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof CTraitElement_PassiveAttributeMod)) return false;

        CTraitElement_PassiveAttributeMod other = (CTraitElement_PassiveAttributeMod) obj;
        return other.operation == operation && other.attributeName.equals(attributeName);
    }


    @Override
    public CTraitElement_PassiveAttributeMod write(ByteBuf buf)
    {
        super.write(buf);

        ByteBufUtils.writeUTF8String(buf, attributeName);
        buf.writeBoolean(isGood);
        buf.writeInt(operation);

        return this;
    }

    @Override
    public CTraitElement_PassiveAttributeMod read(ByteBuf buf)
    {
        super.read(buf);

        attributeName = ByteBufUtils.readUTF8String(buf);
        isGood = buf.readBoolean();
        operation = buf.readInt();

        return this;
    }

    @Override
    public CTraitElement_PassiveAttributeMod save(OutputStream stream)
    {
        super.save(stream);

        new CStringUTF8().set(attributeName).save(stream);
        new CBoolean().set(isGood).save(stream);
        new CInt().set(operation).save(stream);

        return this;
    }

    @Override
    public CTraitElement_PassiveAttributeMod load(InputStream stream)
    {
        super.load(stream);

        attributeName = new CStringUTF8().load(stream).value;
        isGood = new CBoolean().load(stream).value;
        operation = new CInt().load(stream).value;

        return this;
    }
}
