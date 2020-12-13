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

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class CRTraitElement_GenericDouble extends CRecalculableTraitElement
{
    public String name = "";
    public double minAmount = 0, maxAmount = 1;


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
            return I18n.translateToLocalFormatted(MODID + ".generic." + name + ".description", minAmount, maxAmount);
        }

        double amount = getStandardAmount(args, 0, minAmount, maxAmount, itemTypeAndLevelMultiplier);
        return I18n.translateToLocalFormatted(MODID + ".generic." + name, amount);
    }


    @Override
    public void applyToItem(ItemStack stack, int[] args, double itemTypeAndLevelMultiplier)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        double amount = getStandardAmount(args, 0, minAmount, maxAmount, itemTypeAndLevelMultiplier);
        MCTools.getOrGenerateSubCompound(compound, MODID, "generic").setDouble(name, amount);
    }


    @Override
    public CRTraitElement_GenericDouble write(ByteBuf buf)
    {
        super.write(buf);

        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeDouble(minAmount);
        buf.writeDouble(maxAmount);

        return this;
    }

    @Override
    public CRTraitElement_GenericDouble read(ByteBuf buf)
    {
        super.read(buf);

        name = ByteBufUtils.readUTF8String(buf);
        minAmount = buf.readDouble();
        maxAmount = buf.readDouble();

        return this;
    }

    @Override
    public CRTraitElement_GenericDouble save(OutputStream stream)
    {
        super.save(stream);

        new CStringUTF8().set(name).save(stream);
        new CDouble().set(minAmount).save(stream).set(maxAmount).save(stream);

        return this;
    }

    @Override
    public CRTraitElement_GenericDouble load(InputStream stream)
    {
        super.load(stream);

        CDouble cd = new CDouble();

        name = new CStringUTF8().load(stream).value;
        minAmount = cd.load(stream).value;
        maxAmount = cd.load(stream).value;

        return this;
    }
}
