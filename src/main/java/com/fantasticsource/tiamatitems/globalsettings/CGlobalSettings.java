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
            baseAttributeMultiplier = 1,
            attributeMultiplierPerLevel = 1;

    public static LinkedHashMap<String, Double> attributeBalanceMultipliers = new LinkedHashMap<>();


    @Override
    public CGlobalSettings write(ByteBuf buf)
    {
        buf.writeDouble(baseAttributeMultiplier);
        buf.writeDouble(attributeMultiplierPerLevel);

        return this;
    }

    @Override
    public CGlobalSettings read(ByteBuf buf)
    {
        baseAttributeMultiplier = buf.readDouble();
        attributeMultiplierPerLevel = buf.readDouble();

        return this;
    }

    @Override
    public CGlobalSettings save(OutputStream stream)
    {
        new CDouble().set(baseAttributeMultiplier).save(stream).set(attributeMultiplierPerLevel).save(stream);

        return this;
    }

    @Override
    public CGlobalSettings load(InputStream stream)
    {
        CDouble cd = new CDouble();
        baseAttributeMultiplier = cd.load(stream).value;
        attributeMultiplierPerLevel = cd.load(stream).value;

        return this;
    }
}
