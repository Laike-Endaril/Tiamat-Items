package com.fantasticsource.tiamatitems.trait;

import com.fantasticsource.tiamatitems.TiamatItems;
import com.fantasticsource.tiamatitems.globalsettings.CRarity;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CDouble;
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
    public static final int ITEM_GEN_CODE_VERSION = 0;
    public static int itemGenConfigVersion = 0; //TODO handle data retention
    public static LinkedHashMap<String, CItemType> itemTypes = new LinkedHashMap<>(); //TODO handle data retention


    public String name, slotting;
    public double percentageMultiplier = 1, value;
    public ArrayList<CTrait> staticTraits = new ArrayList<>();
    public LinkedHashMap<String, ArrayList<CTraitGenPool>> randomTraitPoolSets = new LinkedHashMap<>(); //TODO disallow "Static" as a name during editing


    public ItemStack generateItem(int level, CRarity rarity)
    {
        ItemStack stack = new ItemStack(TiamatItems.tiamatItem);


        MiscTags.setItemGenVersion(stack, (((long) ITEM_GEN_CODE_VERSION) << 32) | itemGenConfigVersion);


        MiscTags.setItemType(stack, name);
        MiscTags.setItemLevel(stack, level);
        MiscTags.setItemLevelReq(stack, level);
        MiscTags.setItemRarity(stack, rarity);
        MiscTags.setItemSlotting(stack, slotting);


        //Static traits
        double genLevel = rarity.itemLevelModifier + level;
        double totalValue = value;
        for (CTrait trait : staticTraits)
        {
            totalValue += trait.applyToItem(stack, "Static", this, genLevel, null);
        }


        //Trait pools
        for (Map.Entry<String, Integer> poolSet : rarity.traitCounts.entrySet())
        {
            int rollCount = poolSet.getValue();
            if (rollCount <= 0) continue;


            String poolSetName = poolSet.getKey();
            ArrayList<CTraitGenPool> randomTraitPools = randomTraitPoolSets.get(poolSetName);
            if (randomTraitPools == null) continue;


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


            for (int i = rollCount; i > 0; i--)
            {
                if (genPools.size() == 0) break;

                CTraitGenPool pool = Tools.choose(genPools);
                ArrayList<CTrait> list = traitPools.get(pool);
                CTrait trait = Tools.choose(list);

                totalValue += trait.applyToItem(stack, poolSetName, this, genLevel, pool);

                while (list.remove(trait))
                {
                    genPools.remove(pool);
                }
            }
        }


        //Value
        MiscTags.setItemValue(stack, (int) totalValue);


        //Name
        //TODO Generate name


        return stack;
    }


    @Override
    public CItemType write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);
        ByteBufUtils.writeUTF8String(buf, slotting);
        buf.writeDouble(percentageMultiplier);
        buf.writeDouble(value);

        buf.writeInt(staticTraits.size());
        for (CTrait gen : staticTraits) gen.write(buf);

        buf.writeInt(randomTraitPoolSets.size());
        for (Map.Entry<String, ArrayList<CTraitGenPool>> entry : randomTraitPoolSets.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());

            ArrayList<CTraitGenPool> poolSet = entry.getValue();
            buf.writeInt(poolSet.size());
            for (CTraitGenPool pool : poolSet) pool.write(buf);
        }

        return this;
    }

    @Override
    public CItemType read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);
        slotting = ByteBufUtils.readUTF8String(buf);
        percentageMultiplier = buf.readDouble();
        value = buf.readDouble();

        staticTraits.clear();
        for (int i = buf.readInt(); i > 0; i--) staticTraits.add(new CTrait().read(buf));

        randomTraitPoolSets.clear();
        for (int i = buf.readInt(); i > 0; i--)
        {
            ArrayList<CTraitGenPool> poolSet = new ArrayList<>();
            randomTraitPoolSets.put(ByteBufUtils.readUTF8String(buf), poolSet);

            for (int i2 = buf.readInt(); i2 > 0; i2--)
            {
                poolSet.add(new CTraitGenPool().read(buf));
            }
        }

        return this;
    }

    @Override
    public CItemType save(OutputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8().set(name).save(stream).set(slotting).save(stream);
        new CDouble().set(percentageMultiplier).save(stream).set(value).save(stream);

        CInt ci = new CInt().set(staticTraits.size()).save(stream);
        for (CTrait gen : staticTraits) gen.save(stream);

        ci.set(randomTraitPoolSets.size()).save(stream);
        for (Map.Entry<String, ArrayList<CTraitGenPool>> entry : randomTraitPoolSets.entrySet())
        {
            cs.set(entry.getKey()).save(stream);

            ArrayList<CTraitGenPool> poolSet = entry.getValue();
            ci.set(poolSet.size()).save(stream);
            for (CTraitGenPool pool : poolSet) pool.save(stream);
        }

        return this;
    }

    @Override
    public CItemType load(InputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8();
        CDouble cd = new CDouble();
        name = cs.load(stream).value;
        slotting = cs.load(stream).value;
        percentageMultiplier = cd.load(stream).value;
        value = cd.load(stream).value;

        CInt ci = new CInt();
        staticTraits.clear();
        for (int i = ci.load(stream).value; i > 0; i--) staticTraits.add(new CTrait().load(stream));

        randomTraitPoolSets.clear();
        for (int i = ci.load(stream).value; i > 0; i--)
        {
            ArrayList<CTraitGenPool> poolSet = new ArrayList<>();
            randomTraitPoolSets.put(cs.load(stream).value, poolSet);

            for (int i2 = ci.load(stream).value; i2 > 0; i2--)
            {
                poolSet.add(new CTraitGenPool().load(stream));
            }
        }

        return this;
    }
}
