package com.fantasticsource.tiamatitems.traitgen;

import com.fantasticsource.tiamatitems.TiamatItems;
import com.fantasticsource.tiamatitems.globalsettings.CRarity;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
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
    public static int itemGenVersion = 0;
    public static LinkedHashMap<String, CItemTypeGen> itemGenerators = new LinkedHashMap<>(); //TODO handle data retention


    public String name, slotting;
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

        for (CTraitGen traitGen : staticTraits) traitGen.applyToItem(stack);
        CTraitGenPool combinedPool = CTraitGenPool.getCombinedPool(randomTraitPools.toArray(new CTraitGenPool[0]));
        combinedPool.applyToItem(stack, rarity.traitCount);

        return stack;
    }


    @Override
    public CItemTypeGen write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);

        return this;
    }

    @Override
    public CItemTypeGen read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);

        return this;
    }

    @Override
    public CItemTypeGen save(OutputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8().set(name).save(stream);

        return this;
    }

    @Override
    public CItemTypeGen load(InputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8();
        name = cs.load(stream).value;

        return this;
    }
}
