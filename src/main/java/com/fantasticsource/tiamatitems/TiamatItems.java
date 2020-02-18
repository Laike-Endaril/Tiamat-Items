package com.fantasticsource.tiamatitems;

import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.tiamatitems.compat.Compat;
import com.fantasticsource.tiamatitems.globalsettings.BlockGlobalSettings;
import com.fantasticsource.tiamatitems.globalsettings.CRarity;
import com.fantasticsource.tiamatitems.globalsettings.ItemGlobalSettings;
import com.fantasticsource.tiamatitems.itemeditor.BlockItemEditor;
import com.fantasticsource.tiamatitems.itemeditor.ItemItemEditor;
import com.fantasticsource.tiamatitems.traitgen.CItemType;
import com.fantasticsource.tiamatitems.traitgen.CTrait;
import com.fantasticsource.tiamatitems.traitgen.CTraitGenPool;
import com.fantasticsource.tiamatitems.traitgen.element.CTraitElement_Action1;
import com.fantasticsource.tiamatitems.traitgen.element.CTraitElement_PassiveAttributeMod;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;

@Mod(modid = TiamatItems.MODID, name = TiamatItems.NAME, version = TiamatItems.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.033b,);required-after:tiamatactions@[1.12.2.000,)")
public class TiamatItems
{
    public static final String MODID = "tiamatitems";
    public static final String NAME = "Tiamat Items";
    public static final String VERSION = "1.12.2.000b";
    public static final String DOMAIN = "tiamatrpg";


    public static final FilterRangedInt FILTER_POSITIVE = FilterRangedInt.get(0, Integer.MAX_VALUE);

    @GameRegistry.ObjectHolder(MODID + ":itemeditor")
    public static BlockItemEditor blockItemEditor;
    @GameRegistry.ObjectHolder(MODID + ":itemeditor")
    public static ItemItemEditor itemItemEditor;
    @GameRegistry.ObjectHolder(MODID + ":globalsettings")
    public static BlockGlobalSettings blockGlobalSettings;
    @GameRegistry.ObjectHolder(MODID + ":globalsettings")
    public static ItemGlobalSettings itemGlobalSettings;

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
        registry.register(new BlockGlobalSettings());
    }

    @SubscribeEvent
    public static void itemRegistry(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new ItemItemEditor());
        registry.register(new ItemGlobalSettings());

        registry.register(new TiamatItem());
    }

    @SubscribeEvent
    public static void modelRegistry(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(itemItemEditor, 0, new ModelResourceLocation(MODID + ":itemeditor", "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemGlobalSettings, 0, new ModelResourceLocation(MODID + ":globalsettings", "inventory"));

        ModelLoader.setCustomModelResourceLocation(tiamatItem, 0, new ModelResourceLocation(MODID + ":tiamatitem", "inventory"));
    }


    @SubscribeEvent
    public static void clientDisconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        TextureCache.clear(event);
    }


    @Mod.EventHandler
    public static void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new Commands());


        //TODO test code start
        CTraitElement_PassiveAttributeMod passiveAttributeMod = new CTraitElement_PassiveAttributeMod();
        passiveAttributeMod.attributeName = "generic.maxHealth";
        passiveAttributeMod.minimum = 1;
        passiveAttributeMod.maximum = 3;

        CTrait gen = new CTrait();
        gen.name = "TestAttributeTrait";
        gen.elements.add(passiveAttributeMod);
        gen.minValue = 1;
        gen.maxValue = 3;

        CTraitGenPool generalPool = new CTraitGenPool();
        generalPool.name = "TestGeneralPool";
        generalPool.traitGenWeights.put(gen, 1);
        CTraitGenPool.traitGenPools.put(generalPool.name, generalPool);

        ArrayList<CTraitGenPool> generalPoolSet = new ArrayList<>();
        generalPoolSet.add(generalPool);


        CTraitElement_Action1 action1 = new CTraitElement_Action1();
        action1.actionName = "Test";

        gen = new CTrait();
        gen.name = "TestActionTrait";
        gen.elements.add(action1);
        gen.minValue = 1;
        gen.maxValue = 3;

        CTraitGenPool actionPool = new CTraitGenPool();
        actionPool.name = "TestActionPool";
        actionPool.traitGenWeights.put(gen, 1);
        CTraitGenPool.traitGenPools.put(actionPool.name, actionPool);

        ArrayList<CTraitGenPool> actionPoolSet = new ArrayList<>();
        actionPoolSet.add(actionPool);


        CItemType itemType = new CItemType();
        itemType.name = "TestType";
        itemType.slotting = "Head";
        itemType.randomTraitPoolSets.add(actionPoolSet);
        itemType.randomTraitPoolSets.add(generalPoolSet);

        CItemType.itemTypes.put(itemType.name, itemType);

        CRarity rarity = new CRarity();
        rarity.name = "TestRarity";
        rarity.textColor = TextFormatting.GOLD;
        rarity.color = Color.ORANGE;
        rarity.itemLevelModifier = 0.5;
        rarity.traitCounts.add(1); //1 action trait
        rarity.traitCounts.add(1); //1 general trait

        CRarity.rarities.put(rarity.name, rarity);
        //TODO test code end
    }
}
