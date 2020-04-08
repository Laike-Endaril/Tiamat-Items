package com.fantasticsource.tiamatitems.settings;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBoolean;
import com.fantasticsource.mctools.gui.element.text.filter.FilterFloat;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.mctools.gui.element.text.filter.FilterWhitelist;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.mctools.gui.screen.TextSelectionGUI;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes.CRGBBoost;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes.CRGBFunction;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes.CRGBGrayscale;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.dyes.CRandomRGB;
import com.fantasticsource.tools.datastructures.Color;

import java.util.LinkedHashMap;
import java.util.Map;

public class CRandomRGBGUI extends GUIScreen
{
    public static final LinkedHashMap<String, Class<? extends CRGBFunction>> FUNCTION_TYPES = new LinkedHashMap<>();
    public static final LinkedHashMap<String, Integer> PAINT_TYPES = new LinkedHashMap<>();

    static
    {
        FUNCTION_TYPES.put("Color Boost", CRGBBoost.class);
        FUNCTION_TYPES.put("Grayscale", CRGBGrayscale.class);

        //TODO get paint types from PaintRegistry.REGISTERED_TYPES
    }

    public static final FilterWhitelist PAINT_FILTER = new FilterWhitelist(PAINT_TYPES.keySet().toArray(new String[0]));
    public static final FilterRangedInt UBYTE_FILTER = FilterRangedInt.get(0, 255);


    protected LinkedHashMap<GUIButton, CRGBFunction> editButtonToCRGBFunctionMap = new LinkedHashMap<>();

    protected CRandomRGB randomRGB;

    protected CRandomRGBGUI(CRandomRGB randomRGB)
    {
        this.randomRGB = randomRGB;
    }

