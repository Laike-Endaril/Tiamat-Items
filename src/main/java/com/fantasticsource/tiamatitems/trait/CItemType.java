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
    public static final int ITEM_GEN_CODE_VERSION = 0;
    private static int itemGenConfigVersion = 0; //TODO handle data retention
    public static LinkedHashMap<String, CItemType> itemTypes = new LinkedHashMap<>(); //TODO handle data retention


    public String name = "", slotting = "None";
    public double percentageMultiplier = 1, value;
    public LinkedHashMap<String, CTrait> staticTraits = new LinkedHashMap<>();
    public LinkedHashMap<String, LinkedHashMap<String, CTraitPool>> randomTraitPoolSets = new LinkedHashMap<>(); //TODO disallow "Static" as a name during editing


    //TODO call this method on item gen definition change, including globals
    public void incrementVersion()
    {
        itemGenConfigVersion++;
        //TODO sync to connected clients
    }

    public static long getVersion()
    {
        return (((long) ITEM_GEN_CODE_VERSION) << 32) | itemGenConfigVersion;
    }


    public ItemStack generateItem(int level, CRarity rarity)
    {
        return generateItem(level, rarity, new ArrayList<>());
    }

    public ItemStack generateItem(int level, CRarity rarity, ArrayList<String> traitStrings)
    {
        ItemStack stack = new ItemStack(TiamatItems.tiamatItem);


        MiscTags.setItemGenVersion(stack, getVersion());


        MiscTags.setItemType(stack, name);
        MiscTags.setItemLevel(stack, level);
        MiscTags.setItemLevelReq(stack, level);
        MiscTags.setItemRarity(stack, rarity);
        MiscTags.setItemSlotting(stack, slotting);


        //Static traits
        double genLevel = rarity.itemLevelModifier + level;
        double totalValue = value;
        for (CTrait trait : staticTraits.values())
        {
            boolean done = false;
            for (String traitString : traitStrings.toArray(new String[0]))
            {
                String[] tokens = Tools.fixedSplit(traitString, ":");
                if (tokens.length != 3) continue;

                if (tokens[1].equals(trait.name))
                {
                    totalValue += trait.applyToItem(stack, "Static", this, genLevel, null, Integer.parseInt(tokens[2]));
                    traitStrings.remove(traitString);
                    done = true;
                    break;
                }
            }
            if (!done) totalValue += trait.applyToItem(stack, "Static", this, genLevel, null);
        }


        //Trait pools
        for (Map.Entry<String, Integer> poolSetRollCountEntry : rarity.traitCounts.entrySet())
        {
            int rollCount = poolSetRollCountEntry.getValue();
            if (rollCount <= 0) continue;


            String poolSetName = poolSetRollCountEntry.getKey();
            LinkedHashMap<String, CTraitPool> randomTraitPools = randomTraitPoolSets.get(poolSetName);
            if (randomTraitPools == null) continue;


            ArrayList<CTraitPool> weightedPools = new ArrayList<>();
            LinkedHashMap<CTraitPool, ArrayList<CTrait>> traitPools = new LinkedHashMap<>();
            for (CTraitPool pool : randomTraitPools.values())
            {
                ArrayList<CTrait> traits = new ArrayList<>();

                for (Map.Entry<CTrait, Integer> entry : pool.traitGenWeights.entrySet())
                {
                    for (int i = entry.getValue(); i > 0; i--)
                    {
                        traits.add(entry.getKey());
                        weightedPools.add(pool);
                    }
                }

                traitPools.put(pool, traits);
            }


            for (int i = rollCount; i > 0; i--)
            {
                if (weightedPools.size() == 0) break;

                boolean done = false;
                for (String traitString : traitStrings.toArray(new String[0]))
                {
                    String[] tokens = Tools.fixedSplit(traitString, ":");
                    if (tokens.length != 4) continue;

                    String poolSetName2 = tokens[0];
                    if (!poolSetName2.equals(poolSetName)) continue;

                    CTraitPool pool = randomTraitPools.get(tokens[1]);
                    if (pool == null || !weightedPools.contains(pool)) continue;

                    ArrayList<CTrait> list = traitPools.get(pool);
                    CTrait trait = null;
                    for (CTrait trait2 : pool.traitGenWeights.keySet())
                    {
                        if (trait2.name.equals(tokens[2]))
                        {
                            trait = trait2;
                            break;
                        }
                    }
                    if (trait == null || !list.contains(trait)) continue;


                    while (list.remove(trait))
                    {
                        weightedPools.remove(pool);
                    }

                    totalValue += trait.applyToItem(stack, poolSetName, this, genLevel, pool, Integer.parseInt(tokens[3]));
                    traitStrings.remove(traitString);
                    done = true;
                    break;
                }

                if (!done)
                {
                    CTraitPool pool = Tools.choose(weightedPools);
                    ArrayList<CTrait> list = traitPools.get(pool);
                    CTrait trait = Tools.choose(list);

                    totalValue += trait.applyToItem(stack, poolSetName, this, genLevel, pool);

                    while (list.remove(trait))
                    {
                        weightedPools.remove(pool);
                    }
                }
            }
        }


        //Value
        MiscTags.setItemValue(stack, (int) totalValue);


        //Name
        stack.setStackDisplayName(rarity.textColor + name);
        //TODO Generate affixes


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
        for (CTrait gen : staticTraits.values()) gen.write(buf);

        buf.writeInt(randomTraitPoolSets.size());
        for (Map.Entry<String, LinkedHashMap<String, CTraitPool>> entry : randomTraitPoolSets.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());

            LinkedHashMap<String, CTraitPool> poolSet = entry.getValue();
            buf.writeInt(poolSet.size());
            for (CTraitPool pool : poolSet.values()) pool.write(buf);
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
        CTrait trait;
        for (int i = buf.readInt(); i > 0; i--)
        {
            trait = new CTrait().read(buf);
            staticTraits.put(trait.name, trait);
        }

        randomTraitPoolSets.clear();
        CTraitPool pool;
        for (int i = buf.readInt(); i > 0; i--)
        {
            LinkedHashMap<String, CTraitPool> poolSet = new LinkedHashMap<>();
            randomTraitPoolSets.put(ByteBufUtils.readUTF8String(buf), poolSet);

            for (int i2 = buf.readInt(); i2 > 0; i2--)
            {
                pool = new CTraitPool().read(buf);
                poolSet.put(pool.name, pool);
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
        for (CTrait gen : staticTraits.values()) gen.save(stream);

        ci.set(randomTraitPoolSets.size()).save(stream);
        for (Map.Entry<String, LinkedHashMap<String, CTraitPool>> entry : randomTraitPoolSets.entrySet())
        {
            cs.set(entry.getKey()).save(stream);

            LinkedHashMap<String, CTraitPool> poolSet = entry.getValue();
            ci.set(poolSet.size()).save(stream);
            for (CTraitPool pool : poolSet.values()) pool.save(stream);
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
        CTrait trait;
        for (int i = ci.load(stream).value; i > 0; i--)
        {
            trait = new CTrait().load(stream);
            staticTraits.put(trait.name, trait);
        }

        randomTraitPoolSets.clear();
        CTraitPool pool;
        for (int i = ci.load(stream).value; i > 0; i--)
        {
            LinkedHashMap<String, CTraitPool> poolSet = new LinkedHashMap<>();
            randomTraitPoolSets.put(cs.load(stream).value, poolSet);

            for (int i2 = ci.load(stream).value; i2 > 0; i2--)
            {
                pool = new CTraitPool().load(stream);
                poolSet.put(pool.name, pool);
            }
        }

        return this;
    }
}
