package com.fantasticsource.tiamatitems.traitgen;

import com.fantasticsource.tiamatitems.nbt.TraitTags;
import com.fantasticsource.tools.component.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;

public class CTrait extends Component
{
    public String name;
    public boolean isGood = true; //Whether it's a good thing to have more of this trait
    public double minValue, maxValue; //The monetary value of this trait at minimum and maximum percentage, respectively
    public HashSet<CTraitElement> elements = new HashSet<>();


    public double applyToItem(ItemStack stack, CItemType itemTypeGen, double level, CTraitGenPool pool)
    {
        int wholeNumberPercentage = (int) (Math.random() * 101 * itemTypeGen.percentageMultiplier);
        for (CTraitElement element : elements) element.applyToItem(stack, level, wholeNumberPercentage);

        TraitTags.setItemTraitData(stack, pool, this, wholeNumberPercentage);

        return minValue + (maxValue - minValue) * wholeNumberPercentage / 100;
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
