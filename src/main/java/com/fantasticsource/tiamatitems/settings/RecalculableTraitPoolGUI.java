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
import com.fantasticsource.mctools.gui.element.text.filter.FilterBoolean;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTrait;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitPool;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class RecalculableTraitPoolGUI extends GUIScreen
{
    public static final FilterRangedInt WEIGHT_FILTER = FilterRangedInt.get(1, Integer.MAX_VALUE);
    public static final String[] RECALCULABLE_TRAIT_ELEMENT_OPTIONS = new String[]
            {
                    " (Empty Trait Element)",
                    " Left Click Action",
                    " Right Click Action",
                    " Active Attribute Modifier",
                    " Passive Attribute Modifier",
                    " Part Slot",
                    " Texture Layers",
                    " AW Skin",
                    " Forced AW Skin Type Override",
            };


    protected String poolName;
    protected CRecalculableTraitPool pool;

    protected LinkedHashMap<GUILabeledTextInput, CRecalculableTrait> nameElementToRecalculableTraitMap = new LinkedHashMap<>();

    protected RecalculableTraitPoolGUI(String poolName, CRecalculableTraitPool pool)
    {
        this.poolName = poolName;
        this.pool = pool;
    }

    public static void show(String poolName, CRecalculableTraitPool pool)
    {
        RecalculableTraitPoolGUI gui = new RecalculableTraitPoolGUI(poolName, pool);
        showStacked(gui);
        gui.drawStack = false;


        //Background
        gui.root.add(new GUIDarkenedBackground(gui));


        //Header
        GUINavbar navbar = new GUINavbar(gui);
        GUITextButton done = new GUITextButton(gui, "Done", Color.GREEN);
        GUITextButton cancel = new GUITextButton(gui, "Cancel", Color.RED);
        gui.root.addAll(navbar, done, cancel);


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
                                new GUILabeledTextInput(gui, " Trait Weight: ", "1", WEIGHT_FILTER),
                                new GUIElement(gui, 1, 0),
                                new GUILabeledTextInput(gui, " Add to Core on Assembly: ", "" + new CRecalculableTrait().addToCoreOnAssembly, FilterBoolean.INSTANCE)
                        };
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (cancel.y + cancel.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, recalculableTraits);
        gui.root.addAll
                (
                        recalculableTraits,
                        scrollbar
                );
        for (Map.Entry<CRecalculableTrait, Integer> entry : gui.pool.traitGenWeights.entrySet())
        {
            GUIList.Line line = recalculableTraits.addLine();
            GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(2);
            nameElement.setText(entry.getKey().name);
            gui.nameElementToRecalculableTraitMap.put(nameElement, entry.getKey());
            ((GUILabeledTextInput) line.getLineElement(4)).setText("" + entry.getValue());
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
                if (!((GUILabeledTextInput) line.getLineElement(4)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(6)).valid()) return;
            }


            //Processing
            gui.pool.traitGenWeights.clear();
            for (GUIList.Line line : recalculableTraits.getLines())
            {
                GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(2);
                CRecalculableTrait trait = gui.nameElementToRecalculableTraitMap.get(nameElement);
                trait.name = nameElement.getText();
                trait.addToCoreOnAssembly = FilterBoolean.INSTANCE.parse(((GUILabeledTextInput) line.getLineElement(6)).getText());
                gui.pool.traitGenWeights.put(trait, WEIGHT_FILTER.parse(((GUILabeledTextInput) line.getLineElement(4)).getText()));
            }


            //Close GUI
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return Minecraft.getMinecraft().currentScreen == this ? poolName + " (R. Trait Pool)" : poolName;
    }
}
