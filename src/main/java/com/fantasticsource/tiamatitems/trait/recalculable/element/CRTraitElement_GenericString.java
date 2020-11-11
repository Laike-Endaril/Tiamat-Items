package com.fantasticsource.tiamatitems.trait.recalculable.element;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
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

public class CRTraitElement_GenericString extends CRecalculableTraitElement
{
    public String name, value;


    @Override
    public int requiredArgumentCount()
    {
        return 0;
    }


    @Override
    public String getDescriptionInternal(ArrayList<Integer> baseArgs, double[] multipliedArgs)
    {
        return I18n.translateToLocalFormatted(MODID + ".generic." + name + ".description", value);
    }


    @Override
    public void applyToItemInternal(ItemStack stack, int[] baseArgs, double[] multipliedArgs)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        MCTools.getOrGenerateSubCompound(compound, MODID, "generic").setString(name, value);
    }


    @Override
    public CRTraitElement_GenericString write(ByteBuf buf)
    {
        super.write(buf);

        ByteBufUtils.writeUTF8String(buf, name);
        ByteBufUtils.writeUTF8String(buf, value);

        return this;
    }

    @Override
    public CRTraitElement_GenericString read(ByteBuf buf)
    {
        super.read(buf);

        name = ByteBufUtils.readUTF8String(buf);
        value = ByteBufUtils.readUTF8String(buf);

        return this;
    }

    @Override
    public CRTraitElement_GenericString save(OutputStream stream)
    {
        super.save(stream);

        new CStringUTF8().set(name).save(stream).set(value).save(stream);

        return this;
    }

    @Override
    public CRTraitElement_GenericString load(InputStream stream)
    {
        super.load(stream);

        CStringUTF8 cs = new CStringUTF8();

        name = cs.load(stream).value;
        value = cs.load(stream).value;

        return this;
    }
}
