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

public class CRarity extends Component
{
    public String name = "";
    public Color color = Color.WHITE;
    public TextFormatting textColor = TextFormatting.WHITE;
    public CAttributePool attributePool = new CAttributePool();


    @Override
    public CRarity write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeInt(color.color());
        buf.writeInt(textColor.getColorIndex());
        attributePool.write(buf);

        return this;
    }

    @Override
    public CRarity read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);
        color.setColor(buf.readInt());
        textColor = TextFormatting.fromColorIndex(buf.readInt());
        attributePool.read(buf);

        return this;
    }

    @Override
    public CRarity save(OutputStream stream)
    {
        new CStringUTF8().set(name).save(stream);
        new CInt().set(color.color()).save(stream).set(textColor.getColorIndex()).save(stream);

        return this;
    }

    @Override
    public CRarity load(InputStream stream)
    {
        name = new CStringUTF8().load(stream).value;

        CInt ci = new CInt();
        color.setColor(ci.load(stream).value);
        textColor = TextFormatting.fromColorIndex(ci.load(stream).value);
        attributePool.load(stream);

        return this;
    }
}
