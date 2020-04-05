package com.fantasticsource.tiamatitems.globalsettings;

import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitPool;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTraitPool;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;

public class CGlobalSettings extends Component
{
    public static final int ITEM_GEN_CODE_VERSION = 0;


    public static int maxItemLevel = 20;

    public static double
            baseMultiplier = 1,
            multiplierBonusPerLevel = 1;

    public static LinkedHashMap<String, Double> attributeBalanceMultipliers = new LinkedHashMap<>();


    public static LinkedHashMap<String, CRecalculableTraitPool> recalcTraitPools = new LinkedHashMap<>(); //TODO handle data retention
    public static LinkedHashMap<String, CUnrecalculableTraitPool> unrecalcTraitPools = new LinkedHashMap<>(); //TODO handle data retention
    public static LinkedHashMap<String, CRarity> rarities = new LinkedHashMap<>(); //TODO handle data retention
    public static LinkedHashMap<String, CItemType> itemTypes = new LinkedHashMap<>(); //TODO handle data retention


    private static int itemGenConfigVersion = 0; //TODO handle data retention

    public static long getVersion()
    {
        return (((long) ITEM_GEN_CODE_VERSION) << 32) | itemGenConfigVersion;
    }

    //TODO call this method on item gen definition change, including globals
    public void updateVersion()
    {
        itemGenConfigVersion++;

        //TODO sync to connected clients
    }

    @Override
    public CGlobalSettings write(ByteBuf buf)
    {
        buf.writeDouble(baseMultiplier);
        buf.writeDouble(multiplierBonusPerLevel);

        return this;
    }

    @Override
    public CGlobalSettings read(ByteBuf buf)
    {
        baseMultiplier = buf.readDouble();
        multiplierBonusPerLevel = buf.readDouble();

        return this;
    }

    @Override
    public CGlobalSettings save(OutputStream stream)
    {
        new CDouble().set(baseMultiplier).save(stream).set(multiplierBonusPerLevel).save(stream);

        return this;
    }

    @Override
    public CGlobalSettings load(InputStream stream)
    {
        CDouble cd = new CDouble();
        baseMultiplier = cd.load(stream).value;
        multiplierBonusPerLevel = cd.load(stream).value;

        return this;
    }
}
