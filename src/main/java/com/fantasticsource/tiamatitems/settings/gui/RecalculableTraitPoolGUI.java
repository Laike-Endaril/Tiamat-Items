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
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTrait;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitPool;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class RecalculableTraitPoolGUI extends GUIScreen
{
    public static final FilterRangedInt WEIGHT_FILTER = FilterRangedInt.get(1, Integer.MAX_VALUE);


    protected String poolName;

    protected LinkedHashMap<GUILabeledTextInput, CRecalculableTrait> nameElementToRecalculableTraitMap = new LinkedHashMap<>();

    protected RecalculableTraitPoolGUI(String poolName)
    {
        this.poolName = poolName;
    }

    public static void show(String poolName, CRecalculableTraitPool pool)
    {
        RecalculableTraitPoolGUI gui = new RecalculableTraitPoolGUI(poolName);
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
        GUIList recalculableTraits = new GUIList(gui, true, 0.98, 1 - (cancel.y + cancel.height))
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                Namespace namespace = gui.namespaces.computeIfAbsent("Recalculable Traits", o -> new Namespace());
                String nameString = namespace.getFirstAvailableNumberedName("RTrait");
                GUILabeledTextInput name = new GUILabeledTextInput(gui, " Trait Name: ", nameString, FilterNotEmpty.INSTANCE).setNamespace("Recalculable Traits");
                GUILabeledTextInput weight = new GUILabeledTextInput(gui, " Trait Weight: ", "1", WEIGHT_FILTER);
                GUILabeledBoolean isGood = new GUILabeledBoolean(gui, " Is Good: ", new CRecalculableTrait().isGood);
                GUILabeledTextInput minValue = new GUILabeledTextInput(gui, " Min Value: ", "" + new CRecalculableTrait().minValue, FilterFloat.INSTANCE);
                GUILabeledTextInput maxValue = new GUILabeledTextInput(gui, " Max Value: ", "" + new CRecalculableTrait().maxValue, FilterFloat.INSTANCE);
                GUILabeledBoolean addToAssembly = new GUILabeledBoolean(gui, " Add to Assembly from Part: ", new CRecalculableTrait().addToAssemblyFromPart);

                gui.nameElementToRecalculableTraitMap.put(name, new CRecalculableTrait());

                GUIButton duplicateButton = GUIButton.newDuplicateButton(screen);
                duplicateButton.addClickActions(() ->
                {
                    int lineIndex = getLineIndexContaining(name);
                    if (lineIndex == -1) lineIndex = lineCount() - 1;
                    lineIndex++;
                    GUIList.Line line = addLine(lineIndex);

                    CRecalculableTrait trait = (CRecalculableTrait) gui.nameElementToRecalculableTraitMap.get(name).copy();
                    trait.name = namespace.getFirstAvailableNumberedName(name.getText() + "_Copy");

                    GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(3);
                    nameElement.setText(trait.name);

                    gui.nameElementToRecalculableTraitMap.put(nameElement, trait);

                    ((GUILabeledTextInput) line.getLineElement(5)).setText(weight.getText());

                    ((GUILabeledBoolean) line.getLineElement(7)).setValue(isGood.getValue());

                    ((GUILabeledTextInput) line.getLineElement(9)).setText(minValue.getText());
                    ((GUILabeledTextInput) line.getLineElement(11)).setText(maxValue.getText());

                    ((GUILabeledBoolean) line.getLineElement(13)).setValue(addToAssembly.getValue());
                });

                return new GUIElement[]
                        {
                                duplicateButton,
                                GUIButton.newListButton(gui).addClickActions(() -> RecalculableTraitGUI.show(name.getText(), gui.nameElementToRecalculableTraitMap.get(name))),
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
                                new GUIElement(gui, 1, 0),
                                addToAssembly
                        };
            }
        };
        recalculableTraits.addRemoveChildActions((Predicate<GUIElement>) element ->
        {
            if (element instanceof GUIList.Line)
            {
                GUIList.Line line = (GUIList.Line) element;
                GUILabeledTextInput labeledTextInput = (GUILabeledTextInput) line.getLineElement(3);
                gui.namespaces.get("Recalculable Traits").inputs.remove(labeledTextInput.input);
            }
            return false;
        });
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (cancel.y + cancel.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, recalculableTraits);
        gui.root.addAll
                (
                        recalculableTraits,
                        scrollbar
                );
        for (Map.Entry<CRecalculableTrait, Integer> entry : pool.traitGenWeights.entrySet())
        {
            CRecalculableTrait trait = entry.getKey();
            GUIList.Line line = recalculableTraits.addLine();
            GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(3);
            nameElement.setText(trait.name);
            gui.nameElementToRecalculableTraitMap.put(nameElement, trait);
            ((GUILabeledTextInput) line.getLineElement(5)).setText("" + entry.getValue());
            ((GUILabeledBoolean) line.getLineElement(7)).setValue(trait.isGood);
            ((GUILabeledTextInput) line.getLineElement(9)).setText("" + trait.minValue);
            ((GUILabeledTextInput) line.getLineElement(11)).setText("" + trait.maxValue);
            ((GUILabeledBoolean) line.getLineElement(13)).setValue(trait.addToAssemblyFromPart);
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
                if (!((GUILabeledTextInput) line.getLineElement(3)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(5)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(9)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(11)).valid()) return;
            }


            //Processing
            pool.traitGenWeights.clear();
            for (GUIList.Line line : recalculableTraits.getLines())
            {
                GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(3);
                CRecalculableTrait trait = gui.nameElementToRecalculableTraitMap.get(nameElement);
                trait.name = nameElement.getText();
                pool.traitGenWeights.put(trait, WEIGHT_FILTER.parse(((GUILabeledTextInput) line.getLineElement(5)).getText()));
                trait.isGood = ((GUILabeledBoolean) line.getLineElement(7)).getValue();
                trait.minValue = FilterFloat.INSTANCE.parse(((GUILabeledTextInput) line.getLineElement(9)).getText());
                trait.maxValue = FilterFloat.INSTANCE.parse(((GUILabeledTextInput) line.getLineElement(11)).getText());
                trait.addToAssemblyFromPart = ((GUILabeledBoolean) line.getLineElement(13)).getValue();
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
