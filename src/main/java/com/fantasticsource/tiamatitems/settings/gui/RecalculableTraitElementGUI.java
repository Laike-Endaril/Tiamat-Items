package com.fantasticsource.tiamatitems.settings.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.Namespace;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.*;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.mctools.gui.element.view.GUIList.Line;
import com.fantasticsource.mctools.gui.screen.TextSelectionGUI;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tiamatitems.trait.recalculable.element.*;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes.CRandomRGB;
import com.fantasticsource.tools.datastructures.Color;
import moe.plushie.armourers_workshop.api.ArmourersWorkshopApi;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinTypeRegistry;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class RecalculableTraitElementGUI extends GUIScreen
{
    public static Color WHITES[] = new Color[]{getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE};

    public static final FilterRangedInt
            OPERATION_FILTER = FilterRangedInt.get(0, 2),
            PART_SLOT_COUNT_FILTER = FilterRangedInt.get(0, Integer.MAX_VALUE),
            AW_SLOT_INDEX_FILTER = FilterRangedInt.get(0, 9),
            AW_DYE_INDEX_FILTER = FilterRangedInt.get(0, 7);

    protected String typeName;

    protected LinkedHashMap<GUIButton, CRandomRGB> editButtonToCRandomRGBMap = new LinkedHashMap<>();

    protected RecalculableTraitElementGUI(String typeName)
    {
        this.typeName = typeName;
    }

    public static RecalculableTraitElementGUI show(String typeName, CRecalculableTraitElement traitElement)
    {
        RecalculableTraitElementGUI gui = new RecalculableTraitElementGUI(typeName);
        showStacked(gui);
        gui.drawStack = false;


        //Background
        gui.root.add(new GUIDarkenedBackground(gui));


        //Header
        GUINavbar navbar = new GUINavbar(gui);
        GUITextButton done = new GUITextButton(gui, "Done", Color.GREEN);
        GUITextButton cancel = new GUITextButton(gui, "Cancel", Color.RED);
        gui.root.addAll(navbar, done, cancel);


        //Main
        GUILabeledBoolean ignoreMultipliers = new GUILabeledBoolean(gui, " Ignore Multipliers: ", traitElement.ignoreMultipliers);
        gui.root.addAll(new GUITextSpacer(gui), ignoreMultipliers);

        if (traitElement.getClass() == CRTraitElement_ActiveAttributeMod.class)
        {
            CRTraitElement_ActiveAttributeMod attributeElement = (CRTraitElement_ActiveAttributeMod) traitElement;

            GUILabeledTextInput attribute = new GUILabeledTextInput(gui, " Attribute Name: ", attributeElement.attributeName.equals("") ? "generic.name" : attributeElement.attributeName, FilterNotEmpty.INSTANCE);
            GUILabeledBoolean isGood = new GUILabeledBoolean(gui, " Is Good Attribute: ", attributeElement.isGood);
            //TODO when editing, instead of giving direct access to operations, give these options: "Adjust Amount (+/-x)", "Adjust Percentage (+/-%)", "Mutliply"
            GUILabeledTextInput operation = new GUILabeledTextInput(gui, " Operation: ", "" + attributeElement.operation, OPERATION_FILTER);
            GUILabeledTextInput minAmount = new GUILabeledTextInput(gui, " Min Amount: ", "" + attributeElement.minAmount, FilterFloat.INSTANCE);
            GUILabeledTextInput maxAmount = new GUILabeledTextInput(gui, " Max Amount: ", "" + attributeElement.maxAmount, FilterFloat.INSTANCE);
            gui.root.addAll(
                    new GUITextSpacer(gui),
                    attribute,
                    new GUITextSpacer(gui),
                    isGood,
                    new GUITextSpacer(gui),
                    operation,
                    new GUITextSpacer(gui),
                    minAmount,
                    new GUITextSpacer(gui),
                    maxAmount
            );

            //Add main header actions
            done.addClickActions(() ->
            {
                //Validation
                if (!attribute.valid() || !operation.valid() || !minAmount.valid() || !maxAmount.valid()) return;


                //Processing
                traitElement.ignoreMultipliers = ignoreMultipliers.getValue();

                attributeElement.attributeName = attribute.getText();
                attributeElement.isGood = isGood.getValue();
                attributeElement.operation = FilterInt.INSTANCE.parse(operation.getText());
                attributeElement.minAmount = FilterFloat.INSTANCE.parse(minAmount.getText());
                attributeElement.maxAmount = FilterFloat.INSTANCE.parse(maxAmount.getText());


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement.getClass() == CRTraitElement_PassiveAttributeMod.class)
        {
            CRTraitElement_PassiveAttributeMod attributeElement = (CRTraitElement_PassiveAttributeMod) traitElement;

            GUILabeledTextInput attribute = new GUILabeledTextInput(gui, " Attribute Name: ", attributeElement.attributeName.equals("") ? "generic.name" : attributeElement.attributeName, FilterNotEmpty.INSTANCE);
            GUILabeledBoolean isGood = new GUILabeledBoolean(gui, " Is Good Attribute: ", attributeElement.isGood);
            //TODO when editing, instead of giving direct access to operations, give these options: "Adjust Amount (+/-x)", "Adjust Percentage (+/-%)", "Mutliply"
            GUILabeledTextInput operation = new GUILabeledTextInput(gui, " Operation: ", "" + attributeElement.operation, OPERATION_FILTER);
            GUILabeledTextInput minAmount = new GUILabeledTextInput(gui, " Min Amount: ", "" + attributeElement.minAmount, FilterFloat.INSTANCE);
            GUILabeledTextInput maxAmount = new GUILabeledTextInput(gui, " Max Amount: ", "" + attributeElement.maxAmount, FilterFloat.INSTANCE);
            gui.root.addAll(
                    new GUITextSpacer(gui),
                    attribute,
                    new GUITextSpacer(gui),
                    isGood,
                    new GUITextSpacer(gui),
                    operation,
                    new GUITextSpacer(gui),
                    minAmount,
                    new GUITextSpacer(gui),
                    maxAmount
            );

            //Add main header actions
            done.addClickActions(() ->
            {
                //Validation
                if (!attribute.valid() || !operation.valid() || !minAmount.valid() || !maxAmount.valid()) return;


                //Processing
                traitElement.ignoreMultipliers = ignoreMultipliers.getValue();

                attributeElement.attributeName = attribute.getText();
                attributeElement.isGood = isGood.getValue();
                attributeElement.operation = FilterInt.INSTANCE.parse(operation.getText());
                attributeElement.minAmount = FilterFloat.INSTANCE.parse(minAmount.getText());
                attributeElement.maxAmount = FilterFloat.INSTANCE.parse(maxAmount.getText());


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement.getClass() == CRTraitElement_PartSlot.class)
        {
            CRTraitElement_PartSlot partSlotElement = (CRTraitElement_PartSlot) traitElement;

            GUILabeledTextInput partSlotType = new GUILabeledTextInput(gui, " Part Slot Type: ", partSlotElement.partSlotType.equals("") ? "PartSlotType" : partSlotElement.partSlotType, FilterNotEmpty.INSTANCE);
            GUILabeledBoolean required = new GUILabeledBoolean(gui, " Required: ", partSlotElement.required);
            GUILabeledTextInput minCount = new GUILabeledTextInput(gui, " Min Count: ", "" + partSlotElement.minCount, PART_SLOT_COUNT_FILTER);
            GUILabeledTextInput maxCount = new GUILabeledTextInput(gui, " Max Count: ", "" + partSlotElement.maxCount, PART_SLOT_COUNT_FILTER);
            gui.root.addAll(
                    new GUITextSpacer(gui),
                    partSlotType,
                    new GUITextSpacer(gui),
                    required,
                    new GUITextSpacer(gui),
                    minCount,
                    new GUITextSpacer(gui),
                    maxCount
            );

            //Add main header actions
            done.addClickActions(() ->
            {
                //Validation
                if (!partSlotType.valid() || !minCount.valid() || !maxCount.valid()) return;


                //Processing
                traitElement.ignoreMultipliers = ignoreMultipliers.getValue();

                partSlotElement.partSlotType = partSlotType.getText();
                partSlotElement.required = required.getValue();
                partSlotElement.minCount = PART_SLOT_COUNT_FILTER.parse(minCount.getText());
                partSlotElement.maxCount = PART_SLOT_COUNT_FILTER.parse(maxCount.getText());


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement.getClass() == CRTraitElement_TextureLayers.class)
        {
            CRTraitElement_TextureLayers textureElement = (CRTraitElement_TextureLayers) traitElement;

            GUILabeledBoolean cacheLayers = new GUILabeledBoolean(gui, " Cache Layers: ", textureElement.cacheLayers);
            GUILabeledBoolean cacheTextures = new GUILabeledBoolean(gui, " Cache Textures: ", textureElement.cacheTextures);

            GUITextButton emptyLayers = new GUITextButton(gui, "'Empty Item' Layers");
            GUITextButton unusableLayers = new GUITextButton(gui, "'Unusable Item' Layers");
            GUITextButton usableLayers = new GUITextButton(gui, "'Usable Item' Layers");
            GUITextButton fullLayers = new GUITextButton(gui, "'Full Item' Layers");

            emptyLayers.addClickActions(() -> TextureLayersGUI.show(textureElement, AssemblyTags.STATE_EMPTY));
            unusableLayers.addClickActions(() -> TextureLayersGUI.show(textureElement, AssemblyTags.STATE_UNUSABLE));
            usableLayers.addClickActions(() -> TextureLayersGUI.show(textureElement, AssemblyTags.STATE_USABLE));
            fullLayers.addClickActions(() -> TextureLayersGUI.show(textureElement, AssemblyTags.STATE_FULL));

            gui.root.addAll(
                    new GUITextSpacer(gui),
                    cacheLayers,
                    new GUITextSpacer(gui),
                    cacheTextures,
                    new GUITextSpacer(gui),
                    emptyLayers,
                    new GUIElement(gui, 1, 0),
                    unusableLayers,
                    new GUIElement(gui, 1, 0),
                    usableLayers,
                    new GUIElement(gui, 1, 0),
                    fullLayers
            );


            //Add main header actions
            done.addClickActions(() ->
            {
                //Processing
                traitElement.ignoreMultipliers = ignoreMultipliers.getValue();

                textureElement.cacheLayers = cacheLayers.getValue();
                textureElement.cacheTextures = cacheTextures.getValue();


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement.getClass() == CRTraitElement_AWSkin.class)
        {
            CRTraitElement_AWSkin skinElement = (CRTraitElement_AWSkin) traitElement;

            GUILabeledTextInput libraryFileOrFolder = new GUILabeledTextInput(gui, " Library File: ", (skinElement.libraryFileOrFolder.equals("") ? "LibraryFileOrFolder" : skinElement.libraryFileOrFolder), FilterNotEmpty.INSTANCE);
            GUILabeledBoolean isRandomFromFolder = new GUILabeledBoolean(gui, " Is Random From Folder: ", skinElement.isRandomFromFolder);

            ArrayList<String> skinTypeArray = new ArrayList<>();
            skinTypeArray.add("(None)");
            ISkinTypeRegistry skinTypeRegistry = ArmourersWorkshopApi.getSkinTypeRegistry();
            if (skinTypeRegistry != null)
            {
                for (ISkinType skinType : skinTypeRegistry.getRegisteredSkinTypes()) skinTypeArray.add(skinType.getRegistryName());
            }
            String[] skinTypes = skinTypeArray.toArray(new String[0]);
            GUIText skinTypeLabel = new GUIText(gui, " Skin Type: ").setColor(WHITES[0], WHITES[1], WHITES[2]);
            GUIText skinType = new GUIText(gui, skinElement.skinType.equals("") ? "(None)" : skinElement.skinType).setColor(WHITES[0], WHITES[1], WHITES[2]);
            skinTypeLabel.addClickActions(skinType::click).linkMouseActivity(skinType);
            skinType.addClickActions(() -> new TextSelectionGUI(skinType, "Select AW Skin Type", skinTypes)).linkMouseActivity(skinTypeLabel);

            GUILabeledBoolean isTransient = new GUILabeledBoolean(gui, " Transient: ", skinElement.isTransient);
            GUILabeledTextInput indexWithinSkinTypeIfTransient = new GUILabeledTextInput(gui, " Wardrobe Slot Index Within Skin Type (if Transient): ", "" + skinElement.indexWithinSkinTypeIfTransient, AW_SLOT_INDEX_FILTER);
            GUIGradientBorder separator = new GUIGradientBorder(gui, 1, 0.02, 0.3, Color.WHITE, Color.BLANK);
            gui.root.addAll(
                    new GUITextSpacer(gui),
                    libraryFileOrFolder,
                    new GUITextSpacer(gui),
                    isRandomFromFolder,
                    new GUITextSpacer(gui),
                    skinTypeLabel, skinType,
                    new GUITextSpacer(gui),
                    isTransient,
                    new GUITextSpacer(gui),
                    indexWithinSkinTypeIfTransient,
                    new GUITextSpacer(gui),
                    new GUIText(gui, " Dyes...", Color.YELLOW),
                    separator
            );

            GUIList dyes = new GUIList(gui, true, 0.98, 1 - (separator.y + separator.height))
            {
                @Override
                public GUIElement[] newLineDefaultElements()
                {
                    GUIButton editButton = GUIButton.newEditButton(gui);
                    gui.editButtonToCRandomRGBMap.put(editButton, new CRandomRGB());

                    Namespace namespace = gui.namespaces.computeIfAbsent("Dye Indices", o -> new Namespace());
                    int index = 0;
                    while (namespace.contains("" + index)) index++;

                    GUILabeledTextInput dyeIndex = new GUILabeledTextInput(gui, " Dye Index: ", "" + index, AW_DYE_INDEX_FILTER).setNamespace("Dye Indices");

                    GUIButton duplicateButton = GUIButton.newDuplicateButton(screen);
                    duplicateButton.addClickActions(() ->
                    {
                        Line line = addLine(getLineIndexContaining(dyeIndex) + 1);

                        GUIButton editButton2 = (GUIButton) line.getLineElement(1);
                        gui.editButtonToCRandomRGBMap.put(editButton2, (CRandomRGB) gui.editButtonToCRandomRGBMap.get(editButton).copy());
                    });

                    return new GUIElement[]{
                            duplicateButton,
                            editButton.addClickActions(() -> CRandomRGBGUI.show(gui.editButtonToCRandomRGBMap.get(editButton), FilterInt.INSTANCE.parse(dyeIndex.getText()))),
                            dyeIndex
                    };
                }
            };
            dyes.addRemoveChildActions((Predicate<GUIElement>) element ->
            {
                if (element instanceof Line)
                {
                    Line line = (Line) element;
                    GUILabeledTextInput labeledTextInput = (GUILabeledTextInput) line.getLineElement(2);
                    gui.namespaces.get("Dye Indices").inputs.remove(labeledTextInput.input);
                }
                return false;
            });
            GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (separator.y + separator.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, dyes);
            gui.root.addAll(dyes, scrollbar);
            for (Map.Entry<Integer, CRandomRGB> entry : skinElement.dyeChannels.entrySet())
            {
                Line line = dyes.addLine();
                GUIButton editButton = (GUIButton) line.getLineElement(1);
                gui.editButtonToCRandomRGBMap.put(editButton, entry.getValue());

                ((GUILabeledTextInput) line.getLineElement(2)).setText("" + entry.getKey());
            }


            //Add main header actions
            separator.addRecalcActions(() ->
            {
                dyes.height = 1 - (separator.y + separator.height);
                scrollbar.height = 1 - (separator.y + separator.height);
            });
            done.addClickActions(() ->
            {
                //Validation
                if (!libraryFileOrFolder.valid() || !indexWithinSkinTypeIfTransient.valid()) return;
                for (Line line : dyes.getLines())
                {
                    if (!((GUILabeledTextInput) line.getLineElement(2)).valid()) return;
                }


                //Processing
                traitElement.ignoreMultipliers = ignoreMultipliers.getValue();

                skinElement.libraryFileOrFolder = libraryFileOrFolder.getText();
                skinElement.isRandomFromFolder = isRandomFromFolder.getValue();
                skinElement.skinType = skinType.getText().equals("(None)") ? "" : skinType.getText();
                skinElement.isTransient = isTransient.getValue();
                skinElement.indexWithinSkinTypeIfTransient = AW_SLOT_INDEX_FILTER.parse(indexWithinSkinTypeIfTransient.getText());

                skinElement.dyeChannels.clear();
                for (Line line : dyes.getLines())
                {
                    GUIButton editButton = (GUIButton) line.getLineElement(1);
                    GUILabeledTextInput index = (GUILabeledTextInput) line.getLineElement(2);
                    skinElement.dyeChannels.put(AW_DYE_INDEX_FILTER.parse(index.getText()), gui.editButtonToCRandomRGBMap.get(editButton));
                }


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement.getClass() == CRTraitElement_ForcedAWSkinTypeOverride.class)
        {
            CRTraitElement_ForcedAWSkinTypeOverride overrideElement = (CRTraitElement_ForcedAWSkinTypeOverride) traitElement;

            String[] skinTypes = new String[]{"(None)", "sword", "shield", "bow", "pickaxe", "axe", "shovel", "hoe", "item"};
            GUIText skinTypeLabel = new GUIText(gui, " Skin Type: ").setColor(WHITES[0], WHITES[1], WHITES[2]);
            GUIText skinType = new GUIText(gui, overrideElement.skinType.equals("") ? "(None)" : overrideElement.skinType).setColor(WHITES[0], WHITES[1], WHITES[2]);
            skinTypeLabel.addClickActions(skinType::click).linkMouseActivity(skinType);
            skinType.addClickActions(() -> new TextSelectionGUI(skinType, "Select AW Skin Type", skinTypes)).linkMouseActivity(skinTypeLabel);

            gui.root.addAll(new GUITextSpacer(gui), skinTypeLabel, skinType);


            //Add main header actions
            done.addClickActions(() ->
            {
                //Processing
                traitElement.ignoreMultipliers = ignoreMultipliers.getValue();

                overrideElement.skinType = skinType.getText().equals("(None)") ? "" : skinType.getText();


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement.getClass() == CRTraitElement_AssemblyName.class)
        {
            CRTraitElement_AssemblyName nameElement = (CRTraitElement_AssemblyName) traitElement;
            GUILabeledTextInput skinType = new GUILabeledTextInput(gui, " Assembly Name: ", nameElement.assemblyName, FilterNotEmpty.INSTANCE);
            gui.root.addAll(new GUITextSpacer(gui), skinType);


            //Add main header actions
            done.addClickActions(() ->
            {
                //Validation
                if (!skinType.valid()) return;


                //Processing
                traitElement.ignoreMultipliers = ignoreMultipliers.getValue();

                nameElement.assemblyName = skinType.getText();


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement.getClass() == CRTraitElement_Durability.class)
        {
            CRTraitElement_Durability durabilityElement = (CRTraitElement_Durability) traitElement;

            GUILabeledTextInput minAmount = new GUILabeledTextInput(gui, " Min Amount: ", "" + durabilityElement.minAmount, FilterFloat.INSTANCE);
            GUILabeledTextInput maxAmount = new GUILabeledTextInput(gui, " Max Amount: ", "" + durabilityElement.maxAmount, FilterFloat.INSTANCE);
            gui.root.addAll(
                    new GUITextSpacer(gui),
                    minAmount,
                    new GUITextSpacer(gui),
                    maxAmount
            );

            //Add main header actions
            done.addClickActions(() ->
            {
                //Validation
                if (!minAmount.valid() || !maxAmount.valid()) return;


                //Processing
                traitElement.ignoreMultipliers = ignoreMultipliers.getValue();

                durabilityElement.minAmount = FilterFloat.INSTANCE.parse(minAmount.getText());
                durabilityElement.maxAmount = FilterFloat.INSTANCE.parse(maxAmount.getText());


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement.getClass() == CRTraitElement_InventorySlots.class)
        {
            CRTraitElement_InventorySlots inventorySlotsElement = (CRTraitElement_InventorySlots) traitElement;

            GUILabeledTextInput minCount = new GUILabeledTextInput(gui, " Min Count: ", "" + inventorySlotsElement.minCount, FilterInt.INSTANCE);
            GUILabeledTextInput maxCount = new GUILabeledTextInput(gui, " Max Count: ", "" + inventorySlotsElement.maxCount, FilterInt.INSTANCE);
            gui.root.addAll(
                    new GUITextSpacer(gui),
                    minCount,
                    new GUITextSpacer(gui),
                    maxCount
            );

            //Add main header actions
            done.addClickActions(() ->
            {
                //Validation
                if (!minCount.valid() || !maxCount.valid()) return;


                //Processing
                traitElement.ignoreMultipliers = ignoreMultipliers.getValue();

                inventorySlotsElement.minCount = FilterInt.INSTANCE.parse(minCount.getText());
                inventorySlotsElement.maxCount = FilterInt.INSTANCE.parse(maxCount.getText());


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement.getClass() == CRTraitElement_GenericDouble.class)
        {
            CRTraitElement_GenericDouble genericDoubleElement = (CRTraitElement_GenericDouble) traitElement;

            GUILabeledTextInput name = new GUILabeledTextInput(gui, " Name: ", genericDoubleElement.name, FilterNotEmpty.INSTANCE);
            GUILabeledTextInput minAmount = new GUILabeledTextInput(gui, " Min Amount: ", "" + genericDoubleElement.minAmount, FilterFloat.INSTANCE);
            GUILabeledTextInput maxAmount = new GUILabeledTextInput(gui, " Max Amount: ", "" + genericDoubleElement.maxAmount, FilterFloat.INSTANCE);
            gui.root.addAll(
                    new GUITextSpacer(gui),
                    name,
                    new GUITextSpacer(gui),
                    minAmount,
                    new GUITextSpacer(gui),
                    maxAmount
            );

            //Add main header actions
            done.addClickActions(() ->
            {
                //Validation
                if (!name.valid() || !minAmount.valid() || !maxAmount.valid()) return;


                //Processing
                traitElement.ignoreMultipliers = ignoreMultipliers.getValue();

                genericDoubleElement.name = FilterNotEmpty.INSTANCE.parse(name.getText());
                genericDoubleElement.minAmount = FilterInt.INSTANCE.parse(minAmount.getText());
                genericDoubleElement.maxAmount = FilterInt.INSTANCE.parse(maxAmount.getText());


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement.getClass() == CRTraitElement_GenericString.class)
        {
            CRTraitElement_GenericString genericStringElement = (CRTraitElement_GenericString) traitElement;

            GUILabeledTextInput name = new GUILabeledTextInput(gui, " Name: ", genericStringElement.name, FilterNotEmpty.INSTANCE);
            GUILabeledTextInput value = new GUILabeledTextInput(gui, " Value: ", "" + genericStringElement.value, FilterNone.INSTANCE);
            gui.root.addAll(
                    new GUITextSpacer(gui),
                    name,
                    new GUITextSpacer(gui),
                    value
            );

            //Add main header actions
            done.addClickActions(() ->
            {
                //Validation
                if (!name.valid() || !value.valid()) return;


                //Processing
                traitElement.ignoreMultipliers = ignoreMultipliers.getValue();

                genericStringElement.name = FilterNotEmpty.INSTANCE.parse(name.getText());
                genericStringElement.value = FilterNone.INSTANCE.parse(value.getText());


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement.getClass() == CRTraitElement_TransformItemOnDrop.class)
        {
            CRTraitElement_TransformItemOnDrop dropTransformElement = (CRTraitElement_TransformItemOnDrop) traitElement;

            GUILabeledTextInput rarity = new GUILabeledTextInput(gui, " Rarity: ", "" + dropTransformElement.rarity, FilterNotEmpty.INSTANCE);
            GUILabeledTextInput minLevel = new GUILabeledTextInput(gui, " Min Level: ", "" + dropTransformElement.minLevel, FilterInt.INSTANCE);
            GUILabeledTextInput maxLevel = new GUILabeledTextInput(gui, " Max Level: ", "" + dropTransformElement.maxLevel, FilterInt.INSTANCE);
            GUIGradientBorder separator = new GUIGradientBorder(gui, 1, 0.02, 0.3, Color.WHITE, Color.BLANK);
            gui.root.addAll(
                    new GUITextSpacer(gui),
                    rarity,
                    new GUITextSpacer(gui),
                    minLevel,
                    new GUITextSpacer(gui),
                    maxLevel,
                    new GUITextSpacer(gui),
                    new GUIText(gui, TextFormatting.YELLOW + "Item Types..."),
                    separator
            );

            GUIList itemTypes = new GUIList(gui, true, 0.98, 1 - (separator.y + separator.height))
            {
                @Override
                public GUIElement[] newLineDefaultElements()
                {
                    return new GUIElement[]{new GUILabeledTextInput(gui, "Item Type: ", "", FilterNotEmpty.INSTANCE)};
                }
            };
            GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (separator.y + separator.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, itemTypes);
            gui.root.addAll(itemTypes, scrollbar);
            for (String itemType : dropTransformElement.itemTypes)
            {
                Line line = itemTypes.addLine();
                ((GUILabeledTextInput) line.getLineElement(0)).setText(itemType);
            }


            //Add main header actions
            separator.addRecalcActions(() ->
            {
                itemTypes.height = 1 - (separator.y + separator.height);
                scrollbar.height = 1 - (separator.y + separator.height);
            });
            done.addClickActions(() ->
            {
                //Validation
                if (!rarity.valid() || !minLevel.valid() || !maxLevel.valid()) return;
                for (Line line : itemTypes.getLines())
                {
                    if (!((GUILabeledTextInput) line.getLineElement(0)).valid()) return;
                }


                //Processing
                traitElement.ignoreMultipliers = ignoreMultipliers.getValue();

                dropTransformElement.rarity = rarity.getText();
                dropTransformElement.minLevel = FilterInt.INSTANCE.parse(minLevel.getText());
                dropTransformElement.maxLevel = FilterInt.INSTANCE.parse(maxLevel.getText());

                dropTransformElement.itemTypes.clear();
                for (Line line : itemTypes.getLines())
                {
                    GUILabeledTextInput itemType = (GUILabeledTextInput) line.getLineElement(0);
                    dropTransformElement.itemTypes.add(itemType.getText());
                }


                //Close GUI
                gui.close();
            });
        }
        else
        {
            gui.root.add(new GUIText(gui, "UNKNOWN TRAIT ELEMENT CLASS: " + traitElement.getClass()));
        }


        //Add main header actions
        cancel.addClickActions(gui::close);


        //Recalc once to fix any colors
        gui.recalc();


        //Return gui reference
        return gui;
    }

    @Override
    public String title()
    {
        return typeName;
    }
}
