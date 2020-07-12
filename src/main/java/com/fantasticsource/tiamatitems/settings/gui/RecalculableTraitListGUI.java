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
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTrait;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.LinkedHashMap;
import java.util.function.Predicate;

public class RecalculableTraitListGUI extends GUIScreen
{
    protected String itemType;

    protected LinkedHashMap<GUILabeledTextInput, CRecalculableTrait> nameElementToRecalculableTraitMap = new LinkedHashMap<>();

    protected RecalculableTraitListGUI(String itemType)
    {
        this.itemType = itemType;
    }

    public static void show(String itemType, LinkedHashMap<String, CRecalculableTrait> list)
    {
        RecalculableTraitListGUI gui = new RecalculableTraitListGUI(itemType);
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

                    ((GUILabeledBoolean) line.getLineElement(5)).setValue(trait.isGood);

                    ((GUILabeledTextInput) line.getLineElement(7)).setText("" + trait.minValue);
                    ((GUILabeledTextInput) line.getLineElement(9)).setText("" + trait.maxValue);

                    ((GUILabeledBoolean) line.getLineElement(11)).setValue(trait.addToCoreOnAssembly);
                });

                return new GUIElement[]
                        {
                                duplicateButton,
                                GUIButton.newListButton(gui).addClickActions(() -> RecalculableTraitGUI.show(name.getText(), gui.nameElementToRecalculableTraitMap.get(name))),
                                new GUIElement(gui, 1, 0),
                                name,
                                new GUIElement(gui, 1, 0),
                                new GUILabeledBoolean(gui, " Is Good: ", new CRecalculableTrait().isGood),
                                new GUIElement(gui, 1, 0),
                                new GUILabeledTextInput(gui, " Min Value: ", "" + new CRecalculableTrait().minValue, FilterFloat.INSTANCE),
                                new GUIElement(gui, 1, 0),
                                new GUILabeledTextInput(gui, " Max Value: ", "" + new CRecalculableTrait().maxValue, FilterFloat.INSTANCE),
                                new GUIElement(gui, 1, 0),
                                new GUILabeledBoolean(gui, " Add to Core on Assembly: ", new CRecalculableTrait().addToCoreOnAssembly)
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
        for (CRecalculableTrait trait : list.values())
        {
            GUIList.Line line = recalculableTraits.addLine();
            GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(3);
            nameElement.setText(trait.name);
            gui.nameElementToRecalculableTraitMap.put(nameElement, trait);
            ((GUILabeledBoolean) line.getLineElement(5)).setValue(trait.isGood);
            ((GUILabeledTextInput) line.getLineElement(7)).setText("" + trait.minValue);
            ((GUILabeledTextInput) line.getLineElement(9)).setText("" + trait.maxValue);
            ((GUILabeledBoolean) line.getLineElement(11)).setValue(trait.addToCoreOnAssembly);
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
                if (!((GUILabeledTextInput) line.getLineElement(7)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(9)).valid()) return;
            }


            //Processing
            list.clear();
            for (GUIList.Line line : recalculableTraits.getLines())
            {
                GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(3);
                CRecalculableTrait trait = gui.nameElementToRecalculableTraitMap.get(nameElement);
                trait.name = nameElement.getText();
                trait.isGood = ((GUILabeledBoolean) line.getLineElement(5)).getValue();
                trait.minValue = FilterFloat.INSTANCE.parse(((GUILabeledTextInput) line.getLineElement(7)).getText());
                trait.maxValue = FilterFloat.INSTANCE.parse(((GUILabeledTextInput) line.getLineElement(9)).getText());
                trait.addToCoreOnAssembly = ((GUILabeledBoolean) line.getLineElement(11)).getValue();
                list.put(trait.name, trait);
            }


            //Close GUI
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return Minecraft.getMinecraft().currentScreen == this ? itemType + " (Static R. Traits)" : itemType;
    }
}
