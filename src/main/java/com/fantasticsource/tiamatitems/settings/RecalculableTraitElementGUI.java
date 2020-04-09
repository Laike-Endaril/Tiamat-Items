package com.fantasticsource.tiamatitems.settings;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.*;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tiamatitems.trait.recalculable.element.*;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes.CRandomRGB;
import com.fantasticsource.tools.datastructures.Color;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class RecalculableTraitElementGUI extends GUIScreen
{
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
        if (traitElement.getClass() == CRTraitElement_LeftClickAction.class)
        {
            CRTraitElement_LeftClickAction actionElement = (CRTraitElement_LeftClickAction) traitElement;

            GUILabeledTextInput action = new GUILabeledTextInput(gui, " Action Name: ", actionElement.actionName.equals("") ? "ActionName" : actionElement.actionName, FilterNotEmpty.INSTANCE);
            gui.root.addAll(new GUITextSpacer(gui), action);

            //Add main header actions
            done.addClickActions(() ->
            {
                //Validation
                if (!action.valid()) return;


                //Processing
                actionElement.actionName = action.getText();


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement.getClass() == CRTraitElement_RightClickAction.class)
        {
            CRTraitElement_RightClickAction actionElement = (CRTraitElement_RightClickAction) traitElement;

            GUILabeledTextInput action = new GUILabeledTextInput(gui, " Action Name: ", actionElement.actionName.equals("") ? "ActionName" : actionElement.actionName, FilterNotEmpty.INSTANCE);
            gui.root.addAll(new GUITextSpacer(gui), action);

            //Add main header actions
            done.addClickActions(() ->
            {
                //Validation
                if (!action.valid()) return;


                //Processing
                actionElement.actionName = action.getText();


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement.getClass() == CRTraitElement_ActiveAttributeMod.class)
        {
            CRTraitElement_ActiveAttributeMod attributeElement = (CRTraitElement_ActiveAttributeMod) traitElement;

            GUILabeledTextInput attribute = new GUILabeledTextInput(gui, " Attribute Name: ", attributeElement.attributeName.equals("") ? "generic.name" : attributeElement.attributeName, FilterNotEmpty.INSTANCE);
            GUILabeledTextInput isGood = new GUILabeledTextInput(gui, " Is Good Attribute: ", "" + attributeElement.isGood, FilterBoolean.INSTANCE);
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
                if (!attribute.valid() || !isGood.valid() || !operation.valid() || !minAmount.valid() || !maxAmount.valid()) return;


                //Processing
                attributeElement.attributeName = attribute.getText();
                attributeElement.isGood = FilterBoolean.INSTANCE.parse(isGood.getText());
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
            GUILabeledTextInput isGood = new GUILabeledTextInput(gui, " Is Good Attribute: ", "" + attributeElement.isGood, FilterBoolean.INSTANCE);
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
                if (!attribute.valid() || !isGood.valid() || !operation.valid() || !minAmount.valid() || !maxAmount.valid()) return;


                //Processing
                attributeElement.attributeName = attribute.getText();
                attributeElement.isGood = FilterBoolean.INSTANCE.parse(isGood.getText());
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
            GUILabeledTextInput required = new GUILabeledTextInput(gui, " Required: ", "" + partSlotElement.required, FilterBoolean.INSTANCE);
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
                if (!partSlotType.valid() || !required.valid() || !minCount.valid() || !maxCount.valid()) return;


                //Processing
                partSlotElement.partSlotType = partSlotType.getText();
                partSlotElement.required = FilterBoolean.INSTANCE.parse(required.getText());
                partSlotElement.minCount = PART_SLOT_COUNT_FILTER.parse(minCount.getText());
                partSlotElement.maxCount = PART_SLOT_COUNT_FILTER.parse(maxCount.getText());


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement.getClass() == CRTraitElement_TextureLayers.class)
        {
            CRTraitElement_TextureLayers textureElement = (CRTraitElement_TextureLayers) traitElement;

            GUILabeledTextInput cacheLayers = new GUILabeledTextInput(gui, " Cache Layers: ", "" + textureElement.cacheLayers, FilterBoolean.INSTANCE);
            GUILabeledTextInput cacheTextures = new GUILabeledTextInput(gui, " Cache Textures: ", "" + textureElement.cacheTextures, FilterBoolean.INSTANCE);

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
                //Validation
                if (!cacheLayers.valid() || !cacheTextures.valid()) return;


                //Processing
                textureElement.cacheLayers = FilterBoolean.INSTANCE.parse(cacheLayers.getText());
                textureElement.cacheTextures = FilterBoolean.INSTANCE.parse(cacheTextures.getText());


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement.getClass() == CRTraitElement_AWSkin.class)
        {
            CRTraitElement_AWSkin skinElement = (CRTraitElement_AWSkin) traitElement;

            GUILabeledTextInput libraryFileOrFolder = new GUILabeledTextInput(gui, " Library File: ", (skinElement.libraryFileOrFolder.equals("") ? "LibraryFileOrFolder" : skinElement.libraryFileOrFolder), FilterNotEmpty.INSTANCE);
            GUILabeledTextInput isRandomFromFolder = new GUILabeledTextInput(gui, " Is Random From Folder: ", "" + skinElement.isRandomFromFolder, FilterBoolean.INSTANCE);
            GUILabeledTextInput skinType = new GUILabeledTextInput(gui, " Skin Type: ", (skinElement.skinType.equals("") ? "SkinType" : skinElement.skinType), FilterNotEmpty.INSTANCE);
            GUILabeledTextInput isTransient = new GUILabeledTextInput(gui, " Transient: ", "" + skinElement.isTransient, FilterBoolean.INSTANCE);
            GUILabeledTextInput indexWithinSkinTypeIfTransient = new GUILabeledTextInput(gui, " Wardrobe Slot Index Within Skin Type (if Transient): ", "" + skinElement.indexWithinSkinTypeIfTransient, AW_SLOT_INDEX_FILTER);
            GUIGradientBorder separator = new GUIGradientBorder(gui, 1, 0.02, 0.3, Color.WHITE, Color.BLANK);
            gui.root.addAll(
                    new GUITextSpacer(gui),
                    libraryFileOrFolder,
                    new GUITextSpacer(gui),
                    isRandomFromFolder,
                    new GUITextSpacer(gui),
                    skinType,
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

                    int index = 0;
                    String indexString = "" + index;
                    ArrayList<GUITextInput> namespace = gui.namespaces.get("Dye Indices");
                    if (namespace != null)
                    {
                        boolean found = true;
                        while (found)
                        {
                            found = false;
                            indexString = "" + index;
                            for (GUITextInput input : namespace)
                            {
                                if (input.getText().equals(indexString))
                                {
                                    found = true;
                                    index++;
                                    break;
                                }
                            }
                        }
                    }

                    GUILabeledTextInput dyeIndex = new GUILabeledTextInput(gui, " Dye Index: ", indexString, AW_DYE_INDEX_FILTER).setNamespace("Dye Indices");

                    return new GUIElement[]{
                            editButton.addClickActions(() ->
                            {
                                if (dyeIndex.valid()) CRandomRGBGUI.show(gui.editButtonToCRandomRGBMap.get(editButton), FilterInt.INSTANCE.parse(dyeIndex.getText()));
                            }),
                            dyeIndex
                    };
                }
            };
            GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (separator.y + separator.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, dyes);
            gui.root.addAll(dyes, scrollbar);
            for (Map.Entry<Integer, CRandomRGB> entry : skinElement.dyeChannels.entrySet())
            {
                GUIList.Line line = dyes.addLine();
                GUIButton editButton = (GUIButton) line.getLineElement(0);
                gui.editButtonToCRandomRGBMap.put(editButton, entry.getValue());

                ((GUILabeledTextInput) line.getLineElement(1)).setText("" + entry.getKey());
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
                if (!libraryFileOrFolder.valid() || !isRandomFromFolder.valid() || !skinType.valid() || !isTransient.valid() || !indexWithinSkinTypeIfTransient.valid()) return;
                for (GUIList.Line line : dyes.getLines())
                {
                    if (!((GUILabeledTextInput) line.getLineElement(1)).valid()) return;
                }


                //Processing
                skinElement.libraryFileOrFolder = libraryFileOrFolder.getText();
                skinElement.isRandomFromFolder = FilterBoolean.INSTANCE.parse(isRandomFromFolder.getText());
                skinElement.skinType = skinType.getText();
                skinElement.isTransient = FilterBoolean.INSTANCE.parse(isTransient.getText());
                skinElement.indexWithinSkinTypeIfTransient = AW_SLOT_INDEX_FILTER.parse(indexWithinSkinTypeIfTransient.getText());
                skinElement.dyeChannels.clear();
                for (GUIList.Line line : dyes.getLines())
                {
                    GUIButton editButton = (GUIButton) line.getLineElement(0);
                    GUILabeledTextInput index = (GUILabeledTextInput) line.getLineElement(1);
                    skinElement.dyeChannels.put(AW_DYE_INDEX_FILTER.parse(index.getText()), gui.editButtonToCRandomRGBMap.get(editButton));
                }


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement.getClass() == CRTraitElement_ForcedAWSkinTypeOverride.class)
        {
            CRTraitElement_ForcedAWSkinTypeOverride overrideElement = (CRTraitElement_ForcedAWSkinTypeOverride) traitElement;
            GUILabeledTextInput skinType = new GUILabeledTextInput(gui, " Skin Type: ", overrideElement.skinType.equals("") ? "SkinType" : overrideElement.skinType, FilterNotEmpty.INSTANCE);
            gui.root.addAll(new GUITextSpacer(gui), skinType);


            //Add main header actions
            done.addClickActions(() ->
            {
                //Validation
                if (!skinType.valid()) return;


                //Processing
                overrideElement.skinType = skinType.getText();


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