package com.fantasticsource.tiamatitems.trait.recalculable.element;

import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;

public class CRTraitElement_AssemblyName extends CRecalculableTraitElement
{
    public String assemblyName = "Assembly";


    @Override
    public int requiredArgumentCount()
    {
        return 0;
    }


    @Override
    public String getDescription(ItemStack stack, int[] args, double itemTypeAndLevelMultiplier)
    {
        return "Assembled Item Name: " + assemblyName;
    }


    @Override
    public void applyToItem(ItemStack stack, int[] args, double itemTypeAndLevelMultiplier)
    {
        MiscTags.setAssemblyNameOverride(stack, assemblyName);
    }


    @Override
    public CRTraitElement_AssemblyName write(ByteBuf buf)
    {
        super.write(buf);

        ByteBufUtils.writeUTF8String(buf, assemblyName);

        return this;
    }

    @Override
    public CRTraitElement_AssemblyName read(ByteBuf buf)
    {
        super.read(buf);

        assemblyName = ByteBufUtils.readUTF8String(buf);

        return this;
    }

    @Override
    public CRTraitElement_AssemblyName save(OutputStream stream)
    {
        super.save(stream);

        new CStringUTF8().set(assemblyName).save(stream);

        return this;
    }

    @Override
    public CRTraitElement_AssemblyName load(InputStream stream)
    {
        super.load(stream);

        assemblyName = new CStringUTF8().load(stream).value;

        return this;
    }
}
