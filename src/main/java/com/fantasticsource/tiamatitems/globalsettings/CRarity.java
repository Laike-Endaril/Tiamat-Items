package com.fantasticsource.tiamatitems.globalsettings;

import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.datastructures.Color;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CRarity extends Component
{
    public String name = "";
    public Color color = Color.WHITE;
    public TextFormatting textColor = TextFormatting.WHITE;
    public CAttributePool attributePool = new CAttributePool();
    public ArrayList<String> randomAttributeGenBlacklist = new ArrayList<>();


    @Override
    public CRarity write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeInt(color.color());
        buf.writeInt(textColor.getColorIndex());
        attributePool.write(buf);

        buf.writeInt(randomAttributeGenBlacklist.size());
        for (String s : randomAttributeGenBlacklist) ByteBufUtils.writeUTF8String(buf, s);

        return this;
    }

    @Override
    public CRarity read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);
        color.setColor(buf.readInt());
        textColor = TextFormatting.fromColorIndex(buf.readInt());
        attributePool.read(buf);

        randomAttributeGenBlacklist.clear();
        for (int i = buf.readInt(); i > 0; i--) randomAttributeGenBlacklist.add(ByteBufUtils.readUTF8String(buf));

        return this;
    }

    @Override
    public CRarity save(OutputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8().set(name).save(stream);
        new CInt().set(color.color()).save(stream).set(textColor.getColorIndex()).save(stream).set(randomAttributeGenBlacklist.size()).save(stream);
        for (String s : randomAttributeGenBlacklist) cs.set(s).save(stream);

        return this;
    }

    @Override
    public CRarity load(InputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8();
        name = cs.load(stream).value;

        CInt ci = new CInt();
        color.setColor(ci.load(stream).value);
        textColor = TextFormatting.fromColorIndex(ci.load(stream).value);
        attributePool.load(stream);

        randomAttributeGenBlacklist.clear();
        for (int i = ci.load(stream).value; i > 0; i--) randomAttributeGenBlacklist.add(cs.load(stream).value);

        return this;
    }
}
