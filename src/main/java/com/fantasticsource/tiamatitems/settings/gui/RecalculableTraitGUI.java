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
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTrait;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tiamatitems.trait.recalculable.element.*;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Loader;

import java.util.LinkedHashMap;
import java.util.Map;

public class RecalculableTraitGUI extends GUIScreen
{
    public static final LinkedHashMap<String, Class<? extends CRecalculableTraitElement>> OPTIONS = new LinkedHashMap<>();

    static
    {
        OPTIONS.put(" Passive Better Attribute Modifier", CRTraitElement_BetterPassiveAttributeMod.class);
        if (Loader.isModLoaded("faerunutils")) OPTIONS.put(" Faerun Active Armor Attribute Modifier", CRTraitElement_ActiveFaerunArmorAttributeMod.class);
        OPTIONS.put(" Active Attribute Modifier", CRTraitElement_ActiveAttributeMod.class);
        OPTIONS.put(" Passive Attribute Modifier", CRTraitElement_PassiveAttributeMod.class);
        OPTIONS.put(" Stack Size Limit", CRTraitElement_StackLimit.class);
        OPTIONS.put(" Part Slot", CRTraitElement_PartSlot.class);
        OPTIONS.put(" Texture Layers", CRTraitElement_TextureLayers.class);
        OPTIONS.put(" Forced AW Skin Type Override", CRTraitElement_ForcedAWSkinTypeOverride.class);
        OPTIONS.put(" Assembly Name", CRTraitElement_AssemblyName.class);
        OPTIONS.put(" Durability", CRTraitElement_Durability.class);
        OPTIONS.put(" Inventory Slots", CRTraitElement_InventorySlots.class);
        OPTIONS.put(" Generic Double", CRTraitElement_GenericDouble.class);
        OPTIONS.put(" Generic String", CRTraitElement_GenericString.class);
        OPTIONS.put(" Transform On Drop", CRTraitElement_TransformItemOnDrop.class);
    }


    protected String traitName;

    protected LinkedHashMap<GUIText, CRecalculableTraitElement> typeElementToRecalculableTraitElementMap = new LinkedHashMap<>();

    protected RecalculableTraitGUI(String traitName)
    {
        this.traitName = traitName;
    }

    public static void show(String traitName, CRecalculableTrait trait)
    {
        RecalculableTraitGUI gui = new RecalculableTraitGUI(traitName);
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
        GUIList recalculableTraitElements = new GUIList(gui, true, 0.98, 1 - (cancel.y + cancel.height))
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
                        RecalculableTraitElementGUI.show(type.getText().replaceFirst(" ", ""), gui.typeElementToRecalculableTraitElementMap.get(type)).addOnClosedActions(() ->
                                description.setText(" " + gui.typeElementToRecalculableTraitElementMap.get(type).getDescription()));
                    }
                };

                GUIButton duplicateButton = GUIButton.newDuplicateButton(screen);
                duplicateButton.addClickActions(() ->
                {
                    GUIList.Line line = addLine(getLineIndexContaining(type) + 1);

                    GUIText typeElement = (GUIText) line.getLineElement(3);
                    typeElement.setText(type.getText());

                    CRecalculableTraitElement element = (CRecalculableTraitElement) gui.typeElementToRecalculableTraitElementMap.get(type).copy();
                    gui.typeElementToRecalculableTraitElementMap.put(typeElement, element);

                    ((GUIText) line.getLineElement(5)).setText(" " + element.getDescription());
                });

                return new GUIElement[]
                        {
                                duplicateButton,
                                GUIButton.newEditButton(gui).addClickActions(action),
                                new GUIElement(gui, 1, 0),
                                type.addClickActions(() -> new TextSelectionGUI(type, " (R. Trait Element Type)", OPTIONS.keySet().toArray(new String[0])).addOnClosedActions(() ->
                                {
                                    CRecalculableTraitElement traitElement = gui.typeElementToRecalculableTraitElementMap.get(type);
                                    if (type.getText().equals(" Select Type..."))
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
                    GUIText typeElement = (GUIText) line.getLineElement(3);
                    typeElement.setText(entry.getKey());
                    gui.typeElementToRecalculableTraitElementMap.put(typeElement, traitElement);
                    break;
                }
            }
            ((GUIText) line.getLineElement(5)).setText(" " + traitElement.getDescription());
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
                GUIText typeElement = (GUIText) line.getLineElement(3);
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
