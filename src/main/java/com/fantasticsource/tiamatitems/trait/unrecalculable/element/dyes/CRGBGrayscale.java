package com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes;

import com.fantasticsource.tools.Tools;

public class CRGBGrayscale extends CRGBFunction
{
    @Override
    public String name()
    {
        return "Grayscale";
    }

    @Override
    public String description()
    {
        return "Convert to grayscale (" + (int) (chance * 100) + "% chance)";
    }

    @Override
    public void apply(int[] rgb)
    {
        int v = Tools.choose(rgb);
        rgb[0] = v;
        rgb[1] = v;
        rgb[2] = v;
    }
}
