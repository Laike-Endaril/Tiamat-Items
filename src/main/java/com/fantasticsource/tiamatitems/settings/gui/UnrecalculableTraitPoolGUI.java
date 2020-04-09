package com.fantasticsource.tiamatitems.settings;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextInput;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTrait;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTraitPool;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class UnrecalculableTraitPoolGUI extends GUIScreen
{
    public static final FilterRangedInt WEIGHT_FILTER = FilterRangedInt.get(1, Integer.MAX_VALUE);


    protected String poolName;

    protected LinkedHashMap<GUILabeledTextInput, CUnrecalculableTrait> nameElementToUnrecalculableTraitMap = new LinkedHashMap<>();

    protected UnrecalculableTraitPoolGUI(String poolName)
    {
        this.poolName = poolName;
    }

    public static void show(String poolName, CUnrecalculableTraitPool pool)
    {
        UnrecalculableTraitPoolGUI gui = new UnrecalculableTraitPoolGUI(poolName);
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
        GUIList unrecalculableTraits = new GUIList(gui, true, 0.98, 1 - (cancel.y + cancel.height))
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                String nameString = "UTrait";
                ArrayList<GUITextInput> namespace = gui.namespaces.get("Unrecalculable Traits");
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

                GUILabeledTextInput name = new GUILabeledTextInput(gui, " Trait Name: ", nameString, FilterNotEmpty.INSTANCE).setNamespace("Unrecalculable Traits");
                gui.nameElementToUnrecalculableTraitMap.put(name, new CUnrecalculableTrait());

                return new GUIElement[]
                        {
                                GUIButton.newListButton(gui).addClickActions(() -> UnrecalculableTraitGUI.show(name.getText(), gui.nameElementToUnrecalculableTraitMap.get(name))),
                                new GUIElement(gui, 1, 0),
                                name,
                                new GUIElement(gui, 1, 0),
                                new GUILabeledTextInput(gui, " Trait Weight: ", "1", WEIGHT_FILTER),
                        };
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (cancel.y + cancel.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, unrecalculableTraits);
        gui.root.addAll
                (
                        unrecalculableTraits,
                        scrollbar
                );
        for (Map.Entry<CUnrecalculableTrait, Integer> entry : pool.traitGenWeights.entrySet())
        {
            GUIList.Line line = unrecalculableTraits.addLine();
            GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(2);
            nameElement.setText(entry.getKey().name);
            gui.nameElementToUnrecalculableTraitMap.put(nameElement, entry.getKey());
            ((GUILabeledTextInput) line.getLineElement(4)).setText("" + entry.getValue());
        }


        //Add main header actions
        cancel.addRecalcActions(() ->
        {
            unrecalculableTraits.height = 1 - (cancel.y + cancel.height);
            scrollbar.height = 1 - (cancel.y + cancel.height);
        });
        cancel.addClickActions(gui::close);
        done.addClickActions(() ->
        {
            //Validation
            for (GUIList.Line line : unrecalculableTraits.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(2)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(4)).valid()) return;
            }


            //Processing
            pool.traitGenWeights.clear();
            for (GUIList.Line line : unrecalculableTraits.getLines())
            {
                GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(2);
                CUnrecalculableTrait trait = gui.nameElementToUnrecalculableTraitMap.get(nameElement);
                trait.name = nameElement.getText();
                pool.traitGenWeights.put(trait, WEIGHT_FILTER.parse(((GUILabeledTextInput) line.getLineElement(4)).getText()));
            }


            //Close GUI
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return Minecraft.getMinecraft().currentScreen == this ? poolName + " (U. Trait Pool)" : poolName;
    }
}
