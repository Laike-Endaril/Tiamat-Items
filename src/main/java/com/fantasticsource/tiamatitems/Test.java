package com.fantasticsource.tiamatitems;

import com.fantasticsource.tiamatitems.assembly.PartSlot;
import com.fantasticsource.tiamatitems.settings.CRarity;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTrait;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitPool;
import com.fantasticsource.tiamatitems.trait.recalculable.element.*;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTrait;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.CUTraitElement_AWSkin;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes.CRGBBoost;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes.CRGBGrayscale;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes.CRandomRGB;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static com.fantasticsource.tiamatitems.nbt.AssemblyTags.*;

public class Test
{
    public static void createGeneralPool()
    {
        CRecalculableTraitPool pool = new CRecalculableTraitPool();
        pool.name = "General";

        CRTraitElement_PassiveAttributeMod passiveAttributeMod = new CRTraitElement_PassiveAttributeMod();
        passiveAttributeMod.attributeName = "generic.maxHealth";
        passiveAttributeMod.minAmount = 1;
        passiveAttributeMod.maxAmount = 3;

        CRecalculableTrait trait = new CRecalculableTrait();
        trait.name = "MaxHP";
        trait.elements.add(passiveAttributeMod);
        trait.minValue = 1;
        trait.maxValue = 3;
        pool.traitGenWeights.put(trait, 1);

        CSettings.SETTINGS.recalcTraitPools.put(pool.name, pool);
    }


    public static void createRarity()
    {
        CRarity rarity = new CRarity();
        rarity.name = "TestRarity";
        rarity.textColor = TextFormatting.GOLD;
        rarity.color = Color.ORANGE;
        rarity.itemLevelModifier = 0.5;
        rarity.traitPoolSetRollCounts.put("ActLC", 1); //1x left click action
        rarity.traitPoolSetRollCounts.put("ActRC", 1); //1x right click action
        rarity.traitPoolSetRollCounts.put("Gen", 1); //1x general trait
        rarity.traitPoolSetRollCounts.put("Socket", 6); //6x part slot trait (each generates 0-1 slots)

        CSettings.SETTINGS.rarities.put(rarity.name, rarity);
    }


    public static void createSocketPool()
    {
        HashSet<String> validItemTypes = new HashSet<>();
        validItemTypes.add("Gem");
        validItemTypes.add("Rune");
        PartSlot.validItemTypes.put("Socket", validItemTypes);

        CRecalculableTraitPool pool = new CRecalculableTraitPool();
        pool.name = "Sockets";
        CSettings.SETTINGS.recalcTraitPools.put(pool.name, pool);

        CRTraitElement_PartSlot partSlot = new CRTraitElement_PartSlot();
        partSlot.partSlotType = "Socket";

        CRecalculableTrait trait;
        for (int i = 0; i < 10; i++)
        {
            trait = new CRecalculableTrait();
            trait.name = "Socket" + i;
            trait.elements.add(partSlot);
            trait.minValue = 0;
            trait.maxValue = 3;
            pool.traitGenWeights.put(trait, 1);
        }
    }