    public static CRandomRGBGUI show(CRandomRGB randomRGB)
    {
        CRandomRGBGUI gui = new CRandomRGBGUI(randomRGB);
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
        String paintString = "INVALID PAINT TYPE!";
        for (Map.Entry<String, Integer> entry : PAINT_TYPES.entrySet())
        {
            if (entry.getValue() == randomRGB.paintType)
            {
                paintString = entry.getKey();
                break;
            }
        }
        GUILabeledTextInput paintType = new GUILabeledTextInput(gui, " Paint Type: ", paintString, PAINT_FILTER);
        GUILabeledTextInput rMin = new GUILabeledTextInput(gui, " Red Base Minimum: ", "" + randomRGB.rMin, UBYTE_FILTER);
        GUILabeledTextInput rMax = new GUILabeledTextInput(gui, " to ", "" + randomRGB.rMax, UBYTE_FILTER);
        GUILabeledTextInput gMin = new GUILabeledTextInput(gui, " Green Base Minimum: ", "" + randomRGB.gMin, UBYTE_FILTER);
        GUILabeledTextInput gMax = new GUILabeledTextInput(gui, " to ", "" + randomRGB.gMax, UBYTE_FILTER);
        GUILabeledTextInput bMin = new GUILabeledTextInput(gui, " Blue Base Minimum: ", "" + randomRGB.bMin, UBYTE_FILTER);
        GUILabeledTextInput bMax = new GUILabeledTextInput(gui, " to ", "" + randomRGB.bMax, UBYTE_FILTER);
        GUIGradientBorder separator = new GUIGradientBorder(gui, 1, 0.02, 0.3, Color.WHITE, Color.BLANK);
        gui.root.addAll
                (
                        new GUITextSpacer(gui),
                        paintType,
                        new GUITextSpacer(gui),
                        rMin, rMax,
                        new GUITextSpacer(gui),
                        gMin, gMax,
                        new GUITextSpacer(gui),
                        bMin, bMax,
                        separator
                );

        GUIList functions = new GUIList(gui, true, 0.98, 1 - (separator.y + separator.height))
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                GUIText type = new GUIText(gui, FUNCTION_TYPES.keySet().iterator().next());

                CRGBFunction function = null;
                try
                {
                    function = FUNCTION_TYPES.get(type.getText()).newInstance();
                }
                catch (InstantiationException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }

                GUIButton editButton = GUIButton.newEditButton(gui);
                gui.editButtonToCRGBFunctionMap.put(editButton, function);

                GUIText description = new GUIText(gui, function.description(), getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);

                GUILabeledTextInput chance = new GUILabeledTextInput(gui, " Chance: ", "" + function.chance, FilterFloat.INSTANCE);
                GUILabeledTextInput endIfExecuted = new GUILabeledTextInput(gui, " End if Executed: ", "" + function.endIfExecuted, FilterBoolean.INSTANCE);

                return new GUIElement[]
                        {
                                editButton.addClickActions(() -> CRGBFunctionGUI.show(gui.editButtonToCRGBFunctionMap.get(editButton))),
                                new GUIElement(gui, 1, 0),
                                type.addClickActions(() -> new TextSelectionGUI(type, "Function Type Selection", FUNCTION_TYPES.keySet().toArray(new String[0])).addOnClosedActions(() ->
                                {
                                    CRGBFunction function2 = null;
                                    try
                                    {
                                        function2 = FUNCTION_TYPES.get(type.getText()).newInstance();
                                    }
                                    catch (InstantiationException | IllegalAccessException e)
                                    {
                                        e.printStackTrace();
                                    }
                                    if (function2.getClass() != gui.editButtonToCRGBFunctionMap.get(editButton).getClass())
                                    {
                                        description.setText(function2.description());
                                        gui.editButtonToCRGBFunctionMap.put(editButton, function2);
                                    }
                                })),
                                new GUIElement(gui, 1, 0),
                                description,
                                new GUIElement(gui, 1, 0),
                                chance,
                                new GUIElement(gui, 1, 0),
                                endIfExecuted
                        };
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (separator.y + separator.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, functions);
        gui.root.addAll(functions, scrollbar);
        for (CRGBFunction function : randomRGB.functions)
        {
            GUIList.Line line = functions.addLine();
            GUIButton editButton = (GUIButton) line.getLineElement(0);
            gui.editButtonToCRGBFunctionMap.put(editButton, function);

            String typeString = null;
            for (Map.Entry<String, Class<? extends CRGBFunction>> entry : FUNCTION_TYPES.entrySet())
            {
                if (entry.getValue() == function.getClass())
                {
                    typeString = entry.getKey();
                    break;
                }
            }
            ((GUIText) line.getLineElement(2)).setText(typeString);
            ((GUIText) line.getLineElement(4)).setText(function.description());
            ((GUILabeledTextInput) line.getLineElement(6)).setText("" + function.chance);
            ((GUILabeledTextInput) line.getLineElement(8)).setText("" + function.endIfExecuted);
        }


        //Add main header actions
        cancel.addRecalcActions(() ->
        {
            functions.height = 1 - (cancel.y + cancel.height);
            scrollbar.height = 1 - (cancel.y + cancel.height);
        });
        cancel.addClickActions(gui::close);
        done.addClickActions(() ->
        {
            //Validation
            if (!paintType.valid() || !rMin.valid() || !rMax.valid() || !gMin.valid() || !gMax.valid() || !bMin.valid() || !bMax.valid()) return;
            for (GUIList.Line line : functions.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(6)).valid()) return;
                if (!((GUILabeledTextInput) line.getLineElement(8)).valid()) return;
            }


            //Processing
            randomRGB.paintType = PAINT_TYPES.get(paintType.getText());
            randomRGB.rMin = UBYTE_FILTER.parse(rMin.getText());
            randomRGB.rMax = UBYTE_FILTER.parse(rMax.getText());
            randomRGB.gMin = UBYTE_FILTER.parse(gMin.getText());
            randomRGB.gMax = UBYTE_FILTER.parse(gMax.getText());
            randomRGB.bMin = UBYTE_FILTER.parse(bMin.getText());
            randomRGB.bMax = UBYTE_FILTER.parse(bMax.getText());
            randomRGB.functions.clear();
            for (GUIList.Line line : functions.getLines())
            {
                GUIButton editButton = (GUIButton) line.getLineElement(0);
                CRGBFunction function = gui.editButtonToCRGBFunctionMap.get(editButton);
                if (function == null) continue;

                randomRGB.functions.add(function);
                function.chance = FilterFloat.INSTANCE.parse(((GUILabeledTextInput) line.getLineElement(6)).getText());
                function.endIfExecuted = FilterBoolean.INSTANCE.parse(((GUILabeledTextInput) line.getLineElement(8)).getText());
            }


            //Close GUI
            gui.close();
        });


        //Recalc once to fix texture colors
        gui.recalc();


        //Return gui reference
        return gui;
    }

    @Override
    public String title()
    {
        return "Random Color Generator";
    }
}
