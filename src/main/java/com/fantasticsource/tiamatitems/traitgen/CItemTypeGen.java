package com.fantasticsource.tiamatitems.traitgen;

import com.fantasticsource.tiamatitems.TiamatItems;
import com.fantasticsource.tiamatitems.globalsettings.CRarity;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
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

public class CItemTypeGen extends Component
{
    //TODO change version on item gen definition change, including globals
    //TODO config for
    public static int itemGenVersion = 0; //TODO handle data retention
    public static LinkedHashMap<String, CItemTypeGen> itemGenerators = new LinkedHashMap<>(); //TODO handle data retention


    public String name, slotting;
    public int percentageMultiplier = 1;
    public ArrayList<CTraitGen> staticTraits = new ArrayList<>();
    public ArrayList<CTraitGenPool> randomTraitPools = new ArrayList<>();


    public static ItemStack generateItem(String itemType, int level, String rarity) //TODO call this from a command
    {
        CItemTypeGen gen = itemGenerators.get(itemType);
        CRarity cRarity = CRarity.rarities.get(rarity);
        if (gen == null || cRarity == null) return ItemStack.EMPTY;
        return gen.generateItem(level, cRarity);
    }


    public ItemStack generateItem(int level, CRarity rarity)
    {
        ItemStack stack = new ItemStack(TiamatItems.tiamatItem);

        MiscTags.setItemGenVersion(stack, itemGenVersion);

        MiscTags.setItemLevel(stack, level);
        MiscTags.setItemLevelReq(stack, level);
        MiscTags.setItemRarity(stack, rarity);

        MiscTags.setItemSlotting(stack, slotting);

        double genLevel = rarity.itemLevelModifier + level;
        for (CTraitGen traitGen : staticTraits) traitGen.applyToItem(stack, this, genLevel);
        CTraitGenPool combinedPool = CTraitGenPool.getCombinedPool(randomTraitPools.toArray(new CTraitGenPool[0]));
        combinedPool.applyToItem(stack, this, rarity.traitCount, genLevel);

        return stack;
    }


    @Override
    public CItemTypeGen write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);
        ByteBufUtils.writeUTF8String(buf, slotting);
        buf.writeInt(percentageMultiplier);

        buf.writeInt(staticTraits.size());
        for (CTraitGen gen : staticTraits) gen.write(buf);

        buf.writeInt(randomTraitPools.size());
        for (CTraitGenPool pool : randomTraitPools) pool.write(buf);

        return this;
    }

    @Override
    public CItemTypeGen read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);
        slotting = ByteBufUtils.readUTF8String(buf);
        percentageMultiplier = buf.readInt();

        staticTraits.clear();
        for (int i = buf.readInt(); i > 0; i--) staticTraits.add(new CTraitGen().read(buf));

        randomTraitPools.clear();
        for (int i = buf.readInt(); i > 0; i--) randomTraitPools.add(new CTraitGenPool().read(buf));

        return this;
    }

    @Override
    public CItemTypeGen save(OutputStream stream)
    {
        new CStringUTF8().set(name).save(stream).set(slotting).save(stream);

        CInt ci = new CInt().set(percentageMultiplier).save(stream).set(staticTraits.size()).save(stream);
        for (CTraitGen gen : staticTraits) gen.save(stream);

        ci.set(randomTraitPools.size()).save(stream);
        for (CTraitGenPool pool : randomTraitPools) pool.save(stream);

        return this;
    }

    @Override
    public CItemTypeGen load(InputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8();
        name = cs.load(stream).value;
        slotting = cs.load(stream).value;

        CInt ci = new CInt();
        percentageMultiplier = ci.load(stream).value;

        staticTraits.clear();
        for (int i = ci.load(stream).value; i > 0; i--) staticTraits.add(new CTraitGen().load(stream));

        randomTraitPools.clear();
        for (int i = ci.load(stream).value; i > 0; i--) randomTraitPools.add(new CTraitGenPool().load(stream));

        return this;
    }
}
