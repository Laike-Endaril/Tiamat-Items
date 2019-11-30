package com.fantasticsource.tiamatitems;

import com.evilnotch.iitemrender.handlers.IItemRendererHandler;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.PNG;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;

@Mod(modid = TiamatItems.MODID, name = TiamatItems.NAME, version = TiamatItems.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.027,);required-after:iitemrenderer@[1.0,)")
public class TiamatItems
{
    public static final String MODID = "tiamatitems";
    public static final String NAME = "Tiamat Items";
    public static final String VERSION = "1.12.2.000";


    public static File texturesDir;
    public static LinkedHashMap<String, Pair<Integer, Integer>> validTextureNamesAndSizes = new LinkedHashMap<>();


    public static CreativeTabs creativeTab = new CreativeTabs(MODID)
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(tiamatItem);
        }

        @Override
        public void displayAllRelevantItems(NonNullList<ItemStack> itemStacks)
        {
            super.displayAllRelevantItems(itemStacks);
        }
    };


    @GameRegistry.ObjectHolder(MODID + ":tiamatitem")
    private static TiamatItem tiamatItem;


    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(TiamatItems.class);
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            //Physical client

            texturesDir = new File(MCTools.getConfigDir() + MODID);
            if (!texturesDir.exists()) texturesDir.mkdir();
            else
            {
                System.out.println("==================================================================================================================");
                for (File imageFile : texturesDir.listFiles())
                {
                    String name = imageFile.getName();
                    if (!name.substring(name.lastIndexOf(".") + 1).equals("png")) continue;

                    PNG png = new PNG(texturesDir.getAbsolutePath() + File.separator + name);
                    validTextureNamesAndSizes.put(name.substring(0, name.length() - 4), new Pair<>(png.getWidth(), png.getHeight()));
                    ByteBuffer buffer = png.getDirectBuffer();
                    int r, g, b, a;
                    buffer.mark();
                    for (int y = 0; y < png.getHeight(); y++)
                    {
                        for (int x = 0; x < png.getWidth(); x++)
                        {
                            r = buffer.get() & 0xff;
                            g = buffer.get() & 0xff;
                            b = buffer.get() & 0xff;
                            a = buffer.get() & 0xff;
                        }
                    }

                    buffer.reset();
                    for (int y = 0; y < png.getHeight(); y++)
                    {
                        for (int x = 0; x < png.getWidth(); x++)
                        {
                            r = buffer.get() & 0xff;
                            g = buffer.get() & 0xff;
                            b = buffer.get() & 0xff;
                            a = buffer.get() & 0xff;
                        }
                    }
                    png.free();

                }
            }
        }
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }


    @SubscribeEvent
    public static void itemRegistry(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new TiamatItem());
    }

    @SubscribeEvent
    public static void modelRegistry(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(tiamatItem, 0, new ModelResourceLocation(MODID + ":tiamatitem", "inventory"));
        IItemRendererHandler.register(tiamatItem, new TiamatItemRenderer());
    }
}
