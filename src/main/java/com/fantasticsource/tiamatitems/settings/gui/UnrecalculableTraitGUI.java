package com.fantasticsource.tiamatitems.settings.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.mctools.gui.screen.TextSelectionGUI;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTrait;
import com.fantasticsource.tiamatitems.trait.unrecalculable.CUnrecalculableTraitElement;
import com.fantasticsource.tiamatitems.trait.unrecalculable.element.CUTraitElement_AWSkin;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.LinkedHashMap;
import java.util.Map;

public class UnrecalculableTraitGUI extends GUIScreen
{
    public static final LinkedHashMap<String, Class<? extends CUnrecalculableTraitElement>> OPTIONS = new LinkedHashMap<>();

    static
    {
        OPTIONS.put(" AW Skin", CUTraitElement_AWSkin.class);
    }


    protected String traitName;

    protected LinkedHashMap<GUIText, CUnrecalculableTraitElement> typeElementToUnrecalculableTraitElementMap = new LinkedHashMap<>();

    protected UnrecalculableTraitGUI(String traitName)
    {
        this.traitName = traitName;
    }

    public static void show(String traitName, CUnrecalculableTrait trait)
    {
        UnrecalculableTraitGUI gui = new UnrecalculableTraitGUI(traitName);
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
        GUIList unrecalculableTraitElements = new GUIList(gui, true, 0.98, 1 - (cancel.y + cancel.height))
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                GUIText type = new GUIText(gui, " Select Type...", getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
                GUIText description = new GUIText(gui, " (No type selected)");

                Runnable action = () ->
                {
                    if (!type.getText().equals(" Select Type..."))
                    {
                        UnrecalculableTraitElementGUI.show(type.getText().replaceFirst(" ", ""), gui.typeElementToUnrecalculableTraitElementMap.get(type)).addOnClosedActions(() ->
                                description.setText(" " + gui.typeElementToUnrecalculableTraitElementMap.get(type).getDescription()));
                    }
                };

                return new GUIElement[]
                        {
                                GUIButton.newEditButton(gui).addClickActions(action),
                                new GUIElement(gui, 1, 0),
                                type.addClickActions(() -> new TextSelectionGUI(type, " (U. Trait Element Type)", OPTIONS.keySet().toArray(new String[0])).addOnClosedActions(() ->
                                {
                                    CUnrecalculableTraitElement traitElement = gui.typeElementToUnrecalculableTraitElementMap.get(type);
                                    if (type.getText().equals(" Select Type..."))
                                    {
                                        gui.typeElementToUnrecalculableTraitElementMap.remove(type);
                                        description.setText(" (No type selected)");
                                    }
                                    else
                                    {
                                        Class<? extends CUnrecalculableTraitElement> cls = OPTIONS.get(type.getText());
                                        if (traitElement == null || traitElement.getClass() != cls)
                                        {
                                            try
                                            {
                                                CUnrecalculableTraitElement element = cls.newInstance();
                                                gui.typeElementToUnrecalculableTraitElementMap.put(type, element);
                                                description.setText(" " + element.getDescription());
                                            }
                                            catch (InstantiationException | IllegalAccessException e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                })),
                                new GUIElement(gui, 1, 0),
                                description.addClickActions(action)
                        };
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (cancel.y + cancel.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, unrecalculableTraitElements);
        gui.root.addAll
                (
                        unrecalculableTraitElements,
                        scrollbar
                );
        for (CUnrecalculableTraitElement traitElement : trait.elements)
        {
            GUIList.Line line = unrecalculableTraitElements.addLine();
            for (Map.Entry<String, Class<? extends CUnrecalculableTraitElement>> entry : OPTIONS.entrySet())
            {
                if (traitElement.getClass() == entry.getValue())
                {
                    GUIText typeElement = (GUIText) line.getLineElement(2);
                    typeElement.setText(entry.getKey());
                    gui.typeElementToUnrecalculableTraitElementMap.put(typeElement, traitElement);
                    break;
                }
            }
            ((GUIText) line.getLineElement(4)).setText(" " + traitElement.getDescription());
        }


        //Add main header actions
        cancel.addRecalcActions(() ->
        {
            unrecalculableTraitElements.height = 1 - (cancel.y + cancel.height);
            scrollbar.height = 1 - (cancel.y + cancel.height);
        });
        cancel.addClickActions(gui::close);
        done.addClickActions(() ->
        {
            //Processing
            trait.elements.clear();
            for (GUIList.Line line : unrecalculableTraitElements.getLines())
            {
                GUIText typeElement = (GUIText) line.getLineElement(2);
                if (typeElement.getText().equals(" Select Type...")) continue;

                trait.elements.add(gui.typeElementToUnrecalculableTraitElementMap.get(typeElement));
            }


            //Close GUI
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return Minecraft.getMinecraft().currentScreen == this ? traitName + " (U. Trait)" : traitName;
    }
}
