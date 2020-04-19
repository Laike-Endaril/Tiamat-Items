package com.fantasticsource.tiamatitems.settings.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTrait;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class RecalculableTraitListGUI extends GUIScreen
{
    protected String itemType;

    protected LinkedHashMap<GUILabeledTextInput, CRecalculableTrait> nameElementToRecalculableTraitMap = new LinkedHashMap<>();

    protected RecalculableTraitListGUI(String itemType)
    {
        this.itemType = itemType;
    }

    public static void show(String itemType, LinkedHashMap<String, CRecalculableTrait> list)
    {
        RecalculableTraitListGUI gui = new RecalculableTraitListGUI(itemType);
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
        GUIList recalculableTraits = new GUIList(gui, true, 0.98, 1 - (cancel.y + cancel.height))
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                String nameString = "RTrait";
                ArrayList<GUITextInput> namespace = gui.namespaces.get("Recalculable Traits");
                if (namespace != null)
                {
                    int i = 0;
                    for (; i >= 0; i++)
                    {
                        boolean found = false;
                        for (GUITextInput input : namespace)
                        {
                            if (input.getText().equals(nameString + i))
                            {
                                found = true;
                                break;
                            }
                        }
                        if (!found) break;
                    }
                    nameString += i;
                }

                GUILabeledTextInput name = new GUILabeledTextInput(gui, " Trait Name: ", nameString, FilterNotEmpty.INSTANCE).setNamespace("Recalculable Traits");
                gui.nameElementToRecalculableTraitMap.put(name, new CRecalculableTrait());

                return new GUIElement[]
                        {
                                GUIButton.newListButton(gui).addClickActions(() -> RecalculableTraitGUI.show(name.getText(), gui.nameElementToRecalculableTraitMap.get(name))),
                                new GUIElement(gui, 1, 0),
                                name,
                                new GUIElement(gui, 1, 0),
                                new GUILabeledBoolean(gui, " Add to Core on Assembly: ", new CRecalculableTrait().addToCoreOnAssembly)
                        };
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (cancel.y + cancel.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, recalculableTraits);
        gui.root.addAll
                (
                        recalculableTraits,
                        scrollbar
                );
        for (CRecalculableTrait trait : list.values())
        {
            GUIList.Line line = recalculableTraits.addLine();
            GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(2);
            nameElement.setText(trait.name);
            gui.nameElementToRecalculableTraitMap.put(nameElement, trait);
            ((GUILabeledTextInput) line.getLineElement(4)).setText("" + trait.addToCoreOnAssembly);
        }


        //Add main header actions
        cancel.addRecalcActions(() ->
        {
            recalculableTraits.height = 1 - (cancel.y + cancel.height);
            scrollbar.height = 1 - (cancel.y + cancel.height);
        });
        cancel.addClickActions(gui::close);
        done.addClickActions(() ->
        {
            //Validation
            for (GUIList.Line line : recalculableTraits.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(2)).valid()) return;
            }


            //Processing
            list.clear();
            for (GUIList.Line line : recalculableTraits.getLines())
            {
                GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(2);
                CRecalculableTrait trait = gui.nameElementToRecalculableTraitMap.get(nameElement);
                trait.name = nameElement.getText();
                trait.addToCoreOnAssembly = ((GUILabeledBoolean) line.getLineElement(4)).getValue();
                list.put(trait.name, trait);
            }


            //Close GUI
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return Minecraft.getMinecraft().currentScreen == this ? itemType + " (Static R. Traits)" : itemType;
    }
}
