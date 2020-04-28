package com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes;

import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.datastructures.Color;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CRandomRGB extends Component
{
    public int paintType = 255; //Normal paint type
    public int rMin = 0, rMax = 255, gMin = 0, gMax = 255, bMin = 0, bMax = 255;
    public ArrayList<CRGBFunction> functions = new ArrayList<>();


    public Color generate()
    {
        int[] rgb = new int[3];
        rgb[0] = rMin + Tools.random(rMax - rMin);
        rgb[1] = gMin + Tools.random(gMax - gMin);
        rgb[2] = bMin + Tools.random(bMax - bMin);

        for (CRGBFunction function : functions) function.tryApply(rgb);

        rgb[0] = Tools.min(Tools.max(rgb[0], 0), 255);
        rgb[1] = Tools.min(Tools.max(rgb[1], 0), 255);
        rgb[2] = Tools.min(Tools.max(rgb[2], 0), 255);

        return new Color(rgb[0], rgb[1], rgb[2], paintType);
    }


    @Override
    public CRandomRGB write(ByteBuf buf)
    {
        for (int i : new int[]{paintType, rMin, rMax, gMin, gMax, bMin, bMax, functions.size()}) buf.writeInt(i);
        for (CRGBFunction function : functions) Component.writeMarked(buf, function);

        return this;
    }

    @Override
    public CRandomRGB read(ByteBuf buf)
    {
        paintType = buf.readInt();
        rMin = buf.readInt();
        rMax = buf.readInt();
        gMin = buf.readInt();
        gMax = buf.readInt();
        bMin = buf.readInt();
        bMax = buf.readInt();

        functions.clear();
        for (int i = buf.readInt(); i > 0; i--) functions.add((CRGBFunction) Component.readMarked(buf));

        return this;
    }

    @Override
    public CRandomRGB save(OutputStream stream)
    {
        CInt ci = new CInt();
        for (int i : new int[]{paintType, rMin, rMax, gMin, gMax, bMin, bMax, functions.size()}) ci.set(i).save(stream);
        for (CRGBFunction function : functions) Component.saveMarked(stream, function);

        return this;
    }

    @Override
    public CRandomRGB load(InputStream stream)
    {
        CInt ci = new CInt();

        paintType = ci.load(stream).value;
        rMin = ci.load(stream).value;
        rMax = ci.load(stream).value;
        gMin = ci.load(stream).value;
        gMax = ci.load(stream).value;
        bMin = ci.load(stream).value;
        bMax = ci.load(stream).value;

        functions.clear();
        for (int i = ci.load(stream).value; i > 0; i--) functions.add((CRGBFunction) Component.loadMarked(stream));

        return this;
    }
}
