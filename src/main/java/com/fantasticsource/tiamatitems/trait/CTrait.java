package com.fantasticsource.tiamatitems.trait;

import com.fantasticsource.tiamatitems.globalsettings.CGlobalSettings;
import com.fantasticsource.tiamatitems.nbt.TraitTags;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;

public class CTrait extends Component
{
    public String name = "";
    public boolean isGood = true; //Whether it's a good thing to have more of this trait
    public double minValue = 0, maxValue = 0; //The monetary value of this trait at minimum and maximum percentage, respectively
    public HashSet<CTraitElement> elements = new HashSet<>();


    /**
     * @return The monetary value of the resulting trait
     */
    public double applyToItem(ItemStack stack, String poolSetName, CItemType itemTypeGen, double level, CTraitPool pool, int... baseArgs)
    {
        ArrayList<Integer> baseArguments = new ArrayList<>();
        for (int base : baseArgs) baseArguments.add(base);


        int requiredArgumentCount = 0;
        for (CTraitElement element : elements)
        {
            requiredArgumentCount = Tools.max(requiredArgumentCount, element.requiredArgumentCount());
        }
        while (baseArguments.size() < requiredArgumentCount)
        {
            baseArguments.add(Tools.random(Integer.MAX_VALUE));
        }


        double multipliedTotal = 0;
        double[] multipliedArgs = new double[baseArguments.size()];
        int i = 0;
        for (int base : baseArguments)
        {
            double multiplied = ((double) base / (Integer.MAX_VALUE - 1) * itemTypeGen.percentageMultiplier * (CGlobalSettings.baseAttributeMultiplier + (CGlobalSettings.attributeMultiplierPerLevel * level)));
            multipliedArgs[i++] = multiplied;
            multipliedTotal += multiplied;
        }


        for (CTraitElement element : elements) element.applyToItem(stack, baseArguments, multipliedArgs);


        TraitTags.addTraitTag(stack, poolSetName, pool, this, baseArguments);


        return minValue + (maxValue - minValue) * multipliedTotal / multipliedArgs.length / (Integer.MAX_VALUE - 1);
    }


    @Override
    public CTrait write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeBoolean(isGood);
        buf.writeDouble(minValue);
        buf.writeDouble(maxValue);

        buf.writeInt(elements.size());
        for (CTraitElement element : elements) writeMarked(buf, element);

        return this;
    }

    @Override
    public CTrait read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);
        isGood = buf.readBoolean();
        minValue = buf.readDouble();
        maxValue = buf.readDouble();

        elements.clear();
        for (int i = buf.readInt(); i > 0; i--) elements.add((CTraitElement) readMarked(buf));

        return this;
    }

    @Override
    public CTrait save(OutputStream stream)
    {
        new CStringUTF8().set(name).save(stream);
        new CBoolean().set(isGood).save(stream);
        new CDouble().set(minValue).save(stream).set(maxValue).save(stream);

        new CInt().set(elements.size()).save(stream);
        for (CTraitElement element : elements) saveMarked(stream, element);

        return this;
    }

    @Override
    public CTrait load(InputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8();
        CInt ci = new CInt();
        CDouble cd = new CDouble();

        name = cs.load(stream).value;
        isGood = new CBoolean().load(stream).value;
        minValue = cd.load(stream).value;
        maxValue = cd.load(stream).value;

        elements.clear();
        for (int i = ci.load(stream).value; i > 0; i--) elements.add((CTraitElement) loadMarked(stream));

        return this;
    }
}
