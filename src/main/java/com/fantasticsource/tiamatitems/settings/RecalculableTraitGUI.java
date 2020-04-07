package com.fantasticsource.tiamatitems.settings;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTrait;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class RecalculableTraitGUI extends GUIScreen
{
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


    protected String traitName;
    protected CRecalculableTrait trait;

    protected RecalculableTraitGUI(String traitName, CRecalculableTrait trait)
    {
        this.traitName = traitName;
        this.trait = trait;
    }

    public static void show(String poolName, CRecalculableTrait pool)
    {
        RecalculableTraitGUI gui = new RecalculableTraitGUI(poolName, pool);
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
                return new GUIElement[]
                        {
                                //TODO
                        };
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (cancel.y + cancel.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, recalculableTraits);
        gui.root.addAll
                (
                        recalculableTraits,
                        scrollbar
                );


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
            //TODO


            //Processing
            //TODO


            //Close GUI
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return Minecraft.getMinecraft().currentScreen == this ? traitName + " (R. Trait)" : traitName;
    }
}
