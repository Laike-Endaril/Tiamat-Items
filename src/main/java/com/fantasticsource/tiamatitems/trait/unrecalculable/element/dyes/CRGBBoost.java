package com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes;

import com.fantasticsource.tools.component.CInt;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public class CRGBBoost extends CRGBFunction
{
    public int[] toAdd = new int[]{0, 0, 0};

    @Override
    public String name()
    {
        return "Color Boost";
    }

    @Override
    public String description()
    {
        return "Boost RGB by (" + toAdd[0] + ", " + toAdd[1] + ", " + toAdd[2] + ")" + (endIfExecuted ? " and end" : "") + " (" + (int) (chance * 100) + "% chance)";
    }

    @Override
    public void apply(int[] rgb)
    {
        rgb[0] += toAdd[0];
        rgb[1] += toAdd[1];
        rgb[2] += toAdd[2];
    }

    @Override
    public CRGBBoost write(ByteBuf buf)
    {
        super.write(buf);

        buf.writeInt(toAdd[0]);
        buf.writeInt(toAdd[1]);
        buf.writeInt(toAdd[2]);

        return this;
    }

    @Override
    public CRGBBoost read(ByteBuf buf)
    {
        super.read(buf);

        toAdd[0] = buf.readInt();
        toAdd[1] = buf.readInt();
        toAdd[2] = buf.readInt();

        return this;
    }

    @Override
    public CRGBBoost save(OutputStream stream)
    {
        super.save(stream);

        new CInt().set(toAdd[0]).save(stream).set(toAdd[1]).save(stream).set(toAdd[2]).save(stream);

        return this;
    }

    @Override
    public CRGBBoost load(InputStream stream)
    {
        super.load(stream);

        CInt ci = new CInt();
        toAdd[0] = ci.load(stream).value;
        toAdd[1] = ci.load(stream).value;
        toAdd[2] = ci.load(stream).value;

        return this;
    }
}
