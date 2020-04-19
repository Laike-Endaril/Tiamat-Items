package com.fantasticsource.tiamatitems;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.tiamatitems.assembly.ItemAssembly;
import com.fantasticsource.tiamatitems.compat.Compat;
import com.fantasticsource.tiamatitems.itemeditor.BlockItemEditor;
import com.fantasticsource.tiamatitems.itemeditor.ItemItemEditor;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.settings.BlockSettings;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tiamatitems.settings.ItemSettings;
import moe.plushie.armourers_workshop.api.ArmourersWorkshopApi;
import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.io.IOException;
import java.util.List;

@Mod(modid = TiamatItems.MODID, name = TiamatItems.NAME, version = TiamatItems.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.034i,);required-after:tiamatactions@[1.12.2.000,)")
public class TiamatItems
{
    public static final String MODID = "tiamatitems";
    public static final String NAME = "Tiamat Items";
    public static final String VERSION = "1.12.2.000e";
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

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            //Physical client
            MinecraftForge.EVENT_BUS.register(ClientInit.class);
        }
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event)
    {
        Compat.tiamatrpg = Loader.isModLoaded("tiamatrpg");
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
        Network.WRAPPER.sendTo(new Network.ItemgenVersionPacket(CSettings.getVersion()), (EntityPlayerMP) event.player);
    }


    @Mod.EventHandler
    public static void serverStarting(FMLServerStartingEvent event) throws IOException
    {
        event.registerServerCommand(new Commands());

        CSettings.loadAll(event);
    }


    @SubscribeEvent
    public static void itemStackConstruction(AttachCapabilitiesEvent<ItemStack> event)
    {
        if (!MCTools.hosting()) return; //Logical server side only

        ItemStack stack = event.getObject();
        if (stack.isEmpty() || !stack.hasTagCompound()) return;

        String itemTypeName = MiscTags.getItemTypeName(stack);
        if (itemTypeName.equals("")) return;

        long version = MiscTags.getItemGenVersion(stack);
        if (version == Long.MAX_VALUE || version == CSettings.getVersion()) return;


        ItemAssembly.recalc(stack);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void tooltip(ItemTooltipEvent event)
    {
        if (ClientData.serverItemGenConfigVersion == -1) return;

        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || !stack.hasTagCompound()) return;

        String itemTypeName = MiscTags.getItemTypeName(stack);
        if (itemTypeName.equals("")) return;

        if (MiscTags.getItemGenVersion(stack) == ClientData.serverItemGenConfigVersion) return;


        List<String> tooltip = event.getToolTip();
        tooltip.add("");
        tooltip.add(TextFormatting.RED + "WARNING: This item's tooltip may not be accurate!  Requesting accurate data from server...");
        tooltip.add("");


        int id;
        if (!ClientData.idToBadStack.containsValue(stack))
        {
            id = ClientData.nextID++;
            ClientData.idToBadStack.put(id, stack);

            Network.WRAPPER.sendToServer(new Network.RequestItemStackUpdatePacket(stack, id));
        }
        else
        {
            ItemStack goodStack = ClientData.badStackToGoodStack.get(stack);
            if (goodStack != null)
            {
                stack.setTagCompound(goodStack.getTagCompound());

                ClientData.idToBadStack.entrySet().removeIf(entry -> entry.getValue() == stack);
                ClientData.badStackToGoodStack.remove(stack);
            }
        }
    }


    @SubscribeEvent
    public static void test(PlayerInteractEvent.EntityInteractSpecific event)
    {
        //TODO Remove all this
        if (event.getSide() == Side.CLIENT || event.getHand() == EnumHand.OFF_HAND) return;

        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        IPlayerWardrobeCap wardrobe = ArmourersWorkshopApi.getPlayerWardrobeCapability(player);
        wardrobe.getExtraColours().setColour(IExtraColours.ExtraColourType.SKIN, (255 << 24) | (255 << 16) | (0 << 8) | 255);
        wardrobe.syncToPlayer(player);
        wardrobe.syncToAllTracking();


        ItemAssembly.assemble(player.inventory.getStackInSlot(0), player.inventory.getStackInSlot(1), player.inventory.getStackInSlot(2), player.inventory.getStackInSlot(3));
    }
}