    public static void create2HAxeItemType()
    {
        //Main action traits
        CRecalculableTraitPool pool = new CRecalculableTraitPool();
        pool.name = "2HAxeActLC";
        CSettings.SETTINGS.recalcTraitPools.put(pool.name, pool);


        CRTraitElement_LeftClickAction leftClickAction = new CRTraitElement_LeftClickAction();
        leftClickAction.actionName = "2HAxeAttack1";

        CRecalculableTrait trait = new CRecalculableTrait();
        trait.name = "2HAxeAttack1";
        trait.elements.add(leftClickAction);
        trait.minValue = 3;
        trait.maxValue = 3;
        pool.traitGenWeights.put(trait, 1);


        //Sub action traits
        pool = new CRecalculableTraitPool();
        pool.name = "2HAxeActRC";
        CSettings.SETTINGS.recalcTraitPools.put(pool.name, pool);


        CRTraitElement_RightClickAction rightClickAction = new CRTraitElement_RightClickAction();
        rightClickAction.actionName = "2HAxeAttack2";

        trait = new CRecalculableTrait();
        trait.name = "2HAxeAttack2";
        trait.elements.add(rightClickAction);
        trait.minValue = 3;
        trait.maxValue = 3;
        pool.traitGenWeights.put(trait, 1);


        //Item type
        CItemType itemType = new CItemType();
        itemType.name = "2H Axe";
        itemType.slotting = "Tiamat 2H";
        itemType.traitLevelMultiplier = 2;


        CRTraitElement_ForcedAWSkinTypeOverride skinTypeOverrideElement = new CRTraitElement_ForcedAWSkinTypeOverride();
        skinTypeOverrideElement.skinType = "axe";

        trait = new CRecalculableTrait();
        trait.name = "Axe Skin Override";
        trait.elements.add(skinTypeOverrideElement);
        trait.minValue = 1;
        trait.maxValue = 1;
        itemType.staticRecalculableTraits.put(trait.name, trait);


        HashSet<String> validItemTypes = new HashSet<>();
        validItemTypes.add("2H Axehead");
        PartSlot.validItemTypes.put("2H Axehead", validItemTypes);

        CRTraitElement_PartSlot partSlot = new CRTraitElement_PartSlot();
        partSlot.partSlotType = "2H Axehead";
        partSlot.required = true;
        partSlot.minCount = 1;

        trait = new CRecalculableTrait();
        trait.name = "2H Axehead Slot";
        trait.elements.add(partSlot);
        trait.minValue = 0;
        trait.maxValue = 0;
        itemType.staticRecalculableTraits.put(trait.name, trait);


        validItemTypes = new HashSet<>();
        validItemTypes.add("2H Axe Handle");
        PartSlot.validItemTypes.put("2H Axe Handle", validItemTypes);

        partSlot = new CRTraitElement_PartSlot();
        partSlot.partSlotType = "2H Axe Handle";
        partSlot.required = true;
        partSlot.minCount = 1;

        trait = new CRecalculableTrait();
        trait.name = "2H Axe Handle Slot";
        trait.elements.add(partSlot);
        trait.minValue = 0;
        trait.maxValue = 0;
        itemType.staticRecalculableTraits.put(trait.name, trait);


        validItemTypes = new HashSet<>();
        validItemTypes.add("2H Axe Skin");
        PartSlot.validItemTypes.put("2H Axe Skin", validItemTypes);

        partSlot = new CRTraitElement_PartSlot();
        partSlot.partSlotType = "2H Axe Skin";
        partSlot.required = true;
        partSlot.minCount = 1;

        trait = new CRecalculableTrait();
        trait.name = "2H Axe Skin Slot";
        trait.elements.add(partSlot);
        trait.minValue = 0;
        trait.maxValue = 0;
        itemType.staticRecalculableTraits.put(trait.name, trait);


        CRTraitElement_TextureLayers textureElement;
        ArrayList<String> layerGroup;
        for (int state : new int[]{STATE_USABLE, STATE_FULL})
        {
            textureElement = new CRTraitElement_TextureLayers();
            layerGroup = new ArrayList<>();
            layerGroup.add("equipment/axe:0:ffffffff");
            textureElement.layerGroups.put(state, layerGroup);

            trait = new CRecalculableTrait();
            trait.name = "Layer Group " + state;
            trait.elements.add(textureElement);
            trait.minValue = 0;
            trait.maxValue = 0;
            trait.addToCoreOnAssembly = false;
            itemType.staticRecalculableTraits.put(trait.name, trait);
        }


        LinkedHashMap<String, CRecalculableTraitPool> poolSet = new LinkedHashMap<>();
        poolSet.put("2HAxeActLC", CSettings.SETTINGS.recalcTraitPools.get("2HAxeActLC"));
        itemType.randomRecalculableTraitPoolSets.put("ActLC", poolSet);

        poolSet = new LinkedHashMap<>();
        poolSet.put("2HAxeActRC", CSettings.SETTINGS.recalcTraitPools.get("2HAxeActRC"));
        itemType.randomRecalculableTraitPoolSets.put("ActRC", poolSet);

        poolSet = new LinkedHashMap<>();
        poolSet.put("General", CSettings.SETTINGS.recalcTraitPools.get("General"));
        itemType.randomRecalculableTraitPoolSets.put("Gen", poolSet);

        poolSet = new LinkedHashMap<>();
        poolSet.put("Sockets", CSettings.SETTINGS.recalcTraitPools.get("Sockets"));
        itemType.randomRecalculableTraitPoolSets.put("Socket", poolSet);


        CSettings.SETTINGS.itemTypes.put(itemType.name, itemType);
    }

