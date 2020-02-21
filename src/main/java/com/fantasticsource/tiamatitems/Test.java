package com.fantasticsource.tiamatitems;

import com.fantasticsource.tiamatitems.assembly.PartSlot;
import com.fantasticsource.tiamatitems.globalsettings.CRarity;
import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tiamatitems.trait.CTrait;
import com.fantasticsource.tiamatitems.trait.CTraitPool;
import com.fantasticsource.tiamatitems.trait.element.*;
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
        CTraitPool pool = new CTraitPool();
        pool.name = "General";
        CTraitPool.traitGenPools.put(pool.name, pool);

        CTraitElement_PassiveAttributeMod passiveAttributeMod = new CTraitElement_PassiveAttributeMod();
        passiveAttributeMod.attributeName = "generic.maxHealth";
        passiveAttributeMod.minimum = 1;
        passiveAttributeMod.maximum = 3;

        CTrait trait = new CTrait();
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

        CTraitPool pool = new CTraitPool();
        pool.name = "Sockets";
        CTraitPool.traitGenPools.put(pool.name, pool);

        CTraitElement_PartSlot partSlot = new CTraitElement_PartSlot();
        partSlot.partSlotType = "Socket";
        partSlot.minimum = 0;
        partSlot.maximum = 1;

        CTrait trait;
        for (int i = 0; i < 10; i++)
        {
            trait = new CTrait();
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
        CTraitPool pool = new CTraitPool();
        pool.name = "2HAxeActLC";
        CTraitPool.traitGenPools.put(pool.name, pool);


        CTraitElement_LeftClickAction leftClickAction = new CTraitElement_LeftClickAction();
        leftClickAction.actionName = "2HAxeAttack1";

        CTrait trait = new CTrait();
        trait.name = "2HAxeAttack1";
        trait.elements.add(leftClickAction);
        trait.minValue = 3;
        trait.maxValue = 3;
        pool.traitGenWeights.put(trait, 1);


        //Sub action traits
        pool = new CTraitPool();
        pool.name = "2HAxeActRC";
        CTraitPool.traitGenPools.put(pool.name, pool);


        CTraitElement_RightClickAction rightClickAction = new CTraitElement_RightClickAction();
        rightClickAction.actionName = "2HAxeAttack2";

        trait = new CTrait();
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


        HashSet<String> validItemTypes = new HashSet<>();
        validItemTypes.add("2H Axehead");
        PartSlot.validItemTypes.put("2H Axehead", validItemTypes);

        CTraitElement_PartSlot partSlot = new CTraitElement_PartSlot();
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


        validItemTypes = new HashSet<>();
        validItemTypes.add("2H Axe Handle");
        PartSlot.validItemTypes.put("2H Axe Handle", validItemTypes);

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


        validItemTypes = new HashSet<>();
        validItemTypes.add("2H Axe Skin");
        PartSlot.validItemTypes.put("2H Axe Skin", validItemTypes);

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


        LinkedHashMap<String, CTraitPool> poolSet = new LinkedHashMap<>();
        poolSet.put("2HAxeActLC", CTraitPool.traitGenPools.get("2HAxeActLC"));
        itemType.randomTraitPoolSets.put("ActLC", poolSet);

        poolSet = new LinkedHashMap<>();
        poolSet.put("2HAxeActRC", CTraitPool.traitGenPools.get("2HAxeActRC"));
        itemType.randomTraitPoolSets.put("ActRC", poolSet);

        poolSet = new LinkedHashMap<>();
        poolSet.put("General", CTraitPool.traitGenPools.get("General"));
        itemType.randomTraitPoolSets.put("Gen", poolSet);

        poolSet = new LinkedHashMap<>();
        poolSet.put("Sockets", CTraitPool.traitGenPools.get("Sockets"));
        itemType.randomTraitPoolSets.put("Socket", poolSet);


        CItemType.itemTypes.put(itemType.name, itemType);
    }

    public static void create2HAxeheadItemType()
    {
        //Item type
        CItemType itemType = new CItemType();
        itemType.name = "2H Axehead";
        itemType.percentageMultiplier = 2;


        CTraitElement_ActiveAttributeMod element = new CTraitElement_ActiveAttributeMod();
        element.attributeName = "generic.attackDamage";
        element.minimum = 1;
        element.maximum = 3;

        CTrait trait = new CTrait();
        trait.name = "Damage";
        trait.elements.add(element);
        trait.minValue = 1;
        trait.maxValue = 3;
        itemType.staticTraits.put(trait.name, trait);


        LinkedHashMap<String, CTraitPool> poolSet = new LinkedHashMap<>();
        poolSet.put("General", CTraitPool.traitGenPools.get("General"));
        itemType.randomTraitPoolSets.put("Gen", poolSet);


        CItemType.itemTypes.put(itemType.name, itemType);
    }

    public static void create2HAxeSkinItemType()
    {
        //Item type
        CItemType itemType = new CItemType();
        itemType.name = "2H Axe Skin";
        itemType.percentageMultiplier = 2;


        CTraitElement_TransientAWSkin element = new CTraitElement_TransientAWSkin();
        element.libraryFile = "downloads/5080 - Storm Breaker";
        element.skinType = "armourers:axe";
        element.dyes.add(new Color(255, 0, 0, 255));
        element.dyes.add(new Color(0, 255, 0, 255));
        element.dyes.add(new Color(0, 0, 255, 255));

        CTrait trait = new CTrait();
        trait.name = "Storm Breaker";
        trait.elements.add(element);
        trait.minValue = 1;
        trait.maxValue = 3;
        itemType.staticTraits.put(trait.name, trait);


        CItemType.itemTypes.put(itemType.name, itemType);
    }
}
