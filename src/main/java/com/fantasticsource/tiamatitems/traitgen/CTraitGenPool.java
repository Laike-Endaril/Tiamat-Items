package com.fantasticsource.tiamatitems.traitgen;

import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CTraitGenPool extends Component
{
    public static LinkedHashMap<String, CTraitGenPool> globalTraitGenPools = new LinkedHashMap<>(); //TODO handle data retention


    public String name;
    public LinkedHashMap<CTraitGen, Integer> traitGenWeights = new LinkedHashMap<>();


    public static CTraitGenPool getCombinedPool(CTraitGenPool... pools)
    {
        return getCombinedPool("Unnamed Combined Pool", pools);
    }

    public static CTraitGenPool getCombinedPool(String name, CTraitGenPool... pools)
    {
        CTraitGenPool result = new CTraitGenPool();
        result.name = name;

        for (CTraitGenPool pool : pools)
        {
            for (Map.Entry<CTraitGen, Integer> entry : pool.traitGenWeights.entrySet())
            {
                result.traitGenWeights.put(entry.getKey(), result.traitGenWeights.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }

        return result;
    }


    public void applyToItem(ItemStack stack, CItemTypeGen itemTypeGen, int rollCount, double level)
    {
        if (rollCount <= 0 || traitGenWeights.size() <= 0) return;


        if (rollCount >= traitGenWeights.size())
        {
            for (CTraitGen traitGen : traitGenWeights.keySet()) traitGen.applyToItem(stack, itemTypeGen, level);
        }
        else
        {
            ArrayList<CTraitGen> traitGenPool = new ArrayList<>();
            for (Map.Entry<CTraitGen, Integer> entry : traitGenWeights.entrySet())
            {
                for (int i = entry.getValue(); i > 0; i--) traitGenPool.add(entry.getKey());
            }


            for (int i = rollCount; i > 0; i--)
            {
                if (traitGenPool.size() == 0) return;

                CTraitGen traitGen = Tools.choose(traitGenPool);
                traitGen.applyToItem(stack, itemTypeGen, level);
                while (traitGenPool.remove(traitGen))
                {
                    //Removes all duplicate entries from current generation pool
                }
            }
        }
    }


    @Override
    public CTraitGenPool write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);

        buf.writeInt(traitGenWeights.size());
        for (Map.Entry<CTraitGen, Integer> entry : traitGenWeights.entrySet())
        {
            writeMarked(buf, entry.getKey());
            buf.writeInt(entry.getValue());
        }

        return this;
    }

    @Override
    public CTraitGenPool read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);

        traitGenWeights.clear();
        for (int i = buf.readInt(); i > 0; i--) traitGenWeights.put((CTraitGen) readMarked(buf), buf.readInt());

        return this;
    }

    @Override
    public CTraitGenPool save(OutputStream stream)
    {
        new CStringUTF8().set(name).save(stream);

        CInt ci = new CInt().set(traitGenWeights.size()).save(stream);
        for (Map.Entry<CTraitGen, Integer> entry : traitGenWeights.entrySet())
        {
            saveMarked(stream, entry.getKey());
            ci.set(entry.getValue()).save(stream);
        }

        return this;
    }

    @Override
    public CTraitGenPool load(InputStream stream)
    {
        name = new CStringUTF8().load(stream).value;

        CInt ci = new CInt();
        traitGenWeights.clear();
        for (int i = ci.load(stream).value; i > 0; i--) traitGenWeights.put((CTraitGen) loadMarked(stream), ci.load(stream).value);

        return this;
    }
}
