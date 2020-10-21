package com.fantasticsource.tiamatitems.trait.recalculable.element;

import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CRTraitElement_AssemblyName extends CRecalculableTraitElement
{
    public String assemblyName = "Assembly";


    @Override
    public int requiredArgumentCount()
    {
        return 0;
    }


    @Override
    public String getDescription(ArrayList<Integer> baseArgs, double[] multipliedArgs)
    {
        return "Assembled Item Name: " + assemblyName;
    }


    @Override
    public void applyToItem(ItemStack stack, int[] baseArgs, double[] multipliedArgs)
    {
        MiscTags.setAssemblyNameOverride(stack, assemblyName);
    }


    @Override
    public CRTraitElement_AssemblyName write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, assemblyName);

        return this;
    }

    @Override
    public CRTraitElement_AssemblyName read(ByteBuf buf)
    {
        assemblyName = ByteBufUtils.readUTF8String(buf);

        return this;
    }

    @Override
    public CRTraitElement_AssemblyName save(OutputStream stream)
    {
        new CStringUTF8().set(assemblyName).save(stream);

        return this;
    }

    @Override
    public CRTraitElement_AssemblyName load(InputStream stream)
    {
        assemblyName = new CStringUTF8().load(stream).value;

        return this;
    }
}
