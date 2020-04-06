package com.fantasticsource.tiamatitems.settings;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitPool;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTraitPool;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class CSettings extends Component
{
    public static final String FILENAME = MODID + ".dat";
    public static final int ITEM_GEN_CODE_VERSION = 0;
    protected static int itemGenConfigVersion = 0;


    public static int maxItemLevel = 20;

    public static double
            baseMultiplier = 1,
            multiplierBonusPerLevel = 1;


    public static LinkedHashMap<String, CRecalculableTraitPool> recalcTraitPools = new LinkedHashMap<>();
    public static LinkedHashMap<String, CUnrecalculableTraitPool> unrecalcTraitPools = new LinkedHashMap<>();
    public static LinkedHashMap<String, CRarity> rarities = new LinkedHashMap<>();
    public static LinkedHashMap<String, CItemType> itemTypes = new LinkedHashMap<>();


    public static LinkedHashMap<String, Double> attributeBalanceMultipliers = new LinkedHashMap<>();


    public static long getVersion()
    {
        return (((long) ITEM_GEN_CODE_VERSION) << 32) | itemGenConfigVersion;
    }

    //TODO call this method on item gen definition change, including globals
    public static void updateVersionAndSave()
    {
        itemGenConfigVersion++;

        saveAll();
        //TODO sync to connected clients
    }


    protected static void saveAll()
    {
        System.out.println("Saving changes to Tiamat Items settings");
        File file = new File(MCTools.getWorldSaveDir(FMLCommonHandler.instance().getMinecraftServerInstance()) + FILENAME);
        System.out.println(TextFormatting.LIGHT_PURPLE + "Saving " + file.getAbsolutePath());
        if (file.isDirectory()) throw new IllegalStateException(TextFormatting.RED + MCTools.getWorldSaveDir(FMLCommonHandler.instance().getMinecraftServerInstance()) + FILENAME + " is a directory instead of a file!");
        else while (file.exists()) file.delete();

        try
        {
            FileOutputStream stream = new FileOutputStream(file);
            new CSettings().save(stream);
            stream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void loadAll(FMLServerStartingEvent event) throws IOException
    {
        File file = new File(MCTools.getWorldSaveDir(event.getServer()) + FILENAME);
        System.out.println(TextFormatting.LIGHT_PURPLE + "Loading " + file.getAbsolutePath());
        if (file.isDirectory()) throw new IllegalStateException(TextFormatting.RED + MCTools.getWorldSaveDir(event.getServer()) + FILENAME + " is a directory instead of a file!");
        if (!file.exists()) return;

        FileInputStream stream = new FileInputStream(file);
        new CSettings().load(stream);
        stream.close();
    }


    @Override
    public CSettings write(ByteBuf buf)
    {
        buf.writeInt(itemGenConfigVersion);


        buf.writeInt(maxItemLevel);

        buf.writeDouble(baseMultiplier);
        buf.writeDouble(multiplierBonusPerLevel);


        buf.writeInt(recalcTraitPools.size());
        for (Map.Entry<String, CRecalculableTraitPool> entry : recalcTraitPools.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            entry.getValue().write(buf);
        }

        buf.writeInt(unrecalcTraitPools.size());
        for (Map.Entry<String, CUnrecalculableTraitPool> entry : unrecalcTraitPools.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            entry.getValue().write(buf);
        }

        buf.writeInt(rarities.size());
        for (Map.Entry<String, CRarity> entry : rarities.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            entry.getValue().write(buf);
        }

        buf.writeInt(itemTypes.size());
        for (Map.Entry<String, CItemType> entry : itemTypes.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            entry.getValue().write(buf);
        }


        buf.writeInt(attributeBalanceMultipliers.size());
        for (Map.Entry<String, Double> entry : attributeBalanceMultipliers.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            buf.writeDouble(entry.getValue());
        }

        return this;
    }

    @Override
    public CSettings read(ByteBuf buf)
    {
        itemGenConfigVersion = buf.readInt();


        (maxItemLevel) = buf.readInt();

        baseMultiplier = buf.readDouble();
        multiplierBonusPerLevel = buf.readDouble();


        recalcTraitPools.clear();
        for (int i = buf.readInt(); i > 0; i--)
        {
            recalcTraitPools.put(ByteBufUtils.readUTF8String(buf), new CRecalculableTraitPool().read(buf));
        }

        unrecalcTraitPools.clear();
        for (int i = buf.readInt(); i > 0; i--)
        {
            unrecalcTraitPools.put(ByteBufUtils.readUTF8String(buf), new CUnrecalculableTraitPool().read(buf));
        }

        rarities.clear();
        for (int i = buf.readInt(); i > 0; i--)
        {
            rarities.put(ByteBufUtils.readUTF8String(buf), new CRarity().read(buf));
        }

        itemTypes.clear();
        for (int i = buf.readInt(); i > 0; i--)
        {
            itemTypes.put(ByteBufUtils.readUTF8String(buf), new CItemType().read(buf));
        }


        attributeBalanceMultipliers.clear();
        for (int i = buf.readInt(); i > 0; i--)
        {
            attributeBalanceMultipliers.put(ByteBufUtils.readUTF8String(buf), buf.readDouble());
        }

        return this;
    }

    @Override
    public CSettings save(OutputStream stream)
    {
        CInt ci = new CInt().set(itemGenConfigVersion).save(stream).set(maxItemLevel).save(stream);

        CDouble cd = new CDouble().set(baseMultiplier).save(stream).set(multiplierBonusPerLevel).save(stream);


        CStringUTF8 cs = new CStringUTF8();
        ci.set(recalcTraitPools.size()).save(stream);
        for (Map.Entry<String, CRecalculableTraitPool> entry : recalcTraitPools.entrySet())
        {
            cs.set(entry.getKey()).save(stream);
            entry.getValue().save(stream);
        }

        ci.set(unrecalcTraitPools.size()).save(stream);
        for (Map.Entry<String, CUnrecalculableTraitPool> entry : unrecalcTraitPools.entrySet())
        {
            cs.set(entry.getKey()).save(stream);
            entry.getValue().save(stream);
        }

        ci.set(rarities.size()).save(stream);
        for (Map.Entry<String, CRarity> entry : rarities.entrySet())
        {
            cs.set(entry.getKey()).save(stream);
            entry.getValue().save(stream);
        }

        ci.set(itemTypes.size()).save(stream);
        for (Map.Entry<String, CItemType> entry : itemTypes.entrySet())
        {
            cs.set(entry.getKey()).save(stream);
            entry.getValue().save(stream);
        }


        ci.set(attributeBalanceMultipliers.size()).save(stream);
        for (Map.Entry<String, Double> entry : attributeBalanceMultipliers.entrySet())
        {
            cs.set(entry.getKey()).save(stream);
            cd.set(entry.getValue()).save(stream);
        }

        return this;
    }

    @Override
    public CSettings load(InputStream stream)
    {
        CInt ci = new CInt();
        CDouble cd = new CDouble();
        CStringUTF8 cs = new CStringUTF8();


        itemGenConfigVersion = ci.load(stream).value;


        maxItemLevel = ci.load(stream).value;

        baseMultiplier = cd.load(stream).value;
        multiplierBonusPerLevel = cd.load(stream).value;


        recalcTraitPools.clear();
        for (int i = ci.load(stream).value; i > 0; i--)
        {
            recalcTraitPools.put(cs.load(stream).value, new CRecalculableTraitPool().load(stream));
        }

        unrecalcTraitPools.clear();
        for (int i = ci.load(stream).value; i > 0; i--)
        {
            unrecalcTraitPools.put(cs.load(stream).value, new CUnrecalculableTraitPool().load(stream));
        }

        rarities.clear();
        for (int i = ci.load(stream).value; i > 0; i--)
        {
            rarities.put(cs.load(stream).value, new CRarity().load(stream));
        }

        itemTypes.clear();
        for (int i = ci.load(stream).value; i > 0; i--)
        {
            itemTypes.put(cs.load(stream).value, new CItemType().load(stream));
        }


        attributeBalanceMultipliers.clear();
        for (int i = ci.load(stream).value; i > 0; i--)
        {
            attributeBalanceMultipliers.put(cs.load(stream).value, cd.load(stream).value);
        }

        return this;
    }
}
