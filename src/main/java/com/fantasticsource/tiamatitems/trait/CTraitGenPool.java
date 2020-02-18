package com.fantasticsource.tiamatitems.trait;

import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class CTraitGenPool extends Component
{
    public static LinkedHashMap<String, CTraitGenPool> traitGenPools = new LinkedHashMap<>(); //TODO handle data retention


    public String name; //TODO disallow setting to the name "null" (see TraitTags class)
    public LinkedHashMap<CTrait, Integer> traitGenWeights = new LinkedHashMap<>();


    @Override
    public CTraitGenPool write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);

        buf.writeInt(traitGenWeights.size());
        for (Map.Entry<CTrait, Integer> entry : traitGenWeights.entrySet())
        {
            writeMarked(buf, entry.getKey());
            buf.writeInt(entry.getValue());
        }

        return this;
    }

    @Override
    public CTraitGenPool read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);

        traitGenWeights.clear();
        for (int i = buf.readInt(); i > 0; i--) traitGenWeights.put((CTrait) readMarked(buf), buf.readInt());

        return this;
    }

    @Override
    public CTraitGenPool save(OutputStream stream)
    {
        new CStringUTF8().set(name).save(stream);

        CInt ci = new CInt().set(traitGenWeights.size()).save(stream);
        for (Map.Entry<CTrait, Integer> entry : traitGenWeights.entrySet())
        {
            saveMarked(stream, entry.getKey());
            ci.set(entry.getValue()).save(stream);
        }

        return this;
    }

    @Override
    public CTraitGenPool load(InputStream stream)
    {
        name = new CStringUTF8().load(stream).value;

        CInt ci = new CInt();
        traitGenWeights.clear();
        for (int i = ci.load(stream).value; i > 0; i--) traitGenWeights.put((CTrait) loadMarked(stream), ci.load(stream).value);

        return this;
    }
}
