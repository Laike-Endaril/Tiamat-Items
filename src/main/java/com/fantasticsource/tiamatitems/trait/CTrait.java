package com.fantasticsource.tiamatitems.trait;

import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class CTrait extends Component
{
    public boolean isGood = true; //Whether it's a good thing to have more of this trait
    public double minValue = 0, maxValue = 0; //The monetary value of this trait at minimum and maximum percentage, respectively


    @Override
    public CTrait write(ByteBuf buf)
    {
        buf.writeBoolean(isGood);
        buf.writeDouble(minValue);
        buf.writeDouble(maxValue);

        return this;
    }

    @Override
    public CTrait read(ByteBuf buf)
    {
        isGood = buf.readBoolean();
        minValue = buf.readDouble();
        maxValue = buf.readDouble();

        return this;
    }

    @Override
    public CTrait save(OutputStream stream)
    {
        new CBoolean().set(isGood).save(stream);
        new CDouble().set(minValue).save(stream).set(maxValue).save(stream);

        return this;
    }

    @Override
    public CTrait load(InputStream stream)
    {
        CDouble cd = new CDouble();

        isGood = new CBoolean().load(stream).value;
        minValue = cd.load(stream).value;
        maxValue = cd.load(stream).value;

        return this;
    }
}
