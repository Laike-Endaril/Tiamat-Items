package com.fantasticsource.tiamatitems.settings.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.Namespace;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTrait;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.LinkedHashMap;
import java.util.function.Predicate;

public class UnrecalculableTraitListGUI extends GUIScreen
{
    protected String itemType;

    protected LinkedHashMap<GUILabeledTextInput, CUnrecalculableTrait> nameElementToUnrecalculableTraitMap = new LinkedHashMap<>();

    protected UnrecalculableTraitListGUI(String itemType)
    {
        this.itemType = itemType;
    }

    public static void show(String itemType, LinkedHashMap<String, CUnrecalculableTrait> list)
    {
        UnrecalculableTraitListGUI gui = new UnrecalculableTraitListGUI(itemType);
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

                gui.nameElementToUnrecalculableTraitMap.put(name, new CUnrecalculableTrait());

                GUIButton duplicateButton = GUIButton.newDuplicateButton(screen);
                duplicateButton.addClickActions(() ->
                {
                    int lineIndex = getLineIndexContaining(name);
                    if (lineIndex == -1) lineIndex = lineCount() - 1;
                    lineIndex++;
                    GUIList.Line line = addLine(lineIndex);

                    String nameString2 = namespace.getFirstAvailableNumberedName(name.getText() + "_Copy");
                    CUnrecalculableTrait trait = (CUnrecalculableTrait) gui.nameElementToUnrecalculableTraitMap.get(name).copy();

                    GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(3);
                    nameElement.setText(trait.name);
                    trait.name = nameString2;

                    gui.nameElementToUnrecalculableTraitMap.put(nameElement, trait);
                });

                return new GUIElement[]
                        {
                                duplicateButton,
                                GUIButton.newListButton(gui).addClickActions(() -> UnrecalculableTraitGUI.show(name.getText(), gui.nameElementToUnrecalculableTraitMap.get(name))),
                                new GUIElement(gui, 1, 0),
                                name
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
        for (CUnrecalculableTrait trait : list.values())
        {
            GUIList.Line line = unrecalculableTraits.addLine();
            GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(3);
            nameElement.setText(trait.name);
            gui.nameElementToUnrecalculableTraitMap.put(nameElement, trait);
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
            }


            //Processing
            list.clear();
            for (GUIList.Line line : unrecalculableTraits.getLines())
            {
                GUILabeledTextInput nameElement = (GUILabeledTextInput) line.getLineElement(3);
                CUnrecalculableTrait trait = gui.nameElementToUnrecalculableTraitMap.get(nameElement);
                trait.name = nameElement.getText();
                list.put(trait.name, trait);
            }


            //Close GUI
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return Minecraft.getMinecraft().currentScreen == this ? itemType + " (Static U. Traits)" : itemType;
    }
}