    public static void create2HAxeheadItemType()
    {
        //Item type
        CItemType itemType = new CItemType();
        itemType.name = "2H Axehead";
        itemType.traitLevelMultiplier = 2;


        CRTraitElement_ActiveAttributeMod element = new CRTraitElement_ActiveAttributeMod();
        element.attributeName = "generic.attackDamage";
        element.minAmount = 1;
        element.maxAmount = 3;

        CRecalculableTrait trait = new CRecalculableTrait();
        trait.name = "Damage";
        trait.elements.add(element);
        trait.minValue = 1;
        trait.maxValue = 3;
        itemType.staticRecalculableTraits.put(trait.name, trait);


        CRTraitElement_TextureLayers textureElement;
        ArrayList<String> layerGroup;
        for (int state : new int[]{STATE_EMPTY, STATE_UNUSABLE, STATE_USABLE, STATE_FULL})
        {
            textureElement = new CRTraitElement_TextureLayers();
            layerGroup = new ArrayList<>();
            layerGroup.add("equipment/axe:2:ffffffff");
            textureElement.layerGroups.put(state, layerGroup);

            trait = new CRecalculableTrait();
            trait.name = "Layer Group " + state;
            trait.elements.add(textureElement);
            trait.minValue = 0;
            trait.maxValue = 0;
            trait.addToCoreOnAssembly = false;
            itemType.staticRecalculableTraits.put(trait.name, trait);
        }


        LinkedHashMap<String, CRecalculableTraitPool> poolSet = new LinkedHashMap<>();
        poolSet.put("General", CSettings.SETTINGS.recalcTraitPools.get("General"));
        itemType.randomRecalculableTraitPoolSets.put("Gen", poolSet);


        CSettings.SETTINGS.itemTypes.put(itemType.name, itemType);
    }

    public static void create2HAxeHandleItemType()
    {
        //Item type
        CItemType itemType = new CItemType();
        itemType.name = "2H Axe Handle";
        itemType.traitLevelMultiplier = 2;


        CRTraitElement_ActiveAttributeMod attributeElement = new CRTraitElement_ActiveAttributeMod();
        attributeElement.attributeName = "generic.attackSpeed";
        attributeElement.minAmount = 0.5;
        attributeElement.maxAmount = 1;

        CRecalculableTrait trait = new CRecalculableTrait();
        trait.name = "Attack Speed";
        trait.elements.add(attributeElement);
        trait.minValue = 1;
        trait.maxValue = 3;
        itemType.staticRecalculableTraits.put(trait.name, trait);


        CRTraitElement_TextureLayers textureElement;
        ArrayList<String> layerGroup;
        for (int state : new int[]{STATE_EMPTY, STATE_UNUSABLE, STATE_USABLE, STATE_FULL})
        {
            textureElement = new CRTraitElement_TextureLayers();
            layerGroup = new ArrayList<>();
            layerGroup.add("equipment/axe:1:ffffffff");
            textureElement.layerGroups.put(state, layerGroup);

            trait = new CRecalculableTrait();
            trait.name = "Layer Group " + state;
            trait.elements.add(textureElement);
            trait.minValue = 0;
            trait.maxValue = 0;
            trait.addToCoreOnAssembly = false;
            itemType.staticRecalculableTraits.put(trait.name, trait);
        }


        LinkedHashMap<String, CRecalculableTraitPool> poolSet = new LinkedHashMap<>();
        poolSet.put("General", CSettings.SETTINGS.recalcTraitPools.get("General"));
        itemType.randomRecalculableTraitPoolSets.put("Gen", poolSet);


        CSettings.SETTINGS.itemTypes.put(itemType.name, itemType);
    }

    public static void create2HAxeSkinItemType()
    {
        //Item type
        CItemType itemType = new CItemType();
        itemType.name = "2H Axe Skin";
        itemType.traitLevelMultiplier = 2;


        CUTraitElement_AWSkin skinElement = new CUTraitElement_AWSkin();
        skinElement.libraryFileOrFolder = "official";
        skinElement.isRandomFromFolder = true;
        skinElement.skinType = "armourers:axe";

        CRandomRGB rgb = new CRandomRGB();
        rgb.rMin = 125;
        rgb.gMax = 125;
        CRGBBoost boost = new CRGBBoost();
        boost.toAdd[1] = 255;
        rgb.functions.add(boost);
        skinElement.dyeChannels.put(0, rgb);

        rgb = new CRandomRGB();
        rgb.functions.add(new CRGBGrayscale());
        skinElement.dyeChannels.put(0, rgb);

        CUnrecalculableTrait trait = new CUnrecalculableTrait();
        trait.name = "Random Axe Skin";
        trait.elements.add(skinElement);
        trait.minValue = 10;
        trait.maxValue = 10;
        itemType.staticUnrecalculableTraits.put(trait.name, trait);


        CSettings.SETTINGS.itemTypes.put(itemType.name, itemType);
    }


