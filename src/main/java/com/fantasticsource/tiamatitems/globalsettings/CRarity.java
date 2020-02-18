package com.fantasticsource.tiamatitems.globalsettings;

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

public class CRarity extends Component
{
    public static LinkedHashMap<String, CRarity> rarities = new LinkedHashMap<>(); //TODO handle data retention

    public String name = "";
    public Color color = Color.WHITE;
    public TextFormatting textColor = TextFormatting.WHITE;

    public double itemLevelModifier;

    public LinkedHashMap<String, Integer> traitCounts = new LinkedHashMap<>();


    @Override
    public CRarity write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeInt(color.color());
        buf.writeInt(textColor.getColorIndex());

        buf.writeDouble(itemLevelModifier);

        buf.writeInt(traitCounts.size());
        for (Map.Entry<String, Integer> entry : traitCounts.entrySet())
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
        color.setColor(buf.readInt());
        textColor = TextFormatting.fromColorIndex(buf.readInt());

        itemLevelModifier = buf.readDouble();

        traitCounts.clear();
        for (int i = buf.readInt(); i > 0; i--) traitCounts.put(ByteBufUtils.readUTF8String(buf), buf.readInt());

        return this;
    }

    @Override
    public CRarity save(OutputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8().set(name).save(stream);
        CInt ci = new CInt().set(color.color()).save(stream).set(textColor.getColorIndex()).save(stream);

        new CDouble().set(itemLevelModifier).save(stream);

        ci.set(traitCounts.size()).save(stream);
        for (Map.Entry<String, Integer> entry : traitCounts.entrySet())
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
        color.setColor(ci.load(stream).value);
        textColor = TextFormatting.fromColorIndex(ci.load(stream).value);

        itemLevelModifier = new CDouble().load(stream).value;

        traitCounts.clear();
        for (int i = ci.load(stream).value; i > 0; i--) traitCounts.put(cs.load(stream).value, ci.load(stream).value);

        return this;
    }
}
