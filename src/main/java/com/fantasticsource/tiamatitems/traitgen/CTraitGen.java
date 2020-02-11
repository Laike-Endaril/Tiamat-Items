package com.fantasticsource.tiamatitems.traitgen;

import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class CTraitGen extends Component
{
    String name = "";


    public abstract void applyToItem(ItemStack stack);


    @Override
    public CTraitGen write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);

        return this;
    }

    @Override
    public CTraitGen read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);

        return this;
    }

    @Override
    public CTraitGen save(OutputStream stream)
    {
        new CStringUTF8().set(name).save(stream);

        return this;
    }

    @Override
    public CTraitGen load(InputStream stream)
    {
        name = new CStringUTF8().load(stream).value;

        return this;
    }
}
