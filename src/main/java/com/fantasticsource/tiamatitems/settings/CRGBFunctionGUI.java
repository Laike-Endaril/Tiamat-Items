package com.fantasticsource.tiamatitems.settings;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes.CRGBBoost;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes.CRGBFunction;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes.CRGBGrayscale;
import com.fantasticsource.tools.datastructures.Color;

public class CRGBFunctionGUI extends GUIScreen
{
    protected CRGBFunction function;

    protected CRGBFunctionGUI(CRGBFunction function)
    {
        this.function = function;
    }

    public static CRGBFunctionGUI show(CRGBFunction function)
    {
        CRGBFunctionGUI gui = new CRGBFunctionGUI(function);
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
        if (function.getClass() == CRGBBoost.class)
        {
            CRGBBoost boostFunction = (CRGBBoost) function;

            GUILabeledTextInput addRed = new GUILabeledTextInput(gui, " Add Red: ", "" + boostFunction.toAdd[0], FilterInt.INSTANCE);
            GUILabeledTextInput addGreen = new GUILabeledTextInput(gui, " Add Green: ", "" + boostFunction.toAdd[1], FilterInt.INSTANCE);
            GUILabeledTextInput addBlue = new GUILabeledTextInput(gui, " Add Blue: ", "" + boostFunction.toAdd[2], FilterInt.INSTANCE);
            gui.root.addAll(
                    new GUITextSpacer(gui),
                    addRed,
                    new GUITextSpacer(gui),
                    addGreen,
                    new GUITextSpacer(gui),
                    addBlue
            );

            //Add main header actions
            done.addClickActions(() ->
            {
                //Validation
                if (!addRed.valid() || !addGreen.valid() || !addBlue.valid()) return;


                //Processing
                boostFunction.toAdd[0] = FilterInt.INSTANCE.parse(addRed.getText());
                boostFunction.toAdd[1] = FilterInt.INSTANCE.parse(addGreen.getText());
                boostFunction.toAdd[2] = FilterInt.INSTANCE.parse(addBlue.getText());


                //Close GUI
                gui.close();
            });
        }
        else if (function.getClass() == CRGBGrayscale.class)
        {
            gui.root.addAll(new GUITextSpacer(gui), new GUIText(gui, " (No additional options for this function type)"));

            //Add main header actions
            done.addClickActions(() ->
            {
                //Close GUI
                gui.close();
            });
        }
        else
        {
            gui.root.add(new GUIText(gui, "UNKNOWN FUNCTION CLASS: " + function.getClass()));
        }


        //Add main header actions
        cancel.addClickActions(gui::close);


        //Recalc once to fix any colors
        gui.recalc();


        //Return gui reference
        return gui;
    }

    @Override
    public String title()
    {
        return function.name();
    }
}
