package com.fantasticsource.tiamatitems;

import com.evilnotch.iitemrender.handlers.IItemRendererHandler;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatitems.cache.ProcessedTextureCache;
import com.fantasticsource.tiamatitems.cache.TextureLayerCache;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;

import java.io.File;

@Mod(modid = TiamatItems.MODID, name = TiamatItems.NAME, version = TiamatItems.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.027a,);required-after:iitemrenderer@[1.0,)")
public class TiamatItems
{
    public static final String MODID = "tiamatitems";
    public static final String NAME = "Tiamat Items";
    public static final String VERSION = "1.12.2.000";


    public static File texturesDir;

    @GameRegistry.ObjectHolder(MODID + ":tiamatitem")
    private static TiamatItem tiamatItem;

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
                int i = 0;
                for (File imageFile : texturesDir.listFiles())
                {
                    String name = imageFile.getName();
                    if (!name.substring(name.lastIndexOf(".") + 1).equals("png")) continue;

                    i += TextureLayerCache.addTextureLayers(texturesDir.getAbsolutePath() + File.separator + name, name);
                }
                System.out.println("Cached " + i + " raw texture layer" + (i == 1 ? "" : "s"));
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


    @SubscribeEvent
    public static void clientDisconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        ProcessedTextureCache.clear(event);
    }


    @SubscribeEvent
    public static void rightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        ItemStack stack = event.getItemStack();

        if (stack.getItem() == tiamatItem)
        {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound == null)
            {
                compound = new NBTTagCompound();
                stack.setTagCompound(compound);
            }

            compound.setTag(MODID, new NBTTagCompound());
            compound = compound.getCompoundTag(MODID);

            compound.setTag("layers", new NBTTagList());
            NBTTagList list = compound.getTagList("layers", Constants.NBT.TAG_STRING);

            list.appendTag(new NBTTagString("Test:0:ff000077"));
        }
    }
}
