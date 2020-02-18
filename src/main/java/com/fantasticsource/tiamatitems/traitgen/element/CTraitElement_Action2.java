package com.fantasticsource.tiamatitems.traitgen.element;

import com.fantasticsource.tiamatitems.nbt.ActionTags;
import com.fantasticsource.tiamatitems.traitgen.CTraitElement;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;

public class CTraitElement_Action2 extends CTraitElement
{
    public String actionName = "";

    @Override
    public String getDescription()
    {
        return "Set item action 2 to " + actionName;
    }

    @Override
    public String getDescription(double percentage)
    {
        return getDescription();
    }

    @Override
    public void applyToItem(ItemStack stack, int wholeNumberPercentage)
    {
        ActionTags.setItemAction2(stack, actionName);
    }


    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof CTraitElement_Action2)) return false;

        CTraitElement_Action2 other = (CTraitElement_Action2) obj;
        return other.actionName.equals(actionName);
    }


    @Override
    public CTraitElement_Action2 write(ByteBuf buf)
    {
        super.write(buf);

        ByteBufUtils.writeUTF8String(buf, actionName);

        return this;
    }

    @Override
    public CTraitElement_Action2 read(ByteBuf buf)
    {
        super.read(buf);

        actionName = ByteBufUtils.readUTF8String(buf);

        return this;
    }

    @Override
    public CTraitElement_Action2 save(OutputStream stream)
    {
        super.save(stream);

        new CStringUTF8().set(actionName).save(stream);

        return this;
    }

    @Override
    public CTraitElement_Action2 load(InputStream stream)
    {
        super.load(stream);

        actionName = new CStringUTF8().load(stream).value;

        return this;
    }
}
