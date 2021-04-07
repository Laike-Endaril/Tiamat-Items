package com.fantasticsource.tiamatitems.trait.recalculable;

import com.fantasticsource.tiamatitems.nbt.TraitTags;
import com.fantasticsource.tiamatitems.trait.CTrait;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashSet;

public final class CRecalculableTrait extends CTrait
{
    public boolean addToAssemblyFromPart = true;
    public LinkedHashSet<CRecalculableTraitElement> elements = new LinkedHashSet<>();


    /**
     * @return The monetary value of the resulting trait
     */
    public double generateNBT(ItemStack stack, String poolSetName, CRecalculableTraitPool pool, double itemTypeAndLevelMultiplier, int... baseArgs)
    {
        int requiredArgumentCount = 0;
        for (CRecalculableTraitElement element : elements)
        {
            requiredArgumentCount = Tools.max(requiredArgumentCount, element.requiredArgumentCount());
        }


        int[] baseArguments = new int[requiredArgumentCount];
        double[] decimalArgs = new double[requiredArgumentCount];
        System.arraycopy(baseArgs, 0, baseArguments, 0, Tools.min(baseArgs.length, requiredArgumentCount));
        for (int i = baseArgs.length; i < requiredArgumentCount; i++) baseArguments[i] = Tools.random(Integer.MAX_VALUE);
        for (int i = 0; i < requiredArgumentCount; i++)
        {
            decimalArgs[i] = (double) baseArguments[i] / (Integer.MAX_VALUE - 1);
        }


        TraitTags.addTraitTag(stack, poolSetName, pool, this, baseArguments);


        double averagedRoll = 0, rollUsageCount = 0;
        for (CRecalculableTraitElement element : elements)
        {
            for (int i = 0; i < element.requiredArgumentCount(); i++)
            {
                averagedRoll += element.ignoreMultipliers ? 1 : decimalArgs[i];
                rollUsageCount++;
            }
        }


        if (requiredArgumentCount == 0) return (minValue + maxValue) * 0.5 * itemTypeAndLevelMultiplier;

        averagedRoll /= rollUsageCount;
        return (minValue + (maxValue - minValue) * averagedRoll) * itemTypeAndLevelMultiplier;
    }


    public void applyToItem(ItemStack stack, double itemTypeAndLevelMultiplier, int... baseArguments)
    {
        int requiredArgumentCount = 0;
        for (CRecalculableTraitElement element : elements)
        {
            requiredArgumentCount = Tools.max(requiredArgumentCount, element.requiredArgumentCount());
        }
        if (baseArguments.length < requiredArgumentCount) throw new IllegalStateException("Tried to apply recalculable trait without enough arguments: " + name + " (" + baseArguments.length + "/" + requiredArgumentCount + ")");


        for (CRecalculableTraitElement element : elements) element.applyToItem(stack, baseArguments, itemTypeAndLevelMultiplier);
    }


    @Override
    public CRecalculableTrait write(ByteBuf buf)
    {
        super.write(buf);

        ByteBufUtils.writeUTF8String(buf, name);

        buf.writeBoolean(addToAssemblyFromPart);

        buf.writeInt(elements.size());
        for (CRecalculableTraitElement element : elements) writeMarked(buf, element);

        return this;
    }

    @Override
    public CRecalculableTrait read(ByteBuf buf)
    {
        super.read(buf);

        name = ByteBufUtils.readUTF8String(buf);

        addToAssemblyFromPart = buf.readBoolean();

        elements.clear();
        for (int i = buf.readInt(); i > 0; i--) elements.add((CRecalculableTraitElement) readMarked(buf));

        return this;
    }

    @Override
    public CRecalculableTrait save(OutputStream stream)
    {
        super.save(stream);

        new CStringUTF8().set(name).save(stream);

        new CBoolean().set(addToAssemblyFromPart).save(stream);

        new CInt().set(elements.size()).save(stream);
        for (CRecalculableTraitElement element : elements) saveMarked(stream, element);

        return this;
    }

    @Override
    public CRecalculableTrait load(InputStream stream)
    {
        super.load(stream);

        name = new CStringUTF8().load(stream).value;

        addToAssemblyFromPart = new CBoolean().load(stream).value;

        CInt ci = new CInt();

        elements.clear();
        for (int i = ci.load(stream).value; i > 0; i--) elements.add((CRecalculableTraitElement) loadMarked(stream));

        return this;
    }
}
