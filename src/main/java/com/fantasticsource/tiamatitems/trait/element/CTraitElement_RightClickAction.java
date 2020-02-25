package com.fantasticsource.tiamatitems.trait.element;

import com.fantasticsource.tiamatitems.nbt.ActionTags;
import com.fantasticsource.tiamatitems.trait.CTraitElement;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CTraitElement_RightClickAction extends CTraitElement
{
    public String actionName = "";


    @Override
    public int requiredArgumentCount()
    {
        return 0;
    }


    @Override
    public String getDescription(ArrayList<Integer> baseArgs, double[] multipliedArgs)
    {
        return "Right click: " + actionName;
    }


    @Override
    public void applyToItem(ItemStack stack, ArrayList<Integer> baseArgs, double[] multipliedArgs)
    {
        ActionTags.setRightClickAction(stack, actionName);
    }


    @Override
    public CTraitElement_RightClickAction write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, actionName);

        return this;
    }

    @Override
    public CTraitElement_RightClickAction read(ByteBuf buf)
    {
        actionName = ByteBufUtils.readUTF8String(buf);

        return this;
    }

    @Override
    public CTraitElement_RightClickAction save(OutputStream stream)
    {
        new CStringUTF8().set(actionName).save(stream);

        return this;
    }

    @Override
    public CTraitElement_RightClickAction load(InputStream stream)
    {
        actionName = new CStringUTF8().load(stream).value;

        return this;
    }
}
