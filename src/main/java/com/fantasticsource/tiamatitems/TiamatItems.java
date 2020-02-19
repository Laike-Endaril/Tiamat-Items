package com.fantasticsource.tiamatitems;

import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.tiamatitems.assembly.PartSlot;
import com.fantasticsource.tiamatitems.compat.Compat;
import com.fantasticsource.tiamatitems.globalsettings.BlockGlobalSettings;
import com.fantasticsource.tiamatitems.globalsettings.CRarity;
import com.fantasticsource.tiamatitems.globalsettings.ItemGlobalSettings;
import com.fantasticsource.tiamatitems.itemeditor.BlockItemEditor;
import com.fantasticsource.tiamatitems.itemeditor.ItemItemEditor;
import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tiamatitems.trait.CTrait;
import com.fantasticsource.tiamatitems.trait.CTraitPool;
import com.fantasticsource.tiamatitems.trait.element.*;
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

import java.util.HashSet;
import java.util.LinkedHashMap;

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
        //Main action traits
        LinkedHashMap<String, CTraitPool> leftClickActionPoolSet = new LinkedHashMap<>();

        CTraitPool pool = new CTraitPool();
        pool.name = "2HAxeActLC";
        CTraitPool.traitGenPools.put(pool.name, pool);
        leftClickActionPoolSet.put(pool.name, pool);

        CTraitElement_LeftClickAction leftClickAction = new CTraitElement_LeftClickAction();
        leftClickAction.actionName = "2HAxeAttack1";

        CTrait trait = new CTrait();
        trait.name = "2HAxeAttack1";
        trait.elements.add(leftClickAction);
        trait.minValue = 3;
        trait.maxValue = 3;
        pool.traitGenWeights.put(trait, 1);


        //Sub action traits
        LinkedHashMap<String, CTraitPool> rightClickActionPoolSet = new LinkedHashMap<>();

        pool = new CTraitPool();
        pool.name = "2HAxeActRC";
        CTraitPool.traitGenPools.put(pool.name, pool);
        rightClickActionPoolSet.put(pool.name, pool);

        CTraitElement_RightClickAction rightClickAction = new CTraitElement_RightClickAction();
        rightClickAction.actionName = "2HAxeAttack2";

        trait = new CTrait();
        trait.name = "2HAxeAttack2";
        trait.elements.add(rightClickAction);
        trait.minValue = 3;
        trait.maxValue = 3;
        pool.traitGenWeights.put(trait, 1);


        //General traits
        LinkedHashMap<String, CTraitPool> generalPoolSet = new LinkedHashMap<>();

        pool = new CTraitPool();
        pool.name = "2HAxeGen";
        CTraitPool.traitGenPools.put(pool.name, pool);
        generalPoolSet.put(pool.name, pool);

        CTraitElement_PassiveAttributeMod passiveAttributeMod = new CTraitElement_PassiveAttributeMod();
        passiveAttributeMod.attributeName = "generic.maxHealth";
        passiveAttributeMod.minimum = 1;
        passiveAttributeMod.maximum = 3;

        trait = new CTrait();
        trait.name = "MaxHP";
        trait.elements.add(passiveAttributeMod);
        trait.minValue = 1;
        trait.maxValue = 3;
        pool.traitGenWeights.put(trait, 1);


        //Part slot traits
        HashSet<String> validItemTypes = new HashSet<>();
        validItemTypes.add("Gem");
        validItemTypes.add("Rune");
        PartSlot.validItemTypes.put("Socket", validItemTypes);

        LinkedHashMap<String, CTraitPool> partSlotPoolSet = new LinkedHashMap<>();
        pool = new CTraitPool();
        pool.name = "Sockets";
        CTraitPool.traitGenPools.put(pool.name, pool);
        partSlotPoolSet.put(pool.name, pool);

        CTraitElement_PartSlot partSlot = new CTraitElement_PartSlot();
        partSlot.partSlotType = "Socket";
        partSlot.minimum = 0;
        partSlot.maximum = 1;

        for (int i = 0; i < 10; i++)
        {
            trait = new CTrait();
            trait.name = "Socket" + i;
            trait.elements.add(partSlot);
            trait.minValue = 0;
            trait.maxValue = 3;
            pool.traitGenWeights.put(trait, 1);
        }


        //Item type
        CItemType itemType = new CItemType();
        itemType.name = "2H Axe";
        itemType.slotting = "Tiamat 2H";
        itemType.percentageMultiplier = 2;

        CTraitElement_ActiveAttributeMod activeAttributeMod = new CTraitElement_ActiveAttributeMod();
        activeAttributeMod.attributeName = "generic.attackDamage";
        activeAttributeMod.minimum = 1;
        activeAttributeMod.maximum = 3;

        trait = new CTrait();
        trait.name = "Damage";
        trait.elements.add(activeAttributeMod);
        trait.minValue = 1;
        trait.maxValue = 3;
        itemType.staticTraits.put(trait.name, trait);

        partSlot = new CTraitElement_PartSlot();
        partSlot.partSlotType = "2H Axehead";
        partSlot.required = true;
        partSlot.minimum = 1;
        partSlot.maximum = 1;

        trait = new CTrait();
        trait.name = "2HAxehead";
        trait.elements.add(partSlot);
        trait.minValue = 0;
        trait.maxValue = 0;
        itemType.staticTraits.put(trait.name, trait);

        partSlot = new CTraitElement_PartSlot();
        partSlot.partSlotType = "2H Axe Handle";
        partSlot.required = true;
        partSlot.minimum = 1;
        partSlot.maximum = 1;

        trait = new CTrait();
        trait.name = "2HAxeHandle";
        trait.elements.add(partSlot);
        trait.minValue = 0;
        trait.maxValue = 0;
        itemType.staticTraits.put(trait.name, trait);

        partSlot = new CTraitElement_PartSlot();
        partSlot.partSlotType = "2H Axe Skin";
        partSlot.required = true;
        partSlot.minimum = 1;
        partSlot.maximum = 1;

        trait = new CTrait();
        trait.name = "2HAxeSkin";
        trait.elements.add(partSlot);
        trait.minValue = 0;
        trait.maxValue = 0;
        itemType.staticTraits.put(trait.name, trait);

        itemType.randomTraitPoolSets.put("ActLC", leftClickActionPoolSet);
        itemType.randomTraitPoolSets.put("ActRC", rightClickActionPoolSet);
        itemType.randomTraitPoolSets.put("Gen", generalPoolSet);
        itemType.randomTraitPoolSets.put("Socket", partSlotPoolSet);


        CItemType.itemTypes.put(itemType.name, itemType);


        CRarity rarity = new CRarity();
        rarity.name = "TestRarity";
        rarity.textColor = TextFormatting.GOLD;
        rarity.color = Color.ORANGE;
        rarity.itemLevelModifier = 0.5;
        rarity.traitCounts.put("ActLC", 1); //1x action 1 trait
        rarity.traitCounts.put("ActRC", 1); //1x action 2 trait
        rarity.traitCounts.put("Gen", 1); //1x general trait
        rarity.traitCounts.put("Socket", 6); //6x part slot trait (each generates 0-1 slots)

        CRarity.rarities.put(rarity.name, rarity);
        //TODO test code end
    }
}
