package com.fantasticsource.tiamatitems.trait.recalculable.element;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class CRTraitElement_GenericDouble extends CRecalculableTraitElement
{
    public String name;
    public double minAmount = 0, maxAmount = 1;


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
            return I18n.translateToLocalFormatted(MODID + ".generic." + name + ".description", minAmount, maxAmount);
        }

        double amount = minAmount + (maxAmount - minAmount) * multipliedArgs[0];
        return I18n.translateToLocalFormatted(MODID + ".generic." + name, amount);
    }


    @Override
    public void applyToItem(ItemStack stack, int[] baseArgs, double[] multipliedArgs)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        double amount = minAmount + (maxAmount - minAmount) * multipliedArgs[0];
        MCTools.getOrGenerateSubCompound(compound, MODID, "generic").setDouble(name, amount);
    }


    @Override
    public CRTraitElement_GenericDouble write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeDouble(minAmount);
        buf.writeDouble(maxAmount);

        return this;
    }

    @Override
    public CRTraitElement_GenericDouble read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);
        minAmount = buf.readDouble();
        maxAmount = buf.readDouble();

        return this;
    }

    @Override
    public CRTraitElement_GenericDouble save(OutputStream stream)
    {
        new CStringUTF8().set(name).save(stream);
        new CDouble().set(minAmount).save(stream).set(maxAmount).save(stream);

        return this;
    }

    @Override
    public CRTraitElement_GenericDouble load(InputStream stream)
    {
        CDouble cd = new CDouble();

        name = new CStringUTF8().load(stream).value;
        minAmount = cd.load(stream).value;
        maxAmount = cd.load(stream).value;

        return this;
    }
}
