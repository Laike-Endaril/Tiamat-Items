package com.fantasticsource.tiamatitems.traitgen;

import com.fantasticsource.tiamatactions.action.CAction;
import com.fantasticsource.tiamatitems.TiamatItems;
import com.fantasticsource.tiamatitems.globalsettings.CRarity;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
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

public class CItemType extends Component
{
    //TODO change version on item gen definition change, including globals
    public static int itemGenVersion = 0; //TODO handle data retention
    public static LinkedHashMap<String, CItemType> itemTypes = new LinkedHashMap<>(); //TODO handle data retention


    public String name, slotting;
    public int percentageMultiplier = 1;
    public ArrayList<CTrait> staticTraits = new ArrayList<>();
    public ArrayList<CTraitGenPool> randomTraitPools = new ArrayList<>();
    public LinkedHashMap<CAction, Integer> mainActionPool = new LinkedHashMap<>(), subActionPool = new LinkedHashMap<>();


    public ItemStack generateItem(int level, CRarity rarity)
    {
        ItemStack stack = new ItemStack(TiamatItems.tiamatItem);

        MiscTags.setItemGenVersion(stack, itemGenVersion);

        MiscTags.setItemLevel(stack, level);
        MiscTags.setItemLevelReq(stack, level);
        MiscTags.setItemRarity(stack, rarity);

        MiscTags.setItemSlotting(stack, slotting);

        double genLevel = rarity.itemLevelModifier + level;
        for (CTrait traitGen : staticTraits) traitGen.applyToItem(stack, this, genLevel, null);


        ArrayList<CTraitGenPool> genPools = new ArrayList<>();
        LinkedHashMap<CTraitGenPool, ArrayList<CTrait>> traitPools = new LinkedHashMap<>();
        for (CTraitGenPool pool : randomTraitPools)
        {
            ArrayList<CTrait> traits = new ArrayList<>();

            for (Map.Entry<CTrait, Integer> entry : pool.traitGenWeights.entrySet())
            {
                for (int i = entry.getValue(); i > 0; i--)
                {
                    traits.add(entry.getKey());
                    genPools.add(pool);
                }
            }

            traitPools.put(pool, traits);
        }

        for (int i = rarity.traitCount; i > 0; i--)
        {
            if (genPools.size() == 0) break;

            CTraitGenPool pool = Tools.choose(genPools);
            ArrayList<CTrait> list = traitPools.get(pool);
            CTrait trait = Tools.choose(list);

            trait.applyToItem(stack, this, genLevel, pool);

            while (list.remove(trait))
            {
                genPools.remove(pool);
            }
        }


        //TODO Generate actions

        //TODO Generate name

        return stack;
    }


    @Override
    public CItemType write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);
        ByteBufUtils.writeUTF8String(buf, slotting);
        buf.writeInt(percentageMultiplier);

        buf.writeInt(staticTraits.size());
        for (CTrait gen : staticTraits) gen.write(buf);

        buf.writeInt(randomTraitPools.size());
        for (CTraitGenPool pool : randomTraitPools) pool.write(buf);

        buf.writeInt(mainActionPool.size());
        for (Map.Entry<CAction, Integer> entry : mainActionPool.entrySet())
        {
            entry.getKey().write(buf);
            buf.writeInt(entry.getValue());
        }

        buf.writeInt(subActionPool.size());
        for (Map.Entry<CAction, Integer> entry : subActionPool.entrySet())
        {
            entry.getKey().write(buf);
            buf.writeInt(entry.getValue());
        }

        return this;
    }

    @Override
    public CItemType read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);
        slotting = ByteBufUtils.readUTF8String(buf);
        percentageMultiplier = buf.readInt();

        staticTraits.clear();
        for (int i = buf.readInt(); i > 0; i--) staticTraits.add(new CTrait().read(buf));

        randomTraitPools.clear();
        for (int i = buf.readInt(); i > 0; i--) randomTraitPools.add(new CTraitGenPool().read(buf));

        mainActionPool.clear();
        for (int i = buf.readInt(); i > 0; i--)
        {
            mainActionPool.put(new CAction().read(buf), buf.readInt());
        }

        subActionPool.clear();
        for (int i = buf.readInt(); i > 0; i--)
        {
            subActionPool.put(new CAction().read(buf), buf.readInt());
        }

        return this;
    }

    @Override
    public CItemType save(OutputStream stream)
    {
        new CStringUTF8().set(name).save(stream).set(slotting).save(stream);

        CInt ci = new CInt().set(percentageMultiplier).save(stream).set(staticTraits.size()).save(stream);
        for (CTrait gen : staticTraits) gen.save(stream);

        ci.set(randomTraitPools.size()).save(stream);
        for (CTraitGenPool pool : randomTraitPools) pool.save(stream);

        ci.set(mainActionPool.size()).save(stream);
        for (Map.Entry<CAction, Integer> entry : mainActionPool.entrySet())
        {
            entry.getKey().save(stream);
            ci.set(entry.getValue()).save(stream);
        }

        ci.set(subActionPool.size()).save(stream);
        for (Map.Entry<CAction, Integer> entry : subActionPool.entrySet())
        {
            entry.getKey().save(stream);
            ci.set(entry.getValue()).save(stream);
        }

        return this;
    }

    @Override
    public CItemType load(InputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8();
        name = cs.load(stream).value;
        slotting = cs.load(stream).value;

        CInt ci = new CInt();
        percentageMultiplier = ci.load(stream).value;

        staticTraits.clear();
        for (int i = ci.load(stream).value; i > 0; i--) staticTraits.add(new CTrait().load(stream));

        randomTraitPools.clear();
        for (int i = ci.load(stream).value; i > 0; i--) randomTraitPools.add(new CTraitGenPool().load(stream));

        mainActionPool.clear();
        for (int i = ci.load(stream).value; i > 0; i--)
        {
            mainActionPool.put(new CAction().load(stream), ci.load(stream).value);
        }

        subActionPool.clear();
        for (int i = ci.load(stream).value; i > 0; i--)
        {
            subActionPool.put(new CAction().load(stream), ci.load(stream).value);
        }

        return this;
    }
}
