package com.fantasticsource.tiamatitems.settings.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tools.datastructures.Color;

import java.util.LinkedHashSet;

public class PoolListGUI extends GUIScreen
{
    protected String title;

    protected PoolListGUI(String title)
    {
        this.title = title;
    }

    public static void show(String title, LinkedHashSet<String> poolNames)
    {
        PoolListGUI gui = new PoolListGUI(title);
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
                return new GUIElement[]
                        {
                                new GUILabeledTextInput(gui, " Pool Name: ", "PoolName", FilterNotEmpty.INSTANCE).setNamespace("Recalculable Trait Pools")
                        };
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (cancel.y + cancel.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, recalculableTraits);
        gui.root.addAll
                (
                        recalculableTraits,
                        scrollbar
                );
        for (String name : poolNames)
        {
            ((GUILabeledTextInput) recalculableTraits.addLine().getLineElement(0)).setText(name);
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
                if (!((GUILabeledTextInput) line.getLineElement(0)).valid()) return;
            }


            //Processing
            poolNames.clear();
            for (GUIList.Line line : recalculableTraits.getLines())
            {
                poolNames.add(((GUILabeledTextInput) line.getLineElement(0)).getText());
            }


            //Close GUI
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return title;
    }
}