package com.fantasticsource.tiamatitems.settings;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatitems.RarityData;
import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitPool;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTraitPool;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class CSettings extends Component
{
    public static final String FILENAME = MODID + File.separator + "settings.dat";
    public static final int ITEM_GEN_CODE_VERSION = 0;
    public static CSettings LOCAL_SETTINGS = new CSettings(), PENDING_LOCAL_SETTINGS = new CSettings();
    public static LinkedHashMap<String, Double> attributeBalanceMultipliers = new LinkedHashMap<>();
    public int itemGenConfigVersion = 0;
    public int maxItemLevel = 20;
    public double baseMultiplier = 1, multiplierBonusPerLevel = 1;
    public LinkedHashMap<String, CRecalculableTraitPool> recalcTraitPools = new LinkedHashMap<>();
    public LinkedHashMap<String, CUnrecalculableTraitPool> unrecalcTraitPools = new LinkedHashMap<>();
    public LinkedHashMap<String, CRarity> rarities = new LinkedHashMap<>();
    public LinkedHashMap<String, CItemType> itemTypes = new LinkedHashMap<>();
    public LinkedHashMap<String, LinkedHashSet<String>> slotTypes = new LinkedHashMap<>();

    public long getVersion()
    {
        return (((long) ITEM_GEN_CODE_VERSION) << 32) | itemGenConfigVersion;
    }

    public static void updateVersionAndSave(EntityPlayerMP player)
    {
        System.out.println("Saving changes to Tiamat Items settings" + (player == null ? "" : " (" + player.getName() + ")"));
        File file = new File(MCTools.getConfigDir() + FILENAME);
        if (file.isDirectory()) throw new IllegalStateException(TextFormatting.RED + MCTools.getWorldSaveDir(FMLCommonHandler.instance().getMinecraftServerInstance()) + FILENAME + " is a directory instead of a file!");
        file.mkdirs();
        while (file.exists()) file.delete();

        try
        {
            FileOutputStream stream = new FileOutputStream(file);
            PENDING_LOCAL_SETTINGS.save(stream);
            stream.close();
            System.out.println(TextFormatting.GREEN + "Saved settings; they will be applied next server restart");
            player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Saved settings; they will be applied next server restart"));
        }
        catch (IOException e)
        {
            System.err.println(TextFormatting.RED + "Failed to save settings");
            player.sendMessage(new TextComponentString(TextFormatting.RED + "Failed to save settings (see server log for details)"));
            e.printStackTrace();
        }
    }

    public static void loadAll(FMLServerStartingEvent event) throws IOException
    {
        File file = new File(MCTools.getConfigDir() + FILENAME);
        if (file.isDirectory()) throw new IllegalStateException(TextFormatting.RED + MCTools.getWorldSaveDir(event.getServer()) + FILENAME + " is a directory instead of a file!");
        if (!file.exists()) return;

        FileInputStream stream = new FileInputStream(file);
        LOCAL_SETTINGS = new CSettings().load(stream);
        RarityData.rarities = LOCAL_SETTINGS.rarities;
        PENDING_LOCAL_SETTINGS = (CSettings) LOCAL_SETTINGS.copy();
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


        buf.writeInt(slotTypes.size());
        for (Map.Entry<String, LinkedHashSet<String>> entry : slotTypes.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            buf.writeInt(entry.getValue().size());
            for (String itemType : entry.getValue())
            {
                ByteBufUtils.writeUTF8String(buf, itemType);
            }
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


        slotTypes.clear();
        for (int i = buf.readInt(); i > 0; i--)
        {
            String slotType = ByteBufUtils.readUTF8String(buf);
            LinkedHashSet<String> itemTypes = new LinkedHashSet<>();
            for (int i2 = buf.readInt(); i2 > 0; i2--)
            {
                itemTypes.add(ByteBufUtils.readUTF8String(buf));
            }
            slotTypes.put(slotType, itemTypes);
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


        ci.set(slotTypes.size()).save(stream);
        for (Map.Entry<String, LinkedHashSet<String>> entry : slotTypes.entrySet())
        {
            cs.set(entry.getKey()).save(stream);
            ci.set(entry.getValue().size()).save(stream);
            for (String itemType : entry.getValue())
            {
                cs.set(itemType).save(stream);
            }
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


        slotTypes.clear();
        for (int i = ci.load(stream).value; i > 0; i--)
        {
            String slotType = cs.load(stream).value;
            LinkedHashSet<String> itemTypes = new LinkedHashSet<>();
            for (int i2 = ci.load(stream).value; i2 > 0; i2--)
            {
                itemTypes.add(cs.load(stream).value);
            }
            slotTypes.put(slotType, itemTypes);
        }


        return this;
    }
}
