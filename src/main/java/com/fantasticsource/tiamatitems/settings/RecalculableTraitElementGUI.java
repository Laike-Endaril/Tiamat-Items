package com.fantasticsource.tiamatitems.settings;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tiamatitems.trait.recalculable.element.*;
import com.fantasticsource.tools.datastructures.Color;

public class RecalculableTraitElementGUI extends GUIScreen
{
    protected String typeName;

    protected RecalculableTraitElementGUI(String typeName)
    {
        this.typeName = typeName;
    }

    public static RecalculableTraitElementGUI show(String typeName, CRecalculableTraitElement traitElement)
    {
        RecalculableTraitElementGUI gui = new RecalculableTraitElementGUI(typeName);
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
        if (traitElement instanceof CRTraitElement_LeftClickAction)
        {
            CRTraitElement_LeftClickAction actionElement = (CRTraitElement_LeftClickAction) traitElement;

            GUILabeledTextInput action = new GUILabeledTextInput(gui, " Action Name: ", actionElement.actionName.equals("") ? "ActionName" : actionElement.actionName, FilterNotEmpty.INSTANCE);
            gui.root.addAll(new GUIElement(gui, 1, 0), action);

            //Add main header actions
            done.addClickActions(() ->
            {
                //Processing
                actionElement.actionName = action.getText();


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement instanceof CRTraitElement_RightClickAction)
        {
            CRTraitElement_RightClickAction actionElement = (CRTraitElement_RightClickAction) traitElement;

            GUILabeledTextInput action = new GUILabeledTextInput(gui, " Action Name: ", actionElement.actionName.equals("") ? "ActionName" : actionElement.actionName, FilterNotEmpty.INSTANCE);
            gui.root.addAll(new GUIElement(gui, 1, 0), action);

            //Add main header actions
            done.addClickActions(() ->
            {
                //Processing
                actionElement.actionName = action.getText();


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement instanceof CRTraitElement_ActiveAttributeMod || traitElement instanceof CRTraitElement_PassiveAttributeMod)
        {
            //TODO


            //Add main header actions
            done.addClickActions(() ->
            {
                //Processing
                //TODO


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement instanceof CRTraitElement_PartSlot)
        {
            //TODO


            //Add main header actions
            done.addClickActions(() ->
            {
                //Processing
                //TODO


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement instanceof CRTraitElement_TextureLayers)
        {
            //TODO


            //Add main header actions
            done.addClickActions(() ->
            {
                //Processing
                //TODO


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement instanceof CRTraitElement_AWSkin)
        {
            //TODO


            //Add main header actions
            done.addClickActions(() ->
            {
                //Processing
                //TODO


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement instanceof CRTraitElement_ForcedAWSkinTypeOverride)
        {
            //TODO


            //Add main header actions
            done.addClickActions(() ->
            {
                //Processing
                //TODO


                //Close GUI
                gui.close();
            });
        }
        else
        {
            gui.root.add(new GUIText(gui, "UNKNOWN TRAIT ELEMENT CLASS: " + traitElement.getClass()));
        }


        //Add main header actions
        cancel.addClickActions(gui::close);


        //Return gui reference
        return gui;
    }

    @Override
    public String title()
    {
        return typeName;
    }
}
