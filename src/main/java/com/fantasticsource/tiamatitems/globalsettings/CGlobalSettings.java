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
            baseMultiplier = 1,
            multiplierBonusPerLevel = 1;

    public static LinkedHashMap<String, Double> attributeBalanceMultipliers = new LinkedHashMap<>();


    @Override
    public CGlobalSettings write(ByteBuf buf)
    {
        buf.writeDouble(baseMultiplier);
        buf.writeDouble(multiplierBonusPerLevel);

        return this;
    }

    @Override
    public CGlobalSettings read(ByteBuf buf)
    {
        baseMultiplier = buf.readDouble();
        multiplierBonusPerLevel = buf.readDouble();

        return this;
    }

    @Override
    public CGlobalSettings save(OutputStream stream)
    {
        new CDouble().set(baseMultiplier).save(stream).set(multiplierBonusPerLevel).save(stream);

        return this;
    }

    @Override
    public CGlobalSettings load(InputStream stream)
    {
        CDouble cd = new CDouble();
        baseMultiplier = cd.load(stream).value;
        multiplierBonusPerLevel = cd.load(stream).value;

        return this;
    }
}
