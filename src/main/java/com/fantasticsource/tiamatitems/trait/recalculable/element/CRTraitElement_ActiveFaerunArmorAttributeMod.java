package com.fantasticsource.tiamatitems.trait.recalculable.element;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tools.component.CDouble;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import java.io.InputStream;
import java.io.OutputStream;

public class CRTraitElement_ActiveFaerunArmorAttributeMod extends CRecalculableTraitElement
{
    public BetterAttributeMod mod = new BetterAttributeMod();
    public double minAmount = 0, maxAmount = 0;


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
            mod.amount = minAmount;
            String result = mod.toString();
            mod.amount = maxAmount;
            return result.replaceAll("([^" + TextFormatting.GRAY + "]*)" + TextFormatting.GRAY + ".*", "$1 to " + mod.toString() + " when armor is hit");
        }


        mod.amount = getStandardAmount(args, 0, minAmount, maxAmount, itemTypeAndLevelMultiplier) * CSettings.attributeBalanceMultipliers.getOrDefault(mod.betterAttributeName, 1d);
        return mod.toString() + " when armor is hit";
    }


    @Override
    public void applyToItem(ItemStack stack, int[] args, double itemTypeAndLevelMultiplier)
    {
        mod.amount = getStandardAmount(args, 0, minAmount, maxAmount, itemTypeAndLevelMultiplier) * CSettings.attributeBalanceMultipliers.getOrDefault(mod.betterAttributeName, 1d);
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        MCTools.getOrGenerateSubCompound(stack.getTagCompound(), "faerunutils", "activeArmorMods").setTag(mod.name, mod.serializeNBT());
    }


    @Override
    public CRTraitElement_ActiveFaerunArmorAttributeMod write(ByteBuf buf)
    {
        super.write(buf);

        mod.write(buf);
        buf.writeDouble(minAmount);
        buf.writeDouble(maxAmount);

        return this;
    }

    @Override
    public CRTraitElement_ActiveFaerunArmorAttributeMod read(ByteBuf buf)
    {
        super.read(buf);

        mod.read(buf);
        minAmount = buf.readDouble();
        maxAmount = buf.readDouble();

        return this;
    }

    @Override
    public CRTraitElement_ActiveFaerunArmorAttributeMod save(OutputStream stream)
    {
        super.save(stream);

        mod.save(stream);
        new CDouble().set(minAmount).save(stream).set(maxAmount).save(stream);

        return this;
    }

    @Override
    public CRTraitElement_ActiveFaerunArmorAttributeMod load(InputStream stream)
    {
        super.load(stream);

        mod.load(stream);

        CDouble cd = new CDouble();
        minAmount = cd.load(stream).value;
        maxAmount = cd.load(stream).value;

        return this;
    }
}
