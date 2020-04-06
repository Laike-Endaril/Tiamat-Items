package com.fantasticsource.tiamatitems.trait.unrecalculable;

import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class CUnrecalculableTraitPool extends Component
{
    public String name;
    public LinkedHashMap<CUnrecalculableTrait, Integer> traitGenWeights = new LinkedHashMap<>();


    @Override
    public CUnrecalculableTraitPool write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);

        buf.writeInt(traitGenWeights.size());
        for (Map.Entry<CUnrecalculableTrait, Integer> entry : traitGenWeights.entrySet())
        {
            entry.getKey().write(buf);
            buf.writeInt(entry.getValue());
        }

        return this;
    }

    @Override
    public CUnrecalculableTraitPool read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);

        traitGenWeights.clear();
        for (int i = buf.readInt(); i > 0; i--) traitGenWeights.put(new CUnrecalculableTrait().read(buf), buf.readInt());

        return this;
    }

    @Override
    public CUnrecalculableTraitPool save(OutputStream stream)
    {
        new CStringUTF8().set(name).save(stream);

        CInt ci = new CInt().set(traitGenWeights.size()).save(stream);
        for (Map.Entry<CUnrecalculableTrait, Integer> entry : traitGenWeights.entrySet())
        {
            entry.getKey().save(stream);
            ci.set(entry.getValue()).save(stream);
        }

        return this;
    }

    @Override
    public CUnrecalculableTraitPool load(InputStream stream)
    {
        name = new CStringUTF8().load(stream).value;

        CInt ci = new CInt();
        traitGenWeights.clear();
        for (int i = ci.load(stream).value; i > 0; i--) traitGenWeights.put(new CUnrecalculableTrait().load(stream), ci.load(stream).value);

        return this;
    }
}
