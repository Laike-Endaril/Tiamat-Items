package com.fantasticsource.tiamatitems.globalsettings;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class GlobalSettingsGUI extends GUIScreen
{
    public static void show()
    {
        GlobalSettingsGUI gui = new GlobalSettingsGUI();
        Minecraft.getMinecraft().displayGuiScreen(gui);


        //Background
        gui.root.add(new GUIDarkenedBackground(gui));


        //Header
        GUINavbar navbar = new GUINavbar(gui);
        GUITextButton save = new GUITextButton(gui, "Save", Color.GREEN);
        GUITextButton cancel = new GUITextButton(gui, "Cancel", Color.RED);
        gui.root.addAll(navbar, save, cancel);


        GUITabView tabView = new GUITabView(gui, 1, 1 - (cancel.y + cancel.height), "General", "Attribute Multipliers", "Affixes", "Rarities", "Item Types");
        gui.root.add(tabView);


        //General tab
        //TODO
        tabView.tabViews.get(0).addAll
                (
                );


        //Attribute Multipliers tab
        //TODO
        tabView.tabViews.get(1).addAll
                (
                );


        //Affixes tab
        GUIList affixes = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                return new GUIElement[0];
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, affixes);
        tabView.tabViews.get(2).addAll
                (
                        affixes,
                        scrollbar
                );


        //Rarities tab
        GUIList rarities = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                return new GUIElement[0];
            }
        };
        GUIVerticalScrollbar scrollbar2 = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, rarities);
        tabView.tabViews.get(3).addAll
                (
                        rarities,
                        scrollbar2
                );


        //Item Types tab
        //TODO
        tabView.tabViews.get(4).addAll
                (
                );


        //...


        //Add main header actions
        cancel.addRecalcActions(() -> tabView.height = 1 - (cancel.y + cancel.height));
        cancel.addClickActions(gui::close);
        save.addClickActions(() ->
        {
            //Validation
            //TODO


            //Processing
            //TODO


            //Send to server
            //TODO


            //Close GUI
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return "Item Editor";
    }
}
