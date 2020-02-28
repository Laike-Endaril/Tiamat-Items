package com.fantasticsource.tiamatitems;

import com.fantasticsource.tiamatitems.assembly.PartSlot;
import com.fantasticsource.tiamatitems.globalsettings.CRarity;
import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTrait;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitPool;
import com.fantasticsource.tiamatitems.trait.recalculable.element.*;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.util.text.TextFormatting;

import java.util.HashSet;
import java.util.LinkedHashMap;

public class Test
{
    public static void createRarity()
    {
        CRarity rarity = new CRarity();
        rarity.name = "TestRarity";
        rarity.textColor = TextFormatting.GOLD;
        rarity.color = Color.ORANGE;
        rarity.itemLevelModifier = 0.5;
        rarity.traitCounts.put("ActLC", 1); //1x left click action
        rarity.traitCounts.put("ActRC", 1); //1x right click action
        rarity.traitCounts.put("Gen", 1); //1x general trait
        rarity.traitCounts.put("Socket", 6); //6x part slot trait (each generates 0-1 slots)

        CRarity.rarities.put(rarity.name, rarity);
    }


    public static void createGeneralPool()
    {
        CRecalculableTraitPool pool = new CRecalculableTraitPool();
        pool.name = "General";
        CRecalculableTraitPool.pools.put(pool.name, pool);

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
    }

    public static void createSocketPool()
    {
        HashSet<String> validItemTypes = new HashSet<>();
        validItemTypes.add("Gem");
        validItemTypes.add("Rune");
        PartSlot.validItemTypes.put("Socket", validItemTypes);

        CRecalculableTraitPool pool = new CRecalculableTraitPool();
        pool.name = "Sockets";
        CRecalculableTraitPool.pools.put(pool.name, pool);

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
        CRecalculableTraitPool.pools.put(pool.name, pool);


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
        CRecalculableTraitPool.pools.put(pool.name, pool);


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
        itemType.percentageMultiplier = 2;


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
        trait.name = "2HAxehead";
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
        trait.name = "2HAxeHandle";
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
        trait.name = "2HAxeSkin";
        trait.elements.add(partSlot);
        trait.minValue = 0;
        trait.maxValue = 0;
        itemType.staticRecalculableTraits.put(trait.name, trait);


        LinkedHashMap<String, CRecalculableTraitPool> poolSet = new LinkedHashMap<>();
        poolSet.put("2HAxeActLC", CRecalculableTraitPool.pools.get("2HAxeActLC"));
        itemType.randomRecalculableTraitPoolSets.put("ActLC", poolSet);

        poolSet = new LinkedHashMap<>();
        poolSet.put("2HAxeActRC", CRecalculableTraitPool.pools.get("2HAxeActRC"));
        itemType.randomRecalculableTraitPoolSets.put("ActRC", poolSet);

        poolSet = new LinkedHashMap<>();
        poolSet.put("General", CRecalculableTraitPool.pools.get("General"));
        itemType.randomRecalculableTraitPoolSets.put("Gen", poolSet);

        poolSet = new LinkedHashMap<>();
        poolSet.put("Sockets", CRecalculableTraitPool.pools.get("Sockets"));
        itemType.randomRecalculableTraitPoolSets.put("Socket", poolSet);


        CItemType.itemTypes.put(itemType.name, itemType);
    }

    public static void create2HAxeheadItemType()
    {
        //Item type
        CItemType itemType = new CItemType();
        itemType.name = "2H Axehead";
        itemType.percentageMultiplier = 2;


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


        LinkedHashMap<String, CRecalculableTraitPool> poolSet = new LinkedHashMap<>();
        poolSet.put("General", CRecalculableTraitPool.pools.get("General"));
        itemType.randomRecalculableTraitPoolSets.put("Gen", poolSet);


        CItemType.itemTypes.put(itemType.name, itemType);
    }

    public static void create2HAxeSkinItemType()
    {
        //Item type
        CItemType itemType = new CItemType();
        itemType.name = "2H Axe Skin";
        itemType.percentageMultiplier = 2;


        CRTraitElement_AWSkin skinElement = new CRTraitElement_AWSkin();
        skinElement.libraryFile = "downloads/5080 - Storm Breaker";
        skinElement.skinType = "armourers:axe";
        skinElement.dyes.add(new Color(255, 0, 0, 255));
        skinElement.dyes.add(new Color(0, 255, 0, 255));
        skinElement.dyes.add(new Color(0, 0, 255, 255));

        CRecalculableTrait trait = new CRecalculableTrait();
        trait.name = "Storm Breaker";
        trait.elements.add(skinElement);
        trait.minValue = 10;
        trait.maxValue = 10;
        itemType.staticRecalculableTraits.put(trait.name, trait);


        CItemType.itemTypes.put(itemType.name, itemType);
    }
}