    public static void createChestplateItemType()
    {
        //Item type
        CItemType itemType = new CItemType();
        itemType.name = "Chestplate";
        itemType.slotting = "Chest";
        itemType.traitLevelMultiplier = 1;


        CRTraitElement_ForcedAWSkinTypeOverride skinTypeOverrideElement = new CRTraitElement_ForcedAWSkinTypeOverride();
        skinTypeOverrideElement.skinType = "chest";

        CRecalculableTrait trait = new CRecalculableTrait();
        trait.name = "Chest Skin Override";
        trait.elements.add(skinTypeOverrideElement);
        trait.minValue = 1;
        trait.maxValue = 1;
        itemType.staticRecalculableTraits.put(trait.name, trait);


        HashSet<String> validItemTypes = new HashSet<>();
        validItemTypes.add("Chestplate Plates");
        PartSlot.validItemTypes.put("Chestplate Plates", validItemTypes);

        CRTraitElement_PartSlot partSlot = new CRTraitElement_PartSlot();
        partSlot.partSlotType = "Chestplate Plates";
        partSlot.required = true;
        partSlot.minCount = 1;

        trait = new CRecalculableTrait();
        trait.name = "Chestplate Plates Slot";
        trait.elements.add(partSlot);
        trait.minValue = 0;
        trait.maxValue = 0;
        itemType.staticRecalculableTraits.put(trait.name, trait);


        validItemTypes = new HashSet<>();
        validItemTypes.add("Chestplate Straps");
        PartSlot.validItemTypes.put("Chestplate Straps", validItemTypes);

        partSlot = new CRTraitElement_PartSlot();
        partSlot.partSlotType = "Chestplate Straps";
        partSlot.required = true;
        partSlot.minCount = 1;

        trait = new CRecalculableTrait();
        trait.name = "Chestplate Straps Slot";
        trait.elements.add(partSlot);
        trait.minValue = 0;
        trait.maxValue = 0;
        itemType.staticRecalculableTraits.put(trait.name, trait);


        validItemTypes = new HashSet<>();
        validItemTypes.add("Chestplate Skin");
        PartSlot.validItemTypes.put("Chestplate Skin", validItemTypes);

        partSlot = new CRTraitElement_PartSlot();
        partSlot.partSlotType = "Chestplate Skin";
        partSlot.required = true;
        partSlot.minCount = 1;

        trait = new CRecalculableTrait();
        trait.name = "Chestplate Skin Slot";
        trait.elements.add(partSlot);
        trait.minValue = 0;
        trait.maxValue = 0;
        itemType.staticRecalculableTraits.put(trait.name, trait);


        CRTraitElement_TextureLayers textureElement;
        ArrayList<String> layerGroup;
        for (int state : new int[]{STATE_USABLE, STATE_FULL})
        {
            textureElement = new CRTraitElement_TextureLayers();
            layerGroup = new ArrayList<>();
            layerGroup.add("equipment/metalarmor:2:ffffffff");
            textureElement.layerGroups.put(state, layerGroup);

            trait = new CRecalculableTrait();
            trait.name = "Layer Group " + state;
            trait.elements.add(textureElement);
            trait.minValue = 0;
            trait.maxValue = 0;
            trait.addToCoreOnAssembly = false;
            itemType.staticRecalculableTraits.put(trait.name, trait);
        }


        LinkedHashMap<String, CRecalculableTraitPool> poolSet = new LinkedHashMap<>();
        poolSet.put("General", CSettings.SETTINGS.recalcTraitPools.get("General"));
        itemType.randomRecalculableTraitPoolSets.put("Gen", poolSet);

        poolSet = new LinkedHashMap<>();
        poolSet.put("Sockets", CSettings.SETTINGS.recalcTraitPools.get("Sockets"));
        itemType.randomRecalculableTraitPoolSets.put("Socket", poolSet);


        CSettings.SETTINGS.itemTypes.put(itemType.name, itemType);
    }

