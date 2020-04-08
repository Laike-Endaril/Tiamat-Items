package com.fantasticsource.tiamatitems.settings;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.*;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tiamatitems.trait.recalculable.element.*;
import com.fantasticsource.tools.datastructures.Color;

public class RecalculableTraitElementGUI extends GUIScreen
{
    public static final FilterRangedInt OPERATION_FILTER = FilterRangedInt.get(0, 2);

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
                //Validation
                if (!action.valid()) return;


                //Processing
                actionElement.actionName = action.getText();


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement instanceof CRTraitElement_ActiveAttributeMod)
        {
            CRTraitElement_ActiveAttributeMod actionElement = (CRTraitElement_ActiveAttributeMod) traitElement;

            GUILabeledTextInput attribute = new GUILabeledTextInput(gui, " Attribute Name: ", actionElement.attributeName.equals("") ? "generic.name" : actionElement.attributeName, FilterNotEmpty.INSTANCE);
            GUILabeledTextInput isGood = new GUILabeledTextInput(gui, " Is Good Attribute: ", "" + actionElement.isGood, FilterBoolean.INSTANCE);
            GUILabeledTextInput operation = new GUILabeledTextInput(gui, " Operation: ", "" + actionElement.operation, OPERATION_FILTER);
            GUILabeledTextInput minAmount = new GUILabeledTextInput(gui, " Min Amount: ", "" + actionElement.minAmount, FilterFloat.INSTANCE);
            GUILabeledTextInput maxAmount = new GUILabeledTextInput(gui, " Max Amount: ", "" + actionElement.maxAmount, FilterFloat.INSTANCE);
            gui.root.addAll(
                    new GUIElement(gui, 1, 0),
                    attribute,
                    new GUIElement(gui, 1, 0),
                    isGood,
                    new GUIElement(gui, 1, 0),
                    operation,
                    new GUIElement(gui, 1, 0),
                    minAmount,
                    new GUIElement(gui, 1, 0),
                    maxAmount
            );

            //Add main header actions
            done.addClickActions(() ->
            {
                //Validation
                if (!attribute.valid() || !isGood.valid() || !operation.valid() || !minAmount.valid() || !maxAmount.valid()) return;


                //Processing
                actionElement.attributeName = attribute.getText();
                actionElement.isGood = FilterBoolean.INSTANCE.parse(isGood.getText());
                actionElement.operation = FilterInt.INSTANCE.parse(operation.getText());
                actionElement.minAmount = FilterFloat.INSTANCE.parse(minAmount.getText());
                actionElement.maxAmount = FilterFloat.INSTANCE.parse(maxAmount.getText());


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement instanceof CRTraitElement_PassiveAttributeMod)
        {
            CRTraitElement_PassiveAttributeMod actionElement = (CRTraitElement_PassiveAttributeMod) traitElement;

            GUILabeledTextInput attribute = new GUILabeledTextInput(gui, " Attribute Name: ", actionElement.attributeName.equals("") ? "generic.name" : actionElement.attributeName, FilterNotEmpty.INSTANCE);
            GUILabeledTextInput isGood = new GUILabeledTextInput(gui, " Is Good Attribute: ", "" + actionElement.isGood, FilterBoolean.INSTANCE);
            GUILabeledTextInput operation = new GUILabeledTextInput(gui, " Operation: ", "" + actionElement.operation, OPERATION_FILTER);
            GUILabeledTextInput minAmount = new GUILabeledTextInput(gui, " Min Amount: ", "" + actionElement.minAmount, FilterFloat.INSTANCE);
            GUILabeledTextInput maxAmount = new GUILabeledTextInput(gui, " Max Amount: ", "" + actionElement.maxAmount, FilterFloat.INSTANCE);
            gui.root.addAll(
                    new GUIElement(gui, 1, 0),
                    attribute,
                    new GUIElement(gui, 1, 0),
                    isGood,
                    new GUIElement(gui, 1, 0),
                    operation,
                    new GUIElement(gui, 1, 0),
                    minAmount,
                    new GUIElement(gui, 1, 0),
                    maxAmount
            );

            //Add main header actions
            done.addClickActions(() ->
            {
                //Validation
                if (!attribute.valid() || !isGood.valid() || !operation.valid() || !minAmount.valid() || !maxAmount.valid()) return;


                //Processing
                actionElement.attributeName = attribute.getText();
                actionElement.isGood = FilterBoolean.INSTANCE.parse(isGood.getText());
                actionElement.operation = FilterInt.INSTANCE.parse(operation.getText());
                actionElement.minAmount = FilterFloat.INSTANCE.parse(minAmount.getText());
                actionElement.maxAmount = FilterFloat.INSTANCE.parse(maxAmount.getText());


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
