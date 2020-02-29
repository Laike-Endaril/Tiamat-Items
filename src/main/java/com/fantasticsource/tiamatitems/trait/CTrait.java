package com.fantasticsource.tiamatitems.trait;

import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class CTrait extends Component
{
    public String name = "";
    public boolean isGood = true; //Whether it's a good thing to have more of this trait
    public double minValue = 0, maxValue = 0; //The monetary value of this trait at minimum and maximum percentage, respectively


    @Override
    public CTrait write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeBoolean(isGood);
        buf.writeDouble(minValue);
        buf.writeDouble(maxValue);

        return this;
    }

    @Override
    public CTrait read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);
        isGood = buf.readBoolean();
        minValue = buf.readDouble();
        maxValue = buf.readDouble();

        return this;
    }

    @Override
    public CTrait save(OutputStream stream)
    {
        new CStringUTF8().set(name).save(stream);
        new CBoolean().set(isGood).save(stream);
        new CDouble().set(minValue).save(stream).set(maxValue).save(stream);

        return this;
    }

    @Override
    public CTrait load(InputStream stream)
    {
        CDouble cd = new CDouble();

        name = new CStringUTF8().load(stream).value;

        isGood = new CBoolean().load(stream).value;
        minValue = cd.load(stream).value;
        maxValue = cd.load(stream).value;

        return this;
    }
}
