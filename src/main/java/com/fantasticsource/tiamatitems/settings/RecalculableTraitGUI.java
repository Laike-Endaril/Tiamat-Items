package com.fantasticsource.tiamatitems.settings;

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
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTrait;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tiamatitems.trait.recalculable.element.*;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class RecalculableTraitGUI extends GUIScreen
{
    public static final LinkedHashMap<String, Class<? extends CRecalculableTraitElement>> OPTIONS = new LinkedHashMap<>();

    static
    {
        OPTIONS.put(" Select Type...", null);
        OPTIONS.put(" Left Click Action", CRTraitElement_LeftClickAction.class);
        OPTIONS.put(" Right Click Action", CRTraitElement_RightClickAction.class);
        OPTIONS.put(" Active Attribute Modifier", CRTraitElement_ActiveAttributeMod.class);
        OPTIONS.put(" Passive Attribute Modifier", CRTraitElement_PassiveAttributeMod.class);
        OPTIONS.put(" Part Slot", CRTraitElement_PartSlot.class);
        OPTIONS.put(" Texture Layers", CRTraitElement_TextureLayers.class);
        OPTIONS.put(" AW Skin", CRTraitElement_AWSkin.class);
        OPTIONS.put(" Forced AW Skin Type Override", CRTraitElement_ForcedAWSkinTypeOverride.class);
    }


    protected String traitName;
    protected CRecalculableTrait trait;

    protected LinkedHashMap<GUIText, CRecalculableTraitElement> typeElementToRecalculableTraitElementMap = new LinkedHashMap<>();

    protected RecalculableTraitGUI(String traitName, CRecalculableTrait trait)
    {
        this.traitName = traitName;
        this.trait = trait;
    }

    public static void show(String poolName, CRecalculableTrait trait)
    {
        RecalculableTraitGUI gui = new RecalculableTraitGUI(poolName, trait);
        showStacked(gui);
        gui.drawStack = false;


        //Background
        gui.root.add(new GUIDarkenedBackground(gui));


        //Header
        GUINavbar navbar = new GUINavbar(gui);
        GUITextButton done = new GUITextButton(gui, "Done", Color.GREEN);
        GUITextButton cancel = new GUITextButton(gui, "Cancel", Color.RED);
        gui.root.addAll(navbar, done, cancel);


        GUIList recalculableTraitElements = new GUIList(gui, true, 0.98, 1 - (cancel.y + cancel.height))
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                GUIText type = new GUIText(gui, " Select Type...", getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
                GUIText description = new GUIText(gui, " (No type selected)");
                return new GUIElement[]
                        {
                                GUIButton.newEditButton(gui),
                                new GUIElement(gui, 1, 0),
                                type.addClickActions(() -> new TextSelectionGUI(type, " (R. Trait Element Type)", OPTIONS.keySet().toArray(new String[0])).addOnClosedActions(() ->
                                {
                                    CRecalculableTraitElement traitElement = gui.typeElementToRecalculableTraitElementMap.get(type);
                                    if (type.getText().equals(" (R. Trait Element Type)"))
                                    {
                                        gui.typeElementToRecalculableTraitElementMap.remove(type);
                                        description.setText(" (No type selected)");
                                    }
                                    else
                                    {
                                        Class<? extends CRecalculableTraitElement> cls = OPTIONS.get(type.getText());
                                        if (traitElement == null || traitElement.getClass() != cls)
                                        {
                                            try
                                            {
                                                CRecalculableTraitElement element = cls.newInstance();
                                                gui.typeElementToRecalculableTraitElementMap.put(type, element);
                                                description.setText(" " + element.getDescription(new ArrayList<>(), new double[0]));
                                            }
                                            catch (InstantiationException | IllegalAccessException e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                })),
                                new GUIElement(gui, 1, 0),
                                description
                        };
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (cancel.y + cancel.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, recalculableTraitElements);
        gui.root.addAll
                (
                        recalculableTraitElements,
                        scrollbar
                );
        for (CRecalculableTraitElement traitElement : trait.elements)
        {
            GUIList.Line line = recalculableTraitElements.addLine();
            for (Map.Entry<String, Class<? extends CRecalculableTraitElement>> entry : OPTIONS.entrySet())
            {
                if (traitElement.getClass() == entry.getValue())
                {
                    ((GUIText) line.getLineElement(2)).setText(entry.getKey());
                    break;
                }
            }
            ((GUIText) line.getLineElement(4)).setText(traitElement.getDescription(new ArrayList<>(), new double[0]));
        }


        //Add main header actions
        cancel.addRecalcActions(() ->
        {
            recalculableTraitElements.height = 1 - (cancel.y + cancel.height);
            scrollbar.height = 1 - (cancel.y + cancel.height);
        });
        cancel.addClickActions(gui::close);
        done.addClickActions(() ->
        {
            //Processing

            trait.elements.clear();
            for (GUIList.Line line : recalculableTraitElements.getLines())
            {
                GUIText typeElement = (GUIText) line.getLineElement(2);
                if (typeElement.getText().equals(" Select Type...")) continue;

                trait.elements.add(gui.typeElementToRecalculableTraitElementMap.get(typeElement));
            }


            //Close GUI
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return Minecraft.getMinecraft().currentScreen == this ? traitName + " (R. Trait)" : traitName;
    }
}