    public static void createChestplatePlatesItemType()
    {
        //Item type
        CItemType itemType = new CItemType();
        itemType.name = "Chestplate Plates";
        itemType.traitLevelMultiplier = 2;


        CRTraitElement_ActiveAttributeMod element = new CRTraitElement_ActiveAttributeMod();
        element.attributeName = "generic.armor";
        element.minAmount = 1;
        element.maxAmount = 3;

        CRecalculableTrait trait = new CRecalculableTrait();
        trait.name = "Armor";
        trait.elements.add(element);
        trait.minValue = 1;
        trait.maxValue = 4;
        itemType.staticRecalculableTraits.put(trait.name, trait);


        CRTraitElement_TextureLayers textureElement;
        ArrayList<String> layerGroup;
        for (int state : new int[]{STATE_EMPTY, STATE_UNUSABLE, STATE_USABLE, STATE_FULL})
        {
            textureElement = new CRTraitElement_TextureLayers();
            layerGroup = new ArrayList<>();
            layerGroup.add("equipment/shield:1:ffffffff");
            textureElement.layerGroups.put(state, layerGroup);

            trait = new CRecalculableTrait();
            trait.name = "Layer Group " + state;
            trait.elements.add(textureElement);
            trait.minValue = 0;
            trait.maxValue = 0;
            trait.addToCoreOnAssembly = false;
            itemType.staticRecalculableTraits.put(trait.name, trait);
        }


        LinkedHashMap<String, CRecalculableTraitPool> poolSet = new LinkedHashMap<>();
        poolSet.put("General", CSettings.SETTINGS.recalcTraitPools.get("General"));
        itemType.randomRecalculableTraitPoolSets.put("Gen", poolSet);


        CSettings.SETTINGS.itemTypes.put(itemType.name, itemType);
    }

    public static void createChestplateStrapsItemType()
    {
        //Item type
        CItemType itemType = new CItemType();
        itemType.name = "Chestplate Straps";
        itemType.traitLevelMultiplier = 2;


        CRTraitElement_ActiveAttributeMod attributeElement = new CRTraitElement_ActiveAttributeMod();
        attributeElement.attributeName = "generic.armorToughness";
        attributeElement.minAmount = 0;
        attributeElement.maxAmount = 1;

        CRecalculableTrait trait = new CRecalculableTrait();
        trait.name = "Armor Toughness";
        trait.elements.add(attributeElement);
        trait.minValue = 1;
        trait.maxValue = 3;
        itemType.staticRecalculableTraits.put(trait.name, trait);


        CRTraitElement_TextureLayers textureElement;
        ArrayList<String> layerGroup;
        for (int state : new int[]{STATE_EMPTY, STATE_UNUSABLE, STATE_USABLE, STATE_FULL})
        {
            textureElement = new CRTraitElement_TextureLayers();
            layerGroup = new ArrayList<>();
            layerGroup.add("equipment/shield:2:ffffffff");
            textureElement.layerGroups.put(state, layerGroup);

            trait = new CRecalculableTrait();
            trait.name = "Layer Group " + state;
            trait.elements.add(textureElement);
            trait.minValue = 0;
            trait.maxValue = 0;
            trait.addToCoreOnAssembly = false;
            itemType.staticRecalculableTraits.put(trait.name, trait);
        }


        LinkedHashMap<String, CRecalculableTraitPool> poolSet = new LinkedHashMap<>();
        poolSet.put("General", CSettings.SETTINGS.recalcTraitPools.get("General"));
        itemType.randomRecalculableTraitPoolSets.put("Gen", poolSet);


        CSettings.SETTINGS.itemTypes.put(itemType.name, itemType);
    }

    public static void createChestplateSkinItemType()
    {
        //Item type
        CItemType itemType = new CItemType();
        itemType.name = "Chestplate Skin";
        itemType.traitLevelMultiplier = 2;


        CUTraitElement_AWSkin skinElement = new CUTraitElement_AWSkin();
        skinElement.libraryFileOrFolder = "official";
        skinElement.isRandomFromFolder = true;
        skinElement.isTransient = true;
        skinElement.skinType = "armourers:chest";
        skinElement.indexWithinSkinTypeIfTransient = 1;

        CRandomRGB rgb = new CRandomRGB();
        rgb.rMin = 125;
        rgb.gMax = 125;
        CRGBBoost boost = new CRGBBoost();
        boost.toAdd[1] = 255;
        rgb.functions.add(boost);
        skinElement.dyeChannels.put(0, rgb);

        rgb = new CRandomRGB();
        rgb.functions.add(new CRGBGrayscale());
        skinElement.dyeChannels.put(0, rgb);

        CUnrecalculableTrait trait = new CUnrecalculableTrait();
        trait.name = "Random Chestplate Skin";
        trait.elements.add(skinElement);
        trait.minValue = 10;
        trait.maxValue = 10;
        itemType.staticUnrecalculableTraits.put(trait.name, trait);


        CSettings.SETTINGS.itemTypes.put(itemType.name, itemType);
    }
}
