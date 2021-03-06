package com.fantasticsource.tiamatitems.settings.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.Namespace;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledBoolean;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterFloat;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTrait;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTraitPool;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

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
                Namespace namespace = gui.namespaces.computeIfAbsent("Unrecalculable Traits", o -> new Namespace());
                String nameString = namespace.getFirstAvailableNumberedName("UTrait");
                GUILabeledTextInput name = new GUILabeledTextInput(gui, " Trait Name: ", nameString, FilterNotEmpty.INSTANCE).setNamespace("Unrecalculable Traits");
                GUILabeledTextInput weight = new GUILabeledTextInput(gui, " Trait Weight: ", "1", WEIGHT_FILTER);
                GUILabeledBoolean isGood = new GUILabeledBoolean(gui, " Is Good: ", new CUnrecalculableTrait().isGood);
                GUILabeledTextInput minValue = new GUILabeledTextInput(gui, " Min Value: ", "" + new CUnrecalculableTrait().minValue, FilterFloat.INSTANCE);
                GUILabeledTextInput maxValue = new GUILabeledTextInput(gui, " Max Value: ", "" + new CUnrecalculableTrait().maxValue, FilterFloat.INSTANCE);

                gui.nameElementToUnrecalculableTraitMap.put(name, new CUnrecalculableTrait());

                GUIButton duplicateButton = GUIButton.newDuplicateButton(screen);
                duplicateButton.addClickActions(() ->
                {
                    GUIList.Line line = addLine(getLineIndexContaining(name) + 1);

                    CUnrecalculableTrait trait = (CUnrecalculableTrait) gui.nameElementToUnrecalculableTraitMap.get(name).copy();
                    trait.name = namespace.getFirstAvailableNumberedName(name.getText() + "_Copy");

                    GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(3);
                    nameElement.setText(trait.name);

                    gui.nameElementToUnrecalculableTraitMap.put(nameElement, trait);

                    ((GUILabeledTextInput) line.getLineElement(5)).setText(weight.getText());
                    ((GUILabeledBoolean) line.getLineElement(7)).setValue(isGood.getValue());
                    ((GUILabeledTextInput) line.getLineElement(9)).setText(minValue.getText());
                    ((GUILabeledTextInput) line.getLineElement(11)).setText(maxValue.getText());
                });

                return new GUIElement[]
                        {
                                duplicateButton,
                                GUIButton.newListButton(gui).addClickActions(() -> UnrecalculableTraitGUI.show(name.getText(), gui.nameElementToUnrecalculableTraitMap.get(name))),
                                new GUIElement(gui, 1, 0),
                                name,
                                new GUIElement(gui, 1, 0),
                                weight,
                                new GUIElement(gui, 1, 0),
                                isGood,
                                new GUIElement(gui, 1, 0),
                                minValue,
                                new GUIElement(gui, 1, 0),
                                maxValue,
                        };
            }
        };
        unrecalculableTraits.addRemoveChildActions((Predicate<GUIElement>) element ->
        {
            if (element instanceof GUIList.Line)
            {
                GUIList.Line line = (GUIList.Line) element;
                GUILabeledTextInput labeledTextInput = (GUILabeledTextInput) line.getLineElement(3);
                gui.namespaces.get("Unrecalculable Traits").inputs.remove(labeledTextInput.input);
            }
            return false;
        });
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (cancel.y + cancel.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, unrecalculableTraits);
        gui.root.addAll
                (
                        unrecalculableTraits,
                        scrollbar
                );
        for (Map.Entry<CUnrecalculableTrait, Integer> entry : pool.traitGenWeights.entrySet())
        {
            CUnrecalculableTrait trait = entry.getKey();
            GUIList.Line line = unrecalculableTraits.addLine();
            GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(3);
            nameElement.setText(trait.name);
            gui.nameElementToUnrecalculableTraitMap.put(nameElement, trait);
            ((GUILabeledTextInput) line.getLineElement(5)).setText("" + entry.getValue());
            ((GUILabeledBoolean) line.getLineElement(7)).setValue(trait.isGood);
            ((GUILabeledTextInput) line.getLineElement(9)).setText("" + trait.minValue);
            ((GUILabeledTextInput) line.getLineElement(11)).setText("" + trait.maxValue);
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
                if (!((GUILabeledTextInput) line.getLineElement(3)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(5)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(9)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(11)).valid()) return;
            }


            //Processing
            pool.traitGenWeights.clear();
            for (GUIList.Line line : unrecalculableTraits.getLines())
            {
                GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(3);
                CUnrecalculableTrait trait = gui.nameElementToUnrecalculableTraitMap.get(nameElement);
                trait.name = nameElement.getText();
                pool.traitGenWeights.put(trait, WEIGHT_FILTER.parse(((GUILabeledTextInput) line.getLineElement(5)).getText()));
                trait.isGood = ((GUILabeledBoolean) line.getLineElement(7)).getValue();
                trait.minValue = FilterFloat.INSTANCE.parse(((GUILabeledTextInput) line.getLineElement(9)).getText());
                trait.maxValue = FilterFloat.INSTANCE.parse(((GUILabeledTextInput) line.getLineElement(11)).getText());
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
