package com.fantasticsource.tiamatitems.traitgen;

import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class CTraitGenElement extends Component
{
    public double minimum = 0, maximum = 0;

    public abstract void applyToItem(ItemStack stack, double percentage);

    public abstract String getDescription();

    public abstract String getDescription(double percentage);

    @Override
    public CTraitGenElement write(ByteBuf buf)
    {
        buf.writeDouble(minimum);
        buf.writeDouble(maximum);

        return this;
    }

    @Override
    public CTraitGenElement read(ByteBuf buf)
    {
        minimum = buf.readDouble();
        maximum = buf.readDouble();

        return this;
    }

    @Override
    public CTraitGenElement save(OutputStream stream)
    {
        new CDouble().set(minimum).save(stream).set(maximum).save(stream);

        return this;
    }

    @Override
    public CTraitGenElement load(InputStream stream)
    {
        CDouble cd = new CDouble();
        minimum = cd.load(stream).value;
        maximum = cd.load(stream).value;

        return this;
    }
}
