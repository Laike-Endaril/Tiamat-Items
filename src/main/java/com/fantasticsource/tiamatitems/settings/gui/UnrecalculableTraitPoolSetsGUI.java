package com.fantasticsource.tiamatitems.settings.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.Namespace;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBlacklist;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNone;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class UnrecalculableTraitPoolSetsGUI extends GUIScreen
{
    public static final FilterBlacklist POOL_SET_FILTER = new FilterBlacklist("Static");

    protected String itemType;

    protected LinkedHashMap<GUILabeledTextInput, LinkedHashSet<String>> nameElementToPoolSetMap = new LinkedHashMap<>();

    protected UnrecalculableTraitPoolSetsGUI(String itemType)
    {
        this.itemType = itemType;
    }

    public static void show(String itemType, LinkedHashMap<String, LinkedHashSet<String>> poolSets, Collection<String> otherPoolSetNames)
    {
        UnrecalculableTraitPoolSetsGUI gui = new UnrecalculableTraitPoolSetsGUI(itemType);
        showStacked(gui);
        gui.drawStack = false;


        //Special setup
        for (String otherName : otherPoolSetNames)
        {
            new GUILabeledTextInput(gui, "", otherName, FilterNone.INSTANCE).setNamespace("Unrecalculable Trait Pool Sets");
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
                Namespace namespace = gui.namespaces.computeIfAbsent("Unrecalculable Trait Pool Sets", o -> new Namespace());
                String nameString = namespace.getFirstAvailableNumberedName("UPoolSet");
                GUILabeledTextInput name = new GUILabeledTextInput(gui, " Pool Set Name: ", nameString, POOL_SET_FILTER).setNamespace("Unrecalculable Trait Pool Sets");

                gui.nameElementToPoolSetMap.put(name, new LinkedHashSet<>());

                GUIButton duplicateButton = GUIButton.newDuplicateButton(screen);
                duplicateButton.addClickActions(() ->
                {
                    int index = getLineIndexContaining(name);
                    if (index == -1) index = lineCount() - 1;
                    index++;
                    GUIList.Line line = addLine(index);

                    String nameString2 = namespace.getFirstAvailableNumberedName(name.getText() + "_Copy");

                    GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(2);
                    nameElement.setText(nameString2);

                    gui.nameElementToPoolSetMap.put(nameElement, (LinkedHashSet<String>) gui.nameElementToPoolSetMap.get(name).clone());
                });

                return new GUIElement[]
                        {
                                duplicateButton,
                                GUIButton.newListButton(gui).addClickActions(() -> PoolListGUI.show(name.getText() + " (Random Unrecalculable Pool Set)", gui.nameElementToPoolSetMap.get(name))),
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
            GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(2);
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
                if (!((GUILabeledTextInput) line.getLineElement(2)).valid()) return;
            }


            //Processing
            poolSets.clear();
            for (GUIList.Line line : recalculableTraits.getLines())
            {
                GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(2);
                poolSets.put(nameElement.getText(), gui.nameElementToPoolSetMap.get(nameElement));
            }


            //Close GUI
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return Minecraft.getMinecraft().currentScreen == this ? itemType + " (Random Unrecalculable Pool Sets)" : itemType;
    }
}
