package com.fantasticsource.tiamatitems.settings;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBlacklist;
import com.fantasticsource.mctools.gui.element.text.filter.FilterFloat;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
import com.fantasticsource.tiamatitems.Network;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitPool;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SettingsGUI extends GUIScreen
{
    public static final FilterRangedInt MAX_ITEM_LEVEL_FILTER = FilterRangedInt.get(1, Integer.MAX_VALUE);

    public CSettings settings;

    protected LinkedHashMap<GUILabeledTextInput, CRecalculableTraitPool> nameElementToRecalculableTraitPoolMap = new LinkedHashMap<>();

    protected SettingsGUI(CSettings settings)
    {
        this.settings = settings;
    }

    public static void show(Network.OpenSettingsPacket packet)
    {
        SettingsGUI gui = new SettingsGUI(packet.settings);
        Minecraft.getMinecraft().displayGuiScreen(gui);


        //Background
        gui.root.add(new GUIDarkenedBackground(gui));


        //Header
        GUINavbar navbar = new GUINavbar(gui);
        GUITextButton save = new GUITextButton(gui, "Save and Close", Color.GREEN);
        GUITextButton cancel = new GUITextButton(gui, "Close Without Saving", Color.RED);
        gui.root.addAll(navbar, save, cancel);


        //Main
        GUITabView tabView = new GUITabView(gui, 1, 1 - (cancel.y + cancel.height), "General", "Trait Pools (Recalculable)", "Trait Pools (Unrecalculable)", "Rarities", "Item Types", "Attribute Balance Multipliers");
        gui.root.add(tabView);


        //General tab
        GUILabeledTextInput maxItemLevel = new GUILabeledTextInput(gui, " Max Item Level: ", "" + gui.settings.maxItemLevel, MAX_ITEM_LEVEL_FILTER);
        GUILabeledTextInput baseMultiplier = new GUILabeledTextInput(gui, " Base Trait Multiplier: ", "" + gui.settings.baseMultiplier, FilterFloat.INSTANCE);
        GUILabeledTextInput multiplierBonusPerLevel = new GUILabeledTextInput(gui, " Trait Multiplier Bonus Per Item Level: ", "" + gui.settings.multiplierBonusPerLevel, FilterFloat.INSTANCE);
        tabView.tabViews.get(0).addAll
                (
                        new GUITextSpacer(gui),
                        new GUIText(gui, " Current Version: " + CSettings.getVersion()),
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
                String nameString = "RTraitPool";
                ArrayList<GUITextInput> namespace = gui.namespaces.get("Recalculable Trait Pools");
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

                GUILabeledTextInput name = new GUILabeledTextInput(gui, " Pool Name: ", nameString, new FilterBlacklist("null")).setNamespace("Recalculable Trait Pools");
                gui.nameElementToRecalculableTraitPoolMap.put(name, new CRecalculableTraitPool());

                return new GUIElement[]
                        {
                                GUIButton.newListButton(gui).addClickActions(() -> RecalculableTraitPoolGUI.show(name.getText(), gui.nameElementToRecalculableTraitPoolMap.get(name))),
                                new GUIElement(gui, 1, 0),
                                name
                        };
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, recalculableTraitPools);
        tabView.tabViews.get(1).addAll
                (
                        recalculableTraitPools,
                        scrollbar
                );
        for (CRecalculableTraitPool pool : gui.settings.recalcTraitPools.values())
        {
            GUIList.Line line = recalculableTraitPools.addLine();
            GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(2);
            nameElement.setText(pool.name);
            gui.nameElementToRecalculableTraitPoolMap.put(nameElement, pool);
        }


        //Trait Pools (Unrecalculable) tab
        GUIList unrecalculableTraitPools = new GUIList(gui, true, 0.98, 1)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                GUILabeledTextInput name = new GUILabeledTextInput(gui, " Pool Name: ", "poolName", new FilterBlacklist("null")).setNamespace("Unrecalculable Trait Pools");

                return new GUIElement[]
                        {
                                GUIButton.newListButton(gui).addClickActions(
                                        //TODO show gui
                                ),
                                new GUIElement(gui, 1, 0),
                                name
                        };
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
                GUILabeledTextInput name = new GUILabeledTextInput(gui, " Rarity Name: ", "rarityName", FilterNotEmpty.INSTANCE).setNamespace("Rarities");

                return new GUIElement[]
                        {
                                GUIButton.newListButton(gui).addClickActions(
                                        //TODO show gui
                                ),
                                new GUIElement(gui, 1, 0),
                                name
                        };
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
                GUILabeledTextInput name = new GUILabeledTextInput(gui, " Item Type Name: ", "itemTypeName", FilterNotEmpty.INSTANCE).setNamespace("Item Types");

                return new GUIElement[]
                        {
                                new GUIElement(gui, 1, 0),
                                name,
                                new GUIElement(gui, 1, 0),
                                new GUITextButton(gui, "Edit Static Recalculable Traits").addClickActions(() ->
                                {
                                    //TODO show gui
                                }),
                                new GUITextButton(gui, "Edit Static Unrecalculable Traits").addClickActions(() ->
                                {
                                    //TODO show gui
                                }),
                                new GUIElement(gui, 1, 0),
                                new GUITextButton(gui, "Edit Random Recalculable Trait Pool Sets").addClickActions(() ->
                                {
                                    //TODO show gui
                                    //TODO disallow "Static" as a name when editing trait pool sets
                                }),
                                new GUITextButton(gui, "Edit Random Unrecalculable Trait Pool Sets").addClickActions(() ->
                                {
                                    //TODO show gui
                                    //TODO disallow "Static" as a name when editing trait pool sets
                                })
                        };
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
                return new GUIElement[]{
                        new GUIElement(gui, 1, 0),
                        new GUILabeledTextInput(gui, " Attribute: ", "generic.attributeName", FilterNotEmpty.INSTANCE).setNamespace("Attribute Balance Multipliers"),
                        new GUIElement(gui, 1, 0),
                        new GUILabeledTextInput(gui, " Multiplier: ", "1", FilterFloat.INSTANCE)
                };
            }
        };
        GUIVerticalScrollbar scrollbar5 = new GUIVerticalScrollbar(gui, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, attributeBalanceMultipliers);
        tabView.tabViews.get(5).addAll
                (
                        attributeBalanceMultipliers,
                        scrollbar5
                );
        for (Map.Entry<String, Double> entry : CSettings.attributeBalanceMultipliers.entrySet())
        {
            GUIList.Line line = attributeBalanceMultipliers.addLine();
            ((GUILabeledTextInput) line.getLineElement(1)).setInput(entry.getKey());
            ((GUILabeledTextInput) line.getLineElement(3)).setInput("" + entry.getValue());
        }


        //Insert new tabs here


        //Add main header actions
        cancel.addRecalcActions(() -> tabView.height = 1 - (cancel.y + cancel.height));
        cancel.addClickActions(gui::close);
        save.addClickActions(() ->
        {
            //Validation

            //General
            if (!maxItemLevel.valid() || !baseMultiplier.valid() || !multiplierBonusPerLevel.valid()) return;

            //Recalculable Trait Pools
            for (GUIList.Line line : recalculableTraitPools.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(2)).valid()) return;
            }

            //Unrecalculable Trait Pools
            for (GUIList.Line line : unrecalculableTraitPools.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(2)).valid()) return;
            }

            //Rarities
            for (GUIList.Line line : rarities.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(2)).valid()) return;
            }

            //Item Types
            for (GUIList.Line line : itemTypes.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(1)).valid()) return;
            }

            //Attribute Balance Multipliers
            for (GUIList.Line line : attributeBalanceMultipliers.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(1)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(3)).valid()) return;
            }


            //Processing

            //General
            gui.settings.maxItemLevel = MAX_ITEM_LEVEL_FILTER.parse(maxItemLevel.getText());
            gui.settings.baseMultiplier = FilterFloat.INSTANCE.parse(baseMultiplier.getText());
            gui.settings.multiplierBonusPerLevel = FilterFloat.INSTANCE.parse(multiplierBonusPerLevel.getText());

            //Recalculable Trait Pools
            gui.settings.recalcTraitPools.clear();
            for (GUIList.Line line : recalculableTraitPools.getLines())
            {
                GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(2);
                CRecalculableTraitPool pool = gui.nameElementToRecalculableTraitPoolMap.get(nameElement);
                pool.name = nameElement.getText();
                gui.settings.recalcTraitPools.put(nameElement.getText(), pool);
            }

            //TODO

            //Attribute Balance Multipliers
            CSettings.attributeBalanceMultipliers.clear();
            for (GUIList.Line line : attributeBalanceMultipliers.getLines())
            {
                CSettings.attributeBalanceMultipliers.put(((GUILabeledTextInput) line.getLineElement(1)).getText(), (double) FilterFloat.INSTANCE.parse(((GUILabeledTextInput) line.getLineElement(3)).getText()));
            }


            //Send to server
            Network.WRAPPER.sendToServer(new Network.SaveSettingsPacket(gui.settings));


            //Close GUI
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return "Settings";
    }
}
