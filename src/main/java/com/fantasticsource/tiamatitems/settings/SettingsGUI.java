package com.fantasticsource.tiamatitems.settings;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterFloat;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.tiamatitems.Network;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class SettingsGUI extends GUIScreen
{
    public static void show(Network.OpenSettingsPacket packet)
    {
        SettingsGUI gui = new SettingsGUI();
        Minecraft.getMinecraft().displayGuiScreen(gui);


        //Background
        gui.root.add(new GUIDarkenedBackground(gui));


        //Header
        GUINavbar navbar = new GUINavbar(gui);
        GUITextButton save = new GUITextButton(gui, "Save and Close", Color.GREEN);
        GUITextButton cancel = new GUITextButton(gui, "Close Without Saving", Color.RED);
        gui.root.addAll(navbar, save, cancel);


        GUITabView tabView = new GUITabView(gui, 1, 1 - (cancel.y + cancel.height), "General", "Trait Pools (Recalculable)", "Trait Pools (Unrecalculable)", "Rarities", "Item Types", "Attribute Balance Multipliers");
        gui.root.add(tabView);


        //General tab
        GUILabeledTextInput maxItemLevel = new GUILabeledTextInput(gui, "Max Item Level: ", "" + CSettings.maxItemLevel, FilterInt.INSTANCE);
        GUILabeledTextInput baseMultiplier = new GUILabeledTextInput(gui, "Base Trait Multiplier: ", "" + CSettings.baseMultiplier, FilterFloat.INSTANCE);
        GUILabeledTextInput multiplierBonusPerLevel = new GUILabeledTextInput(gui, "Trait Multiplier Bonus Per Item Level: ", "" + CSettings.multiplierBonusPerLevel, FilterFloat.INSTANCE);
        tabView.tabViews.get(0).addAll
                (
                        new GUITextSpacer(gui),
                        new GUIText(gui, "Current Version: " + CSettings.getVersion()),
                        new GUITextSpacer(gui),
                        maxItemLevel,
                        new GUITextSpacer(gui),
                        baseMultiplier,
                        new GUITextSpacer(gui),
                        multiplierBonusPerLevel
                );


        //Trait Pools (Recalculable) tab
        GUIList recalculableTraitPools = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                //TODO CRecalculableTraitPool
                return new GUIElement[0];
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, recalculableTraitPools);
        tabView.tabViews.get(1).addAll
                (
                        recalculableTraitPools,
                        scrollbar
                );


        //Trait Pools (Unrecalculable) tab
        GUIList unrecalculableTraitPools = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                //TODO CUnrecalculableTraitPool
                return new GUIElement[0];
            }
        };
        GUIVerticalScrollbar scrollbar2 = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, unrecalculableTraitPools);
        tabView.tabViews.get(2).addAll
                (
                        unrecalculableTraitPools,
                        scrollbar2
                );


        //Rarities tab
        GUIList rarities = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                //TODO CRarity
                return new GUIElement[0];
            }
        };
        GUIVerticalScrollbar scrollbar3 = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, rarities);
        tabView.tabViews.get(3).addAll
                (
                        rarities,
                        scrollbar3
                );


        //Item Types tab
        GUIList itemTypes = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                //TODO CItemType
                return new GUIElement[0];
            }
        };
        GUIVerticalScrollbar scrollbar4 = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, itemTypes);
        tabView.tabViews.get(4).addAll
                (
                        itemTypes,
                        scrollbar4
                );


        //Attribute Multipliers tab
        GUIList attributeBalanceMultipliers = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                //TODO .Attribute
                //TODO .Attribute multiplier
                return new GUIElement[0];
            }
        };
        GUIVerticalScrollbar scrollbar5 = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, attributeBalanceMultipliers);
        tabView.tabViews.get(5).addAll
                (
                        attributeBalanceMultipliers,
                        scrollbar5
                );


        //Insert new tabs here


        //Add main header actions
        cancel.addRecalcActions(() -> tabView.height = 1 - (cancel.y + cancel.height));
        cancel.addClickActions(gui::close);
        save.addClickActions(() ->
        {
            //Validation

            //General
            if (!maxItemLevel.valid() || !baseMultiplier.valid() || !multiplierBonusPerLevel.valid()) return;
            //TODO


            //Processing

            //General
            CSettings.maxItemLevel = FilterInt.INSTANCE.parse(maxItemLevel.getText());
            CSettings.baseMultiplier = FilterFloat.INSTANCE.parse(baseMultiplier.getText());
            CSettings.multiplierBonusPerLevel = FilterFloat.INSTANCE.parse(multiplierBonusPerLevel.getText());
            //TODO


            //Send to server
            Network.WRAPPER.sendToServer(new Network.SaveSettingsPacket());


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
