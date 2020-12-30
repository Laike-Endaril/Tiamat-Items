package com.fantasticsource.tiamatitems;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.tiamatitems.assembly.ItemAssembly;
import com.fantasticsource.tiamatitems.compat.Compat;
import com.fantasticsource.tiamatitems.itemeditor.BlockItemEditor;
import com.fantasticsource.tiamatitems.itemeditor.ItemItemEditor;
import com.fantasticsource.tiamatitems.nbt.DropTransformationTags;
import com.fantasticsource.tiamatitems.settings.BlockSettings;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tiamatitems.settings.ItemSettings;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;

import java.io.IOException;

@Mod(modid = TiamatItems.MODID, name = TiamatItems.NAME, version = TiamatItems.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.039h,);required-after:tiamatactions@[1.12.2.000zo,)")
public class TiamatItems
{
    public static final String MODID = "tiamatitems";
    public static final String NAME = "Tiamat Items";
    public static final String VERSION = "1.12.2.000zv";
    public static final String DOMAIN = "tiamatrpg";


    public static final FilterRangedInt FILTER_POSITIVE = FilterRangedInt.get(0, Integer.MAX_VALUE);

    @GameRegistry.ObjectHolder(MODID + ":itemeditor")
    public static BlockItemEditor blockItemEditor;
    @GameRegistry.ObjectHolder(MODID + ":itemeditor")
    public static ItemItemEditor itemItemEditor;
    @GameRegistry.ObjectHolder(MODID + ":settings")
    public static BlockSettings blockSettings;
    @GameRegistry.ObjectHolder(MODID + ":settings")
    public static ItemSettings itemSettings;

    @GameRegistry.ObjectHolder(MODID + ":tiamatitem")
    public static TiamatItem tiamatItem;

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
        Network.init();
        MinecraftForge.EVENT_BUS.register(TiamatItems.class);
        MinecraftForge.EVENT_BUS.register(TransientAttributeModEvent.class);
        MinecraftForge.EVENT_BUS.register(DropTransformationTags.class);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            //Physical client
            MinecraftForge.EVENT_BUS.register(ClientInit.class);
            MinecraftForge.EVENT_BUS.register(ClientItemStackFixer.class);
        }
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event)
    {
        Compat.tiamatinventory = Loader.isModLoaded("tiamatinventory");
        Compat.baubles = Loader.isModLoaded("baubles");

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            //Physical client

            TextureCache.addRawTextureLayers();
        }
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }


    @SubscribeEvent
    public static void blockRegistry(RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(new BlockItemEditor());
        registry.register(new BlockSettings());
    }

    @SubscribeEvent
    public static void itemRegistry(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new ItemItemEditor());
        registry.register(new ItemSettings());

        registry.register(new TiamatItem());
    }

    @SubscribeEvent
    public static void modelRegistry(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(itemItemEditor, 0, new ModelResourceLocation(MODID + ":itemeditor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemSettings, 0, new ModelResourceLocation(MODID + ":settings", "inventory"));

        ModelLoader.setCustomModelResourceLocation(tiamatItem, 0, new ModelResourceLocation(MODID + ":tiamatitem", "inventory"));
    }


    @SubscribeEvent
    public static void clientDisconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        TextureCache.clear(event);
        ClientData.clear();
    }

    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        Network.WRAPPER.sendTo(new Network.ItemgenVersionPacket(CSettings.SETTINGS.getVersion()), (EntityPlayerMP) event.player);
    }


    @Mod.EventHandler
    public static void serverStarting(FMLServerStartingEvent event) throws IOException
    {
        event.registerServerCommand(new Commands());

        CSettings.loadAll(event);

        if (MCTools.devEnv() && CSettings.SETTINGS.itemTypes.size() == 0)
        {
            System.out.println(TextFormatting.AQUA + "Adding test configs");

            Test.createGeneralPool();
            Test.createSocketPool();

            Test.createRarity();

            Test.create2HAxeItemType();
            Test.create2HAxeheadItemType();
            Test.create2HAxeHandleItemType();
            Test.create2HAxeSkinItemType();

            Test.createChestplateItemType();
            Test.createChestplateSoulItemType();
            Test.createChestplateCoreItemType();
            Test.createChestplateTrimItemType();

            CSettings.EDITED_SETTINGS = (CSettings) CSettings.SETTINGS.copy();
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void itemStackConstruction(AttachCapabilitiesEvent<ItemStack> event)
    {
        if (!MCTools.hosting()) return; //Logical server side only

        ItemAssembly.validate(event.getObject());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void inventoryChanged(InventoryChangedEvent event)
    {
        for (ItemStack stack : event.newInventory.allNonSkin) ItemAssembly.validate(stack);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void openedContainer(PlayerContainerEvent.Open event)
    {
        for (Slot slot : event.getContainer().inventorySlots) ItemAssembly.validate(slot.getStack());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void itemJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (event.getWorld().isRemote || !(entity instanceof EntityItem)) return;

        ItemAssembly.validate(((EntityItem) entity).getItem());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void itemPickup(EntityItemPickupEvent event)
    {
        ItemAssembly.validate(event.getItem().getItem());
    }
}
