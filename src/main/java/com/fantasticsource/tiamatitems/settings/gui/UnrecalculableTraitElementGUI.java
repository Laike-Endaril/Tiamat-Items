package com.fantasticsource.tiamatitems.settings.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.Namespace;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTraitElement;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.CUTraitElement_AWDyeChannelOverride;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.CUTraitElement_AWSkin;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes.CRandomRGB;
import com.fantasticsource.tools.datastructures.Color;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class UnrecalculableTraitElementGUI extends GUIScreen
{
    public static final FilterRangedInt
            AW_SLOT_INDEX_FILTER = FilterRangedInt.get(0, 9),
            AW_DYE_INDEX_FILTER = FilterRangedInt.get(0, 7);

    protected String typeName;

    protected LinkedHashMap<GUIButton, CRandomRGB> editButtonToCRandomRGBMap = new LinkedHashMap<>();

    protected UnrecalculableTraitElementGUI(String typeName)
    {
        this.typeName = typeName;
    }

    public static UnrecalculableTraitElementGUI show(String typeName, CUnrecalculableTraitElement traitElement)
    {
        UnrecalculableTraitElementGUI gui = new UnrecalculableTraitElementGUI(typeName);
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
        if (traitElement.getClass() == CUTraitElement_AWSkin.class)
        {
            CUTraitElement_AWSkin skinElement = (CUTraitElement_AWSkin) traitElement;

            GUILabeledTextInput libraryFileOrFolder = new GUILabeledTextInput(gui, " Library File: ", (skinElement.libraryFileOrFolder.equals("") ? "LibraryFileOrFolder" : skinElement.libraryFileOrFolder), FilterNotEmpty.INSTANCE);
            GUILabeledBoolean isRandomFromFolder = new GUILabeledBoolean(gui, " Is Random From Folder: ", skinElement.isRandomFromFolder);
            GUILabeledTextInput skinType = new GUILabeledTextInput(gui, " Skin Type: ", (skinElement.skinType.equals("") ? "SkinType" : skinElement.skinType), FilterNotEmpty.INSTANCE);
            GUILabeledBoolean isTransient = new GUILabeledBoolean(gui, " Transient: ", skinElement.isTransient);
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
                    Namespace namespace = gui.namespaces.computeIfAbsent("Dye Indices", o -> new Namespace());
                    while (namespace.contains("" + index)) index++;

                    GUILabeledTextInput dyeIndex = new GUILabeledTextInput(gui, " Dye Index: ", "" + index, AW_DYE_INDEX_FILTER).setNamespace("Dye Indices");

                    GUIButton duplicateButton = GUIButton.newDuplicateButton(screen);
                    duplicateButton.addClickActions(() ->
                    {
                        int lineIndex = getLineIndexContaining(dyeIndex);
                        if (lineIndex == -1) lineIndex = lineCount() - 1;
                        lineIndex++;
                        GUIList.Line line = addLine(lineIndex);

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
                if (element instanceof GUIList.Line)
                {
                    GUIList.Line line = (GUIList.Line) element;
                    GUILabeledTextInput labeledTextInput = (GUILabeledTextInput) line.getLineElement(2);
                    gui.namespaces.get("Dye Indices").inputs.remove(labeledTextInput.input);
                }
                return false;
            });
            GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (separator.y + separator.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, dyes);
            gui.root.addAll(dyes, scrollbar);
            for (Map.Entry<Integer, CRandomRGB> entry : skinElement.dyeChannels.entrySet())
            {
                GUIList.Line line = dyes.addLine();
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
                if (!libraryFileOrFolder.valid() || !skinType.valid() || !indexWithinSkinTypeIfTransient.valid()) return;
                for (GUIList.Line line : dyes.getLines())
                {
                    if (!((GUILabeledTextInput) line.getLineElement(2)).valid()) return;
                }


                //Processing
                skinElement.libraryFileOrFolder = libraryFileOrFolder.getText();
                skinElement.isRandomFromFolder = isRandomFromFolder.getValue();
                skinElement.skinType = skinType.getText();
                skinElement.isTransient = isTransient.getValue();
                skinElement.indexWithinSkinTypeIfTransient = AW_SLOT_INDEX_FILTER.parse(indexWithinSkinTypeIfTransient.getText());
                skinElement.dyeChannels.clear();
                for (GUIList.Line line : dyes.getLines())
                {
                    GUIButton editButton = (GUIButton) line.getLineElement(1);
                    GUILabeledTextInput index = (GUILabeledTextInput) line.getLineElement(2);
                    skinElement.dyeChannels.put(AW_DYE_INDEX_FILTER.parse(index.getText()), gui.editButtonToCRandomRGBMap.get(editButton));
                }


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement.getClass() == CUTraitElement_AWDyeChannelOverride.class)
        {
            CUTraitElement_AWDyeChannelOverride dyeOverrideElement = (CUTraitElement_AWDyeChannelOverride) traitElement;

            GUIGradientBorder separator = new GUIGradientBorder(gui, 1, 0.02, 0.3, Color.WHITE, Color.BLANK);
            gui.root.addAll(
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
                    Namespace namespace = gui.namespaces.computeIfAbsent("Dye Indices", o -> new Namespace());
                    while (namespace.contains("" + index)) index++;

                    GUILabeledTextInput dyeIndex = new GUILabeledTextInput(gui, " Dye Index: ", "" + index, AW_DYE_INDEX_FILTER).setNamespace("Dye Indices");

                    GUIButton duplicateButton = GUIButton.newDuplicateButton(screen);
                    duplicateButton.addClickActions(() ->
                    {
                        int lineIndex = getLineIndexContaining(dyeIndex);
                        if (lineIndex == -1) lineIndex = lineCount() - 1;
                        lineIndex++;
                        GUIList.Line line = addLine(lineIndex);

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
                if (element instanceof GUIList.Line)
                {
                    GUIList.Line line = (GUIList.Line) element;
                    GUILabeledTextInput labeledTextInput = (GUILabeledTextInput) line.getLineElement(2);
                    gui.namespaces.get("Dye Indices").inputs.remove(labeledTextInput.input);
                }
                return false;
            });
            GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (separator.y + separator.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, dyes);
            gui.root.addAll(dyes, scrollbar);
            for (Map.Entry<Integer, CRandomRGB> entry : dyeOverrideElement.dyeChannels.entrySet())
            {
                GUIList.Line line = dyes.addLine();
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
                for (GUIList.Line line : dyes.getLines())
                {
                    if (!((GUILabeledTextInput) line.getLineElement(2)).valid()) return;
                }


                //Processing
                dyeOverrideElement.dyeChannels.clear();
                for (GUIList.Line line : dyes.getLines())
                {
                    GUIButton editButton = (GUIButton) line.getLineElement(1);
                    GUILabeledTextInput index = (GUILabeledTextInput) line.getLineElement(2);
                    dyeOverrideElement.dyeChannels.put(AW_DYE_INDEX_FILTER.parse(index.getText()), gui.editButtonToCRandomRGBMap.get(editButton));
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
