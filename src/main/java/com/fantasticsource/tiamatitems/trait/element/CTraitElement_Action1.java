package com.fantasticsource.tiamatitems.trait.element;

import com.fantasticsource.tiamatitems.nbt.ActionTags;
import com.fantasticsource.tiamatitems.trait.CTraitElement;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;

public class CTraitElement_Action1 extends CTraitElement
{
    public String actionName = "";

    @Override
    public String getDescription()
    {
        return "Set item action 1 to " + actionName;
    }

    @Override
    public String getDescription(int wholeNumberPercentage)
    {
        return getDescription();
    }

    @Override
    public void applyToItem(ItemStack stack, int wholeNumberPercentage)
    {
        ActionTags.setItemAction1(stack, actionName);
    }


    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof CTraitElement_Action1)) return false;

        CTraitElement_Action1 other = (CTraitElement_Action1) obj;
        return other.actionName.equals(actionName);
    }


    @Override
    public CTraitElement_Action1 write(ByteBuf buf)
    {
        super.write(buf);

        ByteBufUtils.writeUTF8String(buf, actionName);

        return this;
    }

    @Override
    public CTraitElement_Action1 read(ByteBuf buf)
    {
        super.read(buf);

        actionName = ByteBufUtils.readUTF8String(buf);

        return this;
    }

    @Override
    public CTraitElement_Action1 save(OutputStream stream)
    {
        super.save(stream);

        new CStringUTF8().set(actionName).save(stream);

        return this;
    }

    @Override
    public CTraitElement_Action1 load(InputStream stream)
    {
        super.load(stream);

        actionName = new CStringUTF8().load(stream).value;

        return this;
    }
}