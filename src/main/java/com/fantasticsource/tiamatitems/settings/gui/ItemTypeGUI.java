package com.fantasticsource.tiamatitems.settings.gui;

import com.fantasticsource.mctools.Slottings;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterFloat;
import com.fantasticsource.mctools.gui.screen.TextSelectionGUI;
import com.fantasticsource.tiamatitems.trait.CItemType;
import com.fantasticsource.tools.datastructures.Color;

public class ItemTypeGUI extends GUIScreen
{
    protected CItemType itemType;

    protected ItemTypeGUI(CItemType itemType)
    {
        this.itemType = itemType;
    }

    public static void show(CItemType itemType)
    {
        ItemTypeGUI gui = new ItemTypeGUI(itemType);
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
        GUIText slottingLabel = new GUIText(gui, " Slotting: ");
        GUIText slotting = new GUIText(gui, itemType.slotting, getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
        GUILabeledTextInput traitLevelMultiplier = new GUILabeledTextInput(gui, " Trait Level Multiplier: ", "" + itemType.traitLevelMultiplier, FilterFloat.INSTANCE);
        GUILabeledTextInput value = new GUILabeledTextInput(gui, " Base Monetary Value: ", "" + itemType.value, FilterFloat.INSTANCE);
        gui.root.addAll(
                new GUITextSpacer(gui),
                slottingLabel, slotting.addClickActions(() -> new TextSelectionGUI(slotting, "Slotting Selection", Slottings.availableSlottings())),
                new GUITextSpacer(gui),
                traitLevelMultiplier,
                new GUITextSpacer(gui),
                value,
                new GUITextSpacer(gui),
                new GUITextButton(gui, "Edit Static Recalculable Traits").addClickActions(() -> RecalculableTraitListGUI.show(itemType.staticRecalculableTraits)),
                new GUITextSpacer(gui),
                new GUITextButton(gui, "Edit Static Unrecalculable Traits").addClickActions(() -> UnrecalculableTraitListGUI.show(itemType.staticUnrecalculableTraits)),
                new GUITextSpacer(gui),
                new GUITextButton(gui, "Edit Random Recalculable Trait Pool Sets").addClickActions(() -> RecalculableTraitPoolSetsGUI.show(itemType.randomRecalculableTraitPoolSets, itemType.randomUnrecalculableTraitPoolSets.keySet())),
                new GUITextSpacer(gui),
                new GUITextButton(gui, "Edit Random Unrecalculable Trait Pool Sets").addClickActions(() -> UnrecalculableTraitPoolSetsGUI.show(itemType.randomUnrecalculableTraitPoolSets, itemType.randomRecalculableTraitPoolSets.keySet()))
        );

        //Add main header actions
        cancel.addClickActions(gui::close);
        done.addClickActions(() ->
        {
            //Validation
            if (!traitLevelMultiplier.valid()) return;
            if (!value.valid()) return;


            //Processing
            itemType.slotting = slotting.getText();
            itemType.traitLevelMultiplier = FilterFloat.INSTANCE.parse(traitLevelMultiplier.getText());
            itemType.value = FilterFloat.INSTANCE.parse(value.getText());


            //Close GUI
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return itemType.name;
    }
}
