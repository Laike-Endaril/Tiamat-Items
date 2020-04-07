package com.fantasticsource.tiamatitems.trait.recalculable.element;

import com.fantasticsource.tiamatitems.nbt.ActionTags;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CRTraitElement_LeftClickAction extends CRecalculableTraitElement
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
        return "Left Click: " + (actionName.equals("") ? "(Do Nothing)" : actionName);
    }


    @Override
    public void applyToItem(ItemStack stack, int[] baseArgs, double[] multipliedArgs)
    {
        ActionTags.setLeftClickAction(stack, actionName);
    }


    @Override
    public CRTraitElement_LeftClickAction write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, actionName);

        return this;
    }

    @Override
    public CRTraitElement_LeftClickAction read(ByteBuf buf)
    {
        actionName = ByteBufUtils.readUTF8String(buf);

        return this;
    }

    @Override
    public CRTraitElement_LeftClickAction save(OutputStream stream)
    {
        new CStringUTF8().set(actionName).save(stream);

        return this;
    }

    @Override
    public CRTraitElement_LeftClickAction load(InputStream stream)
    {
        actionName = new CStringUTF8().load(stream).value;

        return this;
    }
}
