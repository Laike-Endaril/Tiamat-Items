package com.fantasticsource.tiamatitems.settings;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBoolean;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTrait;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class RecalculableTraitPoolGUI extends GUIScreen
{
    protected String poolName;

    protected RecalculableTraitPoolGUI(String poolName)
    {
        this.poolName = poolName;
    }

    public static void show(String poolName)
    {
        RecalculableTraitPoolGUI gui = new RecalculableTraitPoolGUI(poolName);
        showStacked(gui);
        gui.drawStack = false;


        //Background
        gui.root.add(new GUIDarkenedBackground(gui));


        //Header
        GUINavbar navbar = new GUINavbar(gui);
        GUITextButton save = new GUITextButton(gui, "Save and Close", Color.GREEN);
        GUITextButton cancel = new GUITextButton(gui, "Close Without Saving", Color.RED);
        gui.root.addAll(navbar, save, cancel);


        GUIList recalculableTraitPools = new GUIList(gui, true, 0.98, 1 - (cancel.y + cancel.height))
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                return new GUIElement[]
                        {
                                GUIButton.newListButton(gui),
                                new GUIElement(gui, 1, 0),
                                new GUILabeledTextInput(gui, " Trait name: ", "RTrait", FilterNotEmpty.INSTANCE),
                                new GUIElement(gui, 1, 0),
                                new GUILabeledTextInput(gui, " Add to core on assembly: ", "" + new CRecalculableTrait().addToCoreOnAssembly, FilterBoolean.INSTANCE).setNamespace("Traits"),
                        };
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (cancel.y + cancel.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, recalculableTraitPools);
        gui.root.addAll
                (
                        recalculableTraitPools,
                        scrollbar
                );


        //Add main header actions
        cancel.addRecalcActions(() ->
        {
            recalculableTraitPools.height = 1 - (cancel.y + cancel.height);
            scrollbar.height = 1 - (cancel.y + cancel.height);
        });
        cancel.addClickActions(gui::close);
        save.addClickActions(() ->
        {
            //Validation

            //General
            //TODO


            //Processing
            //TODO


            //Save to data structure in parent GUI
            //TODO


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
