package com.fantasticsource.tiamatitems.settings.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterFloat;
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
        GUILabeledTextInput chance = new GUILabeledTextInput(gui, " Chance: ", "" + function.chance, FilterFloat.INSTANCE);
        GUILabeledBoolean endIfExecuted = new GUILabeledBoolean(gui, " End if Executed: ", function.endIfExecuted);
        gui.root.addAll(
                new GUITextSpacer(gui),
                chance,
                new GUITextSpacer(gui),
                endIfExecuted
        );

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
                if (!chance.valid()) return;
                if (!addRed.valid() || !addGreen.valid() || !addBlue.valid()) return;


                //Processing
                function.chance = FilterFloat.INSTANCE.parse(chance.getText());
                function.endIfExecuted = endIfExecuted.getValue();

                boostFunction.toAdd[0] = FilterInt.INSTANCE.parse(addRed.getText());
                boostFunction.toAdd[1] = FilterInt.INSTANCE.parse(addGreen.getText());
                boostFunction.toAdd[2] = FilterInt.INSTANCE.parse(addBlue.getText());


                //Close GUI
                gui.close();
            });
        }
        else if (function.getClass() == CRGBGrayscale.class)
        {
            //Add main header actions
            done.addClickActions(() ->
            {
                //Validation
                if (!chance.valid()) return;


                //Processing
                function.chance = FilterFloat.INSTANCE.parse(chance.getText());
                function.endIfExecuted = endIfExecuted.getValue();


                //Close GUI
                gui.close();
            });
        }
        else
        {
            gui.root.clear();
            gui.root.add(new GUIText(gui, "UNKNOWN FUNCTION CLASS: " + function.getClass(), Color.RED));
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
