package com.fantasticsource.tiamatitems.settings;

import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.datastructures.Color;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class CRarity extends Component implements Comparable<CRarity>
{
    public String name = "";
    public Color color = Color.WHITE;
    public TextFormatting textColor = TextFormatting.WHITE;
    public int ordering = 0;

    public double itemLevelModifier;

    public LinkedHashMap<String, Integer> traitPoolSetRollCounts = new LinkedHashMap<>();


    @Override
    public CRarity write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeInt(color.color());
        buf.writeInt(textColor.getColorIndex());
        buf.writeInt(ordering);

        buf.writeDouble(itemLevelModifier);

        buf.writeInt(traitPoolSetRollCounts.size());
        for (Map.Entry<String, Integer> entry : traitPoolSetRollCounts.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            buf.writeInt(entry.getValue());
        }

        return this;
    }

    @Override
    public CRarity read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);
        color = new Color(buf.readInt());
        textColor = TextFormatting.fromColorIndex(buf.readInt());
        ordering = buf.readInt();

        itemLevelModifier = buf.readDouble();

        traitPoolSetRollCounts.clear();
        for (int i = buf.readInt(); i > 0; i--) traitPoolSetRollCounts.put(ByteBufUtils.readUTF8String(buf), buf.readInt());

        return this;
    }

    @Override
    public CRarity save(OutputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8().set(name).save(stream);
        CInt ci = new CInt().set(color.color()).save(stream).set(textColor.getColorIndex()).save(stream).set(ordering).save(stream);

        new CDouble().set(itemLevelModifier).save(stream);

        ci.set(traitPoolSetRollCounts.size()).save(stream);
        for (Map.Entry<String, Integer> entry : traitPoolSetRollCounts.entrySet())
        {
            cs.set(entry.getKey()).save(stream);
            ci.set(entry.getValue()).save(stream);
        }

        return this;
    }

    @Override
    public CRarity load(InputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8();
        CInt ci = new CInt();

        name = cs.load(stream).value;
        color = new Color(ci.load(stream).value);
        textColor = TextFormatting.fromColorIndex(ci.load(stream).value);
        ordering = ci.load(stream).value;

        itemLevelModifier = new CDouble().load(stream).value;

        traitPoolSetRollCounts.clear();
        for (int i = ci.load(stream).value; i > 0; i--) traitPoolSetRollCounts.put(cs.load(stream).value, ci.load(stream).value);

        return this;
    }

    @Override
    public int compareTo(CRarity other)
    {
        return ordering - other.ordering;
    }
}
