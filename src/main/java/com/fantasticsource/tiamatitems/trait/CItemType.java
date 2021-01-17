package com.fantasticsource.tiamatitems.trait;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Slottings;
import com.fantasticsource.tiamatitems.TiamatItems;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.nbt.TraitTags;
import com.fantasticsource.tiamatitems.settings.CRarity;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTrait;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitPool;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTrait;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTraitPool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class CItemType extends Component
{
    public String name = "", slotting = "None";
    public double traitLevelMultiplier = 1, value = 0;
    public LinkedHashMap<String, CRecalculableTrait> staticRecalculableTraits = new LinkedHashMap<>();
    public LinkedHashMap<String, CUnrecalculableTrait> staticUnrecalculableTraits = new LinkedHashMap<>();
    public LinkedHashMap<String, LinkedHashSet<String>> randomRecalculableTraitPoolSets = new LinkedHashMap<>();
    public LinkedHashMap<String, LinkedHashSet<String>> randomUnrecalculableTraitPoolSets = new LinkedHashMap<>();


    public ItemStack generateItem(int level, CRarity rarity)
    {
        if (!MCTools.hosting()) throw new IllegalStateException("This method should not be run without a server running!");

        return applyItemType(new ItemStack(TiamatItems.tiamatItem), level, rarity);
    }


    public ItemStack applyItemType(ItemStack stack, int level, CRarity rarity)
    {
        if (!MCTools.hosting()) throw new IllegalStateException("This method should not be run without a server running!");


        //See whether this is the first time we're generating or not
        boolean firstGeneration = MiscTags.getItemGenVersion(stack) == -1;

        //Ensure we're working with a clean core
        ItemStack core = AssemblyTags.getInternalCore(stack);
        if (!core.isEmpty()) stack = core;


        //Apply and/or overwrite most main data
        MiscTags.setItemGenVersion(stack, CSettings.LOCAL_SETTINGS.getVersion());

        MiscTags.setItemType(stack, name);
        MiscTags.setItemLevel(stack, level);
        MiscTags.setItemLevelReq(stack, level);
        MiscTags.setItemRarity(stack, rarity);
        Slottings.setItemSlotting(stack, slotting);


        //Prep generation vars
        double itemTypeAndLevelMultiplier = traitLevelMultiplier * (CSettings.LOCAL_SETTINGS.baseMultiplier + (CSettings.LOCAL_SETTINGS.multiplierBonusPerLevel * rarity.itemLevelModifier + level));
        double totalValue = value + MiscTags.getItemValueMod(stack);


        //Grab trait NBT, then clear it from item
        ArrayList<String> traitStrings = TraitTags.getTraitStrings(stack);
        TraitTags.clearTraitTags(stack);


        //Generate NBT and value for static recalculable traits
        for (CRecalculableTrait trait : staticRecalculableTraits.values())
        {
            boolean done = false;
            for (String traitString : traitStrings.toArray(new String[0]))
            {
                String[] tokens = Tools.fixedSplit(traitString, ":");
                if (!tokens[0].equals("Static")) continue;

                if (tokens[1].equals(trait.name))
                {
                    int[] baseArgs = new int[tokens.length - 2];
                    for (int i = 0; i < baseArgs.length; i++) baseArgs[i] = Integer.parseInt(tokens[i + 2]);

                    totalValue += trait.generateNBT(stack, "Static", null, itemTypeAndLevelMultiplier, baseArgs);
                    traitStrings.remove(traitString);
                    done = true;
                    break;
                }
            }
            if (!done) totalValue += trait.generateNBT(stack, "Static", null, itemTypeAndLevelMultiplier);
        }


        //Generate NBT and value for random recalculable traits
        for (Map.Entry<String, Integer> poolSetRollCountEntry : rarity.traitPoolSetRollCounts.entrySet())
        {
            int rollCount = poolSetRollCountEntry.getValue();
            if (rollCount <= 0) continue;


            String poolSetName = poolSetRollCountEntry.getKey();
            LinkedHashSet<String> randomTraitPoolNames = randomRecalculableTraitPoolSets.get(poolSetName);
            if (randomTraitPoolNames == null) continue;


            ArrayList<CRecalculableTraitPool> weightedPools = new ArrayList<>();
            LinkedHashMap<CRecalculableTraitPool, ArrayList<CRecalculableTrait>> traitPools = new LinkedHashMap<>();
            for (String poolName : randomTraitPoolNames)
            {
                CRecalculableTraitPool pool = CSettings.LOCAL_SETTINGS.recalcTraitPools.get(poolName);
                if (pool == null)
                {
                    System.err.println(TextFormatting.RED + "Could not find recalculable pool: " + poolName);
                    System.err.println(TextFormatting.RED + "Attempted from recalculable pool set: " + poolSetName);
                    System.err.println(TextFormatting.RED + "In item type: " + name);
                    System.err.println(TextFormatting.RED + "With rarity: " + rarity.name);
                    continue;
                }

                ArrayList<CRecalculableTrait> traits = new ArrayList<>();

                for (Map.Entry<CRecalculableTrait, Integer> entry : pool.traitGenWeights.entrySet())
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
                    if (tokens[0].equals("Static")) continue;

                    String poolSetName2 = tokens[0];
                    if (!poolSetName2.equals(poolSetName)) continue;

                    CRecalculableTraitPool pool = CSettings.LOCAL_SETTINGS.recalcTraitPools.get(tokens[1]);
                    if (pool == null || !weightedPools.contains(pool)) continue;

                    ArrayList<CRecalculableTrait> list = traitPools.get(pool);
                    CRecalculableTrait trait = null;
                    for (CRecalculableTrait trait2 : pool.traitGenWeights.keySet())
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


                    int[] baseArgs = new int[tokens.length - 3];
                    for (int i2 = 0; i2 < baseArgs.length; i2++) baseArgs[i2] = Integer.parseInt(tokens[i2 + 3]);

                    totalValue += trait.generateNBT(stack, poolSetName, pool, itemTypeAndLevelMultiplier, baseArgs);
                    traitStrings.remove(traitString);
                    done = true;
                    break;
                }

                if (!done)
                {
                    CRecalculableTraitPool pool = Tools.choose(weightedPools);
                    ArrayList<CRecalculableTrait> list = traitPools.get(pool);
                    CRecalculableTrait trait = Tools.choose(list);

                    totalValue += trait.generateNBT(stack, poolSetName, pool, itemTypeAndLevelMultiplier);

                    while (list.remove(trait))
                    {
                        weightedPools.remove(pool);
                    }
                }
            }
        }


        if (firstGeneration)
        {
            double valueMod = 0;
            //Apply and generate value for static unrecalculable traits
            for (CUnrecalculableTrait trait : staticUnrecalculableTraits.values())
            {
                valueMod += trait.applyToItem(stack, itemTypeAndLevelMultiplier);
            }


            //Apply and generate value for random unrecalculable traits
            for (Map.Entry<String, Integer> poolSetRollCountEntry : rarity.traitPoolSetRollCounts.entrySet())
            {
                int rollCount = poolSetRollCountEntry.getValue();
                if (rollCount <= 0) continue;


                String poolSetName = poolSetRollCountEntry.getKey();
                LinkedHashSet<String> randomTraitPoolNames = randomUnrecalculableTraitPoolSets.get(poolSetName);
                if (randomTraitPoolNames == null) continue;


                ArrayList<CUnrecalculableTraitPool> weightedPools = new ArrayList<>();
                LinkedHashMap<CUnrecalculableTraitPool, ArrayList<CUnrecalculableTrait>> traitPools = new LinkedHashMap<>();
                for (String poolName : randomTraitPoolNames)
                {
                    CUnrecalculableTraitPool pool = CSettings.LOCAL_SETTINGS.unrecalcTraitPools.get(poolName);
                    if (pool == null)
                    {
                        System.err.println(TextFormatting.RED + "Could not find unrecalculable pool: " + poolName);
                        System.err.println(TextFormatting.RED + "Attempted from unrecalculable pool set: " + poolSetName);
                        System.err.println(TextFormatting.RED + "In item type: " + name);
                        System.err.println(TextFormatting.RED + "With rarity: " + rarity.name);
                        continue;
                    }

                    ArrayList<CUnrecalculableTrait> traits = new ArrayList<>();

                    for (Map.Entry<CUnrecalculableTrait, Integer> entry : pool.traitGenWeights.entrySet())
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

                    CUnrecalculableTraitPool pool = Tools.choose(weightedPools);
                    ArrayList<CUnrecalculableTrait> list = traitPools.get(pool);
                    CUnrecalculableTrait trait = Tools.choose(list);

                    valueMod += trait.applyToItem(stack, itemTypeAndLevelMultiplier);

                    while (list.remove(trait))
                    {
                        weightedPools.remove(pool);
                    }
                }
            }

            MiscTags.setItemValueMod(stack, (int) valueMod);
            totalValue += valueMod;
        }


        //Apply value
        MiscTags.setItemValue(stack, (int) totalValue);


        //Generate core name
        stack.setStackDisplayName(rarity.textColor + name);
        //TODO Generate core affixes


        //Save internal core
        AssemblyTags.saveInternalCore(stack);


        //Apply static and random recalculable traits
        for (String traitString : TraitTags.getTraitStrings(stack))
        {
            String[] tokens = Tools.fixedSplit(traitString, ":");
            if (tokens[0].equals("Static"))
            {
                CRecalculableTrait trait = staticRecalculableTraits.get(tokens[1]);

                int[] baseArgs = new int[tokens.length - 2];
                for (int i = 0; i < baseArgs.length; i++) baseArgs[i] = Integer.parseInt(tokens[i + 2]);

                trait.applyToItem(stack, itemTypeAndLevelMultiplier, baseArgs);
            }
            else
            {
                CRecalculableTrait trait = null;
                for (CRecalculableTrait trait2 : CSettings.LOCAL_SETTINGS.recalcTraitPools.get(tokens[1]).traitGenWeights.keySet())
                {
                    if (trait2.name.equals(tokens[2]))
                    {
                        trait = trait2;
                        break;
                    }
                }

                if (trait == null) continue;


                int[] baseArgs = new int[tokens.length - 3];
                for (int i = 0; i < baseArgs.length; i++) baseArgs[i] = Integer.parseInt(tokens[i + 3]);

                trait.applyToItem(stack, itemTypeAndLevelMultiplier, baseArgs);
            }
        }


        return stack;
    }


    @Override
    public CItemType write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);
        ByteBufUtils.writeUTF8String(buf, slotting);
        buf.writeDouble(traitLevelMultiplier);
        buf.writeDouble(value);

        buf.writeInt(staticRecalculableTraits.size());
        for (CRecalculableTrait trait : staticRecalculableTraits.values()) trait.write(buf);

        buf.writeInt(staticUnrecalculableTraits.size());
        for (CUnrecalculableTrait trait : staticUnrecalculableTraits.values()) trait.write(buf);

        buf.writeInt(randomRecalculableTraitPoolSets.size());
        for (Map.Entry<String, LinkedHashSet<String>> entry : randomRecalculableTraitPoolSets.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());

            LinkedHashSet<String> poolSet = entry.getValue();
            buf.writeInt(poolSet.size());
            for (String poolName : poolSet) ByteBufUtils.writeUTF8String(buf, poolName);
        }

        buf.writeInt(randomUnrecalculableTraitPoolSets.size());
        for (Map.Entry<String, LinkedHashSet<String>> entry : randomUnrecalculableTraitPoolSets.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());

            LinkedHashSet<String> poolSet = entry.getValue();
            buf.writeInt(poolSet.size());
            for (String poolName : poolSet) ByteBufUtils.writeUTF8String(buf, poolName);
        }

        return this;
    }

    @Override
    public CItemType read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);
        slotting = ByteBufUtils.readUTF8String(buf);
        traitLevelMultiplier = buf.readDouble();
        value = buf.readDouble();

        staticRecalculableTraits.clear();
        CRecalculableTrait rTrait;
        for (int i = buf.readInt(); i > 0; i--)
        {
            rTrait = new CRecalculableTrait().read(buf);
            staticRecalculableTraits.put(rTrait.name, rTrait);
        }

        staticUnrecalculableTraits.clear();
        CUnrecalculableTrait uTrait;
        for (int i = buf.readInt(); i > 0; i--)
        {
            uTrait = new CUnrecalculableTrait().read(buf);
            staticUnrecalculableTraits.put(uTrait.name, uTrait);
        }

        randomRecalculableTraitPoolSets.clear();
        for (int i = buf.readInt(); i > 0; i--)
        {
            LinkedHashSet<String> poolSet = new LinkedHashSet<>();
            randomRecalculableTraitPoolSets.put(ByteBufUtils.readUTF8String(buf), poolSet);

            for (int i2 = buf.readInt(); i2 > 0; i2--)
            {
                poolSet.add(ByteBufUtils.readUTF8String(buf));
            }
        }

        randomUnrecalculableTraitPoolSets.clear();
        for (int i = buf.readInt(); i > 0; i--)
        {
            LinkedHashSet<String> poolSet = new LinkedHashSet<>();
            randomUnrecalculableTraitPoolSets.put(ByteBufUtils.readUTF8String(buf), poolSet);

            for (int i2 = buf.readInt(); i2 > 0; i2--)
            {
                poolSet.add(ByteBufUtils.readUTF8String(buf));
            }
        }

        return this;
    }

    @Override
    public CItemType save(OutputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8().set(name).save(stream).set(slotting).save(stream);
        new CDouble().set(traitLevelMultiplier).save(stream).set(value).save(stream);

        CInt ci = new CInt().set(staticRecalculableTraits.size()).save(stream);
        for (CRecalculableTrait trait : staticRecalculableTraits.values()) trait.save(stream);

        ci.set(staticUnrecalculableTraits.size()).save(stream);
        for (CUnrecalculableTrait trait : staticUnrecalculableTraits.values()) trait.save(stream);

        ci.set(randomRecalculableTraitPoolSets.size()).save(stream);
        for (Map.Entry<String, LinkedHashSet<String>> entry : randomRecalculableTraitPoolSets.entrySet())
        {
            cs.set(entry.getKey()).save(stream);

            LinkedHashSet<String> poolSet = entry.getValue();
            ci.set(poolSet.size()).save(stream);
            for (String poolName : poolSet) cs.set(poolName).save(stream);
        }

        ci.set(randomUnrecalculableTraitPoolSets.size()).save(stream);
        for (Map.Entry<String, LinkedHashSet<String>> entry : randomUnrecalculableTraitPoolSets.entrySet())
        {
            cs.set(entry.getKey()).save(stream);

            LinkedHashSet<String> poolSet = entry.getValue();
            ci.set(poolSet.size()).save(stream);
            for (String poolName : poolSet) cs.set(poolName).save(stream);
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
        traitLevelMultiplier = cd.load(stream).value;
        value = cd.load(stream).value;

        CInt ci = new CInt();
        staticRecalculableTraits.clear();
        CRecalculableTrait rTrait;
        for (int i = ci.load(stream).value; i > 0; i--)
        {
            rTrait = new CRecalculableTrait().load(stream);
            staticRecalculableTraits.put(rTrait.name, rTrait);
        }

        staticUnrecalculableTraits.clear();
        CUnrecalculableTrait uTrait;
        for (int i = ci.load(stream).value; i > 0; i--)
        {
            uTrait = new CUnrecalculableTrait().load(stream);
            staticUnrecalculableTraits.put(uTrait.name, uTrait);
        }

        randomRecalculableTraitPoolSets.clear();
        for (int i = ci.load(stream).value; i > 0; i--)
        {
            LinkedHashSet<String> poolSet = new LinkedHashSet<>();
            randomRecalculableTraitPoolSets.put(cs.load(stream).value, poolSet);

            for (int i2 = ci.load(stream).value; i2 > 0; i2--)
            {
                poolSet.add(cs.load(stream).value);
            }
        }

        randomUnrecalculableTraitPoolSets.clear();
        for (int i = ci.load(stream).value; i > 0; i--)
        {
            LinkedHashSet<String> poolSet = new LinkedHashSet<>();
            randomUnrecalculableTraitPoolSets.put(cs.load(stream).value, poolSet);

            for (int i2 = ci.load(stream).value; i2 > 0; i2--)
            {
                poolSet.add(cs.load(stream).value);
            }
        }

        return this;
    }
}
