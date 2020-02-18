package com.fantasticsource.tiamatitems.trait.element;

import com.fantasticsource.tiamatitems.nbt.ActionTags;
import com.fantasticsource.tiamatitems.trait.CTraitElement;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;

public class CTraitElement_LeftClickAction extends CTraitElement
{
    public String actionName = "";

    @Override
    public String getDescription()
    {
        return "Left Click: " + actionName;
    }

    @Override
    public String getDescription(int wholeNumberPercentage)
    {
        return getDescription();
    }

    @Override
    public void applyToItem(ItemStack stack, int wholeNumberPercentage)
    {
        ActionTags.setLeftClickAction(stack, actionName);
    }


    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof CTraitElement_LeftClickAction)) return false;

        CTraitElement_LeftClickAction other = (CTraitElement_LeftClickAction) obj;
        return other.actionName.equals(actionName);
    }


    @Override
    public CTraitElement_LeftClickAction write(ByteBuf buf)
    {
        super.write(buf);

        ByteBufUtils.writeUTF8String(buf, actionName);

        return this;
    }

    @Override
    public CTraitElement_LeftClickAction read(ByteBuf buf)
    {
        super.read(buf);

        actionName = ByteBufUtils.readUTF8String(buf);

        return this;
    }

    @Override
    public CTraitElement_LeftClickAction save(OutputStream stream)
    {
        super.save(stream);

        new CStringUTF8().set(actionName).save(stream);

        return this;
    }

    @Override
    public CTraitElement_LeftClickAction load(InputStream stream)
    {
        super.load(stream);

        actionName = new CStringUTF8().load(stream).value;

        return this;
    }
}
