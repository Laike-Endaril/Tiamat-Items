package com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes;

import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class CRGBFunction extends Component
{
    public double chance = 0.5;
    public boolean endIfExecuted = false;

    public abstract String name();

    public abstract String description();

    public final void tryApply(int[] rgb)
    {
        if (Math.random() < chance) apply(rgb);
    }

    public abstract void apply(int[] rgb);

    @Override
    public CRGBFunction write(ByteBuf buf)
    {
        buf.writeDouble(chance);
        buf.writeBoolean(endIfExecuted);

        return this;
    }

    @Override
    public CRGBFunction read(ByteBuf buf)
    {
        chance = buf.readDouble();
        endIfExecuted = buf.readBoolean();

        return this;
    }

    @Override
    public CRGBFunction save(OutputStream stream)
    {
        new CDouble().set(chance).save(stream);
        new CBoolean().set(endIfExecuted).save(stream);

        return this;
    }

    @Override
    public CRGBFunction load(InputStream stream)
    {
        chance = new CDouble().load(stream).value;
        endIfExecuted = new CBoolean().load(stream).value;

        return this;
    }
}
