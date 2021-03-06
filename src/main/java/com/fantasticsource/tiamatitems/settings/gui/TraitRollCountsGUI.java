package com.fantasticsource.tiamatitems.settings.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBlacklist;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tiamatitems.settings.CRarity;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.Map;
import java.util.function.Predicate;

public class TraitRollCountsGUI extends GUIScreen
{
    public static final FilterBlacklist TRAIT_POOL_SET_FILTER = new FilterBlacklist("Static");
    public static final FilterRangedInt TRAIT_POOL_SET_ROLL_COUNT_FILTER = FilterRangedInt.get(0, Integer.MAX_VALUE);

    protected String rarityName;

    protected TraitRollCountsGUI(String rarityName)
    {
        this.rarityName = rarityName;
    }

    public static void show(String rarityName, CRarity rarity)
    {
        TraitRollCountsGUI gui = new TraitRollCountsGUI(rarityName);
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
        GUIList traitPoolSetRollCounts = new GUIList(gui, true, 0.98, 1 - (cancel.y + cancel.height))
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                return new GUIElement[]
                        {
                                new GUIElement(gui, 1, 0),
                                new GUILabeledTextInput(gui, " Trait Pool Set Name: ", "TraitPoolSetName", TRAIT_POOL_SET_FILTER).setNamespace("Trait Pool Sets"),
                                new GUIElement(gui, 1, 0),
                                new GUILabeledTextInput(gui, " Roll Count: ", "1", TRAIT_POOL_SET_ROLL_COUNT_FILTER)
                        };
            }
        };
        traitPoolSetRollCounts.addRemoveChildActions((Predicate<GUIElement>) element ->
        {
            if (element instanceof GUIList.Line)
            {
                GUIList.Line line = (GUIList.Line) element;
                GUILabeledTextInput labeledTextInput = (GUILabeledTextInput) line.getLineElement(1);
                gui.namespaces.get("Trait Pool Sets").inputs.remove(labeledTextInput.input);
            }
            return false;
        });
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (cancel.y + cancel.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, traitPoolSetRollCounts);
        gui.root.addAll
                (
                        traitPoolSetRollCounts,
                        scrollbar
                );
        for (Map.Entry<String, Integer> entry : rarity.traitPoolSetRollCounts.entrySet())
        {
            GUIList.Line line = traitPoolSetRollCounts.addLine();
            ((GUILabeledTextInput) line.getLineElement(1)).setText(entry.getKey());
            ((GUILabeledTextInput) line.getLineElement(3)).setText("" + entry.getValue());
        }


        //Add main header actions
        cancel.addRecalcActions(() ->
        {
            traitPoolSetRollCounts.height = 1 - (cancel.y + cancel.height);
            scrollbar.height = 1 - (cancel.y + cancel.height);
        });
        cancel.addClickActions(gui::close);
        done.addClickActions(() ->
        {
            //Validation
            for (GUIList.Line line : traitPoolSetRollCounts.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(1)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(3)).valid()) return;
            }


            //Processing
            rarity.traitPoolSetRollCounts.clear();
            for (GUIList.Line line : traitPoolSetRollCounts.getLines())
            {
                rarity.traitPoolSetRollCounts.put(((GUILabeledTextInput) line.getLineElement(1)).getText(), TRAIT_POOL_SET_ROLL_COUNT_FILTER.parse(((GUILabeledTextInput) line.getLineElement(3)).getText()));
            }


            //Close GUI
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return Minecraft.getMinecraft().currentScreen == this ? rarityName + " (Rarity)" : rarityName;
    }
}
