package com.fantasticsource.tiamatitems.settings.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.mctools.gui.screen.TextSelectionGUI;
import com.fantasticsource.tiamatitems.dyes.CRGBBoost;
import com.fantasticsource.tiamatitems.dyes.CRGBFunction;
import com.fantasticsource.tiamatitems.dyes.CRGBGrayscale;
import com.fantasticsource.tiamatitems.dyes.CRandomRGB;
import com.fantasticsource.tools.datastructures.Color;
import moe.plushie.armourers_workshop.api.ArmourersWorkshopApi;
import moe.plushie.armourers_workshop.api.common.painting.IPaintType;

import java.util.LinkedHashMap;
import java.util.Map;

public class CRandomRGBGUI extends GUIScreen
{
    public static final LinkedHashMap<String, Class<? extends CRGBFunction>> FUNCTION_TYPES = new LinkedHashMap<>();
    public static final LinkedHashMap<String, Integer> PAINT_TYPES = new LinkedHashMap<>();
    public static final FilterRangedInt UBYTE_FILTER = FilterRangedInt.get(0, 255);

    static
    {
        FUNCTION_TYPES.put("Color Boost", CRGBBoost.class);
        FUNCTION_TYPES.put("Grayscale", CRGBGrayscale.class);


        for (IPaintType paintType : ArmourersWorkshopApi.paintTypeRegistry.getRegisteredTypes())
        {
            PAINT_TYPES.put(paintType.getLocalizedName(), paintType.getId());
        }
    }

    protected LinkedHashMap<GUIButton, CRGBFunction> editButtonToCRGBFunctionMap = new LinkedHashMap<>();

    protected CRandomRGB randomRGB;
    protected int dyeIndex;

    protected CRandomRGBGUI(CRandomRGB randomRGB, int dyeIndex)
    {
        this.randomRGB = randomRGB;
        this.dyeIndex = dyeIndex;
    }

    public static CRandomRGBGUI show(CRandomRGB randomRGB, int dyeIndex)
    {
        CRandomRGBGUI gui = new CRandomRGBGUI(randomRGB, dyeIndex);
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
        GUIText paintTypeLabel = new GUIText(gui, " Paint Type: ");
        GUIText paintType = new GUIText(gui, paintString, getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
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
                        paintTypeLabel.addClickActions(() -> new TextSelectionGUI(paintType, "Paint Type Selection", PAINT_TYPES.keySet().toArray(new String[0]))),
                        paintType.addClickActions(() -> new TextSelectionGUI(paintType, "Paint Type Selection", PAINT_TYPES.keySet().toArray(new String[0]))),
                        new GUITextSpacer(gui),
                        rMin, rMax,
                        new GUITextSpacer(gui),
                        gMin, gMax,
                        new GUITextSpacer(gui),
                        bMin, bMax,
                        new GUITextSpacer(gui),
                        new GUIText(gui, " Functions...", Color.YELLOW),
                        separator
                );

        GUIList functions = new GUIList(gui, true, 0.98, 1 - (separator.y + separator.height))
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                GUIText type = new GUIText(gui, FUNCTION_TYPES.keySet().iterator().next(), getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);

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

                GUIText description = new GUIText(gui, function.description());

                GUIButton duplicateButton = GUIButton.newDuplicateButton(screen);
                duplicateButton.addClickActions(() ->
                {
                    int index = getLineIndexContaining(type);
                    if (index == -1) index = lineCount() - 1;
                    index++;
                    GUIList.Line line = addLine(index);

                    CRGBFunction function2 = (CRGBFunction) gui.editButtonToCRGBFunctionMap.get(editButton).copy();
                    gui.editButtonToCRGBFunctionMap.put((GUIButton) line.getLineElement(0), function2);

                    ((GUIText) line.getLineElement(2)).setText(type.getText());
                    ((GUIText) line.getLineElement(4)).setText(function2.description());
                });

                Runnable action = () -> CRGBFunctionGUI.show(gui.editButtonToCRGBFunctionMap.get(editButton)).addOnClosedActions(() -> description.setText(gui.editButtonToCRGBFunctionMap.get(editButton).description()));
                return new GUIElement[]
                        {
                                editButton.addClickActions(action),
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
                                description.addClickActions(action)
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
            if (!rMin.valid() || !rMax.valid() || !gMin.valid() || !gMax.valid() || !bMin.valid() || !bMax.valid()) return;


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
        return "Dye Channel " + dyeIndex;
    }
}
