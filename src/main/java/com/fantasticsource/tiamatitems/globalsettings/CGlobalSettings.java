package com.fantasticsource.tiamatitems.globalsettings;

import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;

public class CGlobalSettings extends Component
{
    public static double
            baseItemComponentPower = 0,
            itemComponentPowerPerLevel = 10,
            itemPowerVariance = 0.25;

    public static LinkedHashMap<String, CAffix> allAffixes = new LinkedHashMap<>();


    @Override
    public CGlobalSettings write(ByteBuf buf)
    {
        buf.writeDouble(baseItemComponentPower);
        buf.writeDouble(itemComponentPowerPerLevel);
        buf.writeDouble(itemPowerVariance);

        return this;
    }

    @Override
    public CGlobalSettings read(ByteBuf buf)
    {
        baseItemComponentPower = buf.readDouble();
        itemComponentPowerPerLevel = buf.readDouble();
        itemPowerVariance = buf.readDouble();

        return this;
    }

    @Override
    public CGlobalSettings save(OutputStream stream)
    {
        new CDouble().set(baseItemComponentPower).save(stream).set(itemComponentPowerPerLevel).save(stream).set(itemPowerVariance).save(stream);

        return this;
    }

    @Override
    public CGlobalSettings load(InputStream stream)
    {
        CDouble cd = new CDouble();
        baseItemComponentPower = cd.load(stream).value;
        itemComponentPowerPerLevel = cd.load(stream).value;
        itemPowerVariance = cd.load(stream).value;

        return this;
    }
}
