package com.fantasticsource.tiamatitems.settings.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBoolean;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTraitElement;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.CUTraitElement_AWSkin;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes.CRandomRGB;
import com.fantasticsource.tools.datastructures.Color;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

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
                            editButton.addClickActions(() -> CRandomRGBGUI.show(gui.editButtonToCRandomRGBMap.get(editButton), FilterInt.INSTANCE.parse(dyeIndex.getText()))),
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
