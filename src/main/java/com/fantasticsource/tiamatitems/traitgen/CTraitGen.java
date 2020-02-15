package com.fantasticsource.tiamatitems.traitgen;

import com.fantasticsource.tools.component.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class CTraitGen extends Component
{
    public String name;
    public boolean isGood = true; //Whether it's a good thing to have more of this trait
    public HashSet<CTraitGenElement> elements = new HashSet<>();
    public LinkedHashMap<Double, String> prefixes = new LinkedHashMap<>(), suffixes = new LinkedHashMap<>(); //% of possible roll -> prefix/suffix text


    public void applyToItem(ItemStack stack, CItemTypeGen itemTypeGen, double level)
    {
        double percentage = Math.random() * itemTypeGen.percentageMultiplier;
        for (CTraitGenElement element : elements) element.applyToItem(stack, level, percentage);
    }


    @Override
    public CTraitGen write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeBoolean(isGood);

        buf.writeInt(elements.size());
        for (CTraitGenElement element : elements) writeMarked(buf, element);

        buf.writeInt(prefixes.size());
        for (Map.Entry<Double, String> entry : prefixes.entrySet())
        {
            buf.writeDouble(entry.getKey());
            ByteBufUtils.writeUTF8String(buf, entry.getValue());
        }

        buf.writeInt(suffixes.size());
        for (Map.Entry<Double, String> entry : suffixes.entrySet())
        {
            buf.writeDouble(entry.getKey());
            ByteBufUtils.writeUTF8String(buf, entry.getValue());
        }

        return this;
    }

    @Override
    public CTraitGen read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);
        isGood = buf.readBoolean();

        elements.clear();
        for (int i = buf.readInt(); i > 0; i--) elements.add((CTraitGenElement) readMarked(buf));

        prefixes.clear();
        for (int i = buf.readInt(); i > 0; i--) prefixes.put(buf.readDouble(), ByteBufUtils.readUTF8String(buf));

        suffixes.clear();
        for (int i = buf.readInt(); i > 0; i--) suffixes.put(buf.readDouble(), ByteBufUtils.readUTF8String(buf));

        return this;
    }

    @Override
    public CTraitGen save(OutputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8().set(name).save(stream);
        new CBoolean().set(isGood).save(stream);

        CInt ci = new CInt().set(elements.size()).save(stream);
        for (CTraitGenElement element : elements) saveMarked(stream, element);

        CDouble cd = new CDouble();

        ci.set(prefixes.size()).save(stream);
        for (Map.Entry<Double, String> entry : prefixes.entrySet())
        {
            cd.set(entry.getKey()).save(stream);
            cs.set(entry.getValue()).save(stream);
        }

        ci.set(suffixes.size()).save(stream);
        for (Map.Entry<Double, String> entry : suffixes.entrySet())
        {
            cd.set(entry.getKey()).save(stream);
            cs.set(entry.getValue()).save(stream);
        }

        return this;
    }

    @Override
    public CTraitGen load(InputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8();
        CInt ci = new CInt();
        CDouble cd = new CDouble();

        name = cs.load(stream).value;
        isGood = new CBoolean().load(stream).value;

        elements.clear();
        for (int i = ci.load(stream).value; i > 0; i--) elements.add((CTraitGenElement) loadMarked(stream));

        prefixes.clear();
        for (int i = ci.load(stream).value; i > 0; i--) prefixes.put(cd.load(stream).value, cs.load(stream).value);

        suffixes.clear();
        for (int i = ci.load(stream).value; i > 0; i--) suffixes.put(cd.load(stream).value, cs.load(stream).value);

        return this;
    }
}
