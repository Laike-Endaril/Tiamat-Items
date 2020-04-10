package com.fantasticsource.tiamatitems.settings.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextInput;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBlacklist;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNone;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.*;

public class RecalculableTraitPoolSetsGUI extends GUIScreen
{
    public static final FilterBlacklist POOL_SET_FILTER = new FilterBlacklist("Static");

    protected String itemType;

    protected LinkedHashMap<GUILabeledTextInput, LinkedHashSet<String>> nameElementToPoolSetMap = new LinkedHashMap<>();

    protected RecalculableTraitPoolSetsGUI(String itemType)
    {
        this.itemType = itemType;
    }

    public static void show(String itemType, LinkedHashMap<String, LinkedHashSet<String>> poolSets, Collection<String> otherPoolSetNames)
    {
        RecalculableTraitPoolSetsGUI gui = new RecalculableTraitPoolSetsGUI(itemType);
        showStacked(gui);
        gui.drawStack = false;


        //Special setup
        for (String otherName : otherPoolSetNames)
        {
            new GUILabeledTextInput(gui, "", otherName, FilterNone.INSTANCE).setNamespace("Recalculable Trait Pool Sets");
        }

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
                String nameString = "RPoolSet";
                ArrayList<GUITextInput> namespace = gui.namespaces.get("Recalculable Trait Pool Sets");
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

                GUILabeledTextInput name = new GUILabeledTextInput(gui, " Pool Set Name: ", nameString, POOL_SET_FILTER).setNamespace("Recalculable Trait Pool Sets");
                gui.nameElementToPoolSetMap.put(name, new LinkedHashSet<>());

                return new GUIElement[]
                        {
                                GUIButton.newListButton(gui).addClickActions(() -> PoolListGUI.show(name.getText() + " (Random Recalculable Pool Set)", gui.nameElementToPoolSetMap.get(name))),
                                name
                        };
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (cancel.y + cancel.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, recalculableTraits);
        gui.root.addAll
                (
                        recalculableTraits,
                        scrollbar
                );
        for (Map.Entry<String, LinkedHashSet<String>> entry : poolSets.entrySet())
        {
            GUIList.Line line = recalculableTraits.addLine();
            GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(1);
            nameElement.setText(entry.getKey());
            gui.nameElementToPoolSetMap.put(nameElement, entry.getValue());
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
                if (!((GUILabeledTextInput) line.getLineElement(1)).valid()) return;
            }


            //Processing
            poolSets.clear();
            for (GUIList.Line line : recalculableTraits.getLines())
            {
                GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(1);
                poolSets.put(nameElement.getText(), gui.nameElementToPoolSetMap.get(nameElement));
            }


            //Close GUI
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return Minecraft.getMinecraft().currentScreen == this ? itemType + " (Random Recalculable Pool Sets)" : itemType;
    }
}