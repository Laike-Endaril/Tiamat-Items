package com.fantasticsource.tiamatitems.trait.unrecalculable;

import com.fantasticsource.tiamatitems.trait.CTrait;
import com.fantasticsource.tools.component.CInt;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;

public class CUnrecalculableTrait extends CTrait
{
    public HashSet<CUnrecalculableTraitElement> elements = new HashSet<>();


    /**
     * @return The monetary value of the resulting trait
     */
    public double applyToItem(ItemStack stack, double itemTypeAndLevelMultiplier)
    {
        int count = 0;
        double result, totalResult = 0;
        for (CUnrecalculableTraitElement element : elements)
        {
            result = element.applyToItem(stack, itemTypeAndLevelMultiplier);
            if (result == -1) continue;

            totalResult += result;
            count++;
        }


        if (count == 0) return (minValue + maxValue) / 2;

        return minValue + (maxValue - minValue) * totalResult / count;
    }


    @Override
    public CUnrecalculableTrait write(ByteBuf buf)
    {
        super.write(buf);

        buf.writeInt(elements.size());
        for (CUnrecalculableTraitElement element : elements) writeMarked(buf, element);

        return this;
    }

    @Override
    public CUnrecalculableTrait read(ByteBuf buf)
    {
        super.read(buf);

        elements.clear();
        for (int i = buf.readInt(); i > 0; i--) elements.add((CUnrecalculableTraitElement) readMarked(buf));

        return this;
    }

    @Override
    public CUnrecalculableTrait save(OutputStream stream)
    {
        super.save(stream);

        new CInt().set(elements.size()).save(stream);
        for (CUnrecalculableTraitElement element : elements) saveMarked(stream, element);

        return this;
    }

    @Override
    public CUnrecalculableTrait load(InputStream stream)
    {
        super.load(stream);

        CInt ci = new CInt();

        elements.clear();
        for (int i = ci.load(stream).value; i > 0; i--) elements.add((CUnrecalculableTraitElement) loadMarked(stream));

        return this;
    }
}
