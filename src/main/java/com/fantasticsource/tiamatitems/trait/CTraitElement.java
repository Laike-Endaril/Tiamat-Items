package com.fantasticsource.tiamatitems.trait;

import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class CTraitElement extends Component
{
    public double minimum = 0, maximum = 0;
    public String affixSetName = "";

    public final double getDoubleAmount(int wholeNumberPercentage)
    {
        return minimum + (maximum - minimum) * wholeNumberPercentage / 100;
    }

    public final int getIntAmount(int wholeNumberPercentage)
    {
        return (int) Math.round(getDoubleAmount(wholeNumberPercentage));
    }

    public abstract void applyToItem(ItemStack stack, int wholeNumberPercentage);

    public abstract String getDescription();

    public abstract String getDescription(int wholeNumberPercentage);

    @Override
    public CTraitElement write(ByteBuf buf)
    {
        buf.writeDouble(minimum);
        buf.writeDouble(maximum);

        ByteBufUtils.writeUTF8String(buf, affixSetName);

        return this;
    }

    @Override
    public CTraitElement read(ByteBuf buf)
    {
        minimum = buf.readDouble();
        maximum = buf.readDouble();

        affixSetName = ByteBufUtils.readUTF8String(buf);

        return this;
    }

    @Override
    public CTraitElement save(OutputStream stream)
    {
        new CDouble().set(minimum).save(stream).set(maximum).save(stream);

        new CStringUTF8().set(affixSetName).save(stream);

        return this;
    }

    @Override
    public CTraitElement load(InputStream stream)
    {
        CDouble cd = new CDouble();
        minimum = cd.load(stream).value;
        maximum = cd.load(stream).value;

        affixSetName = new CStringUTF8().load(stream).value;

        return this;
    }
}
