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
    public static final FilterRangedInt OPERATION_FILTER = FilterRangedInt.get(0, 2), SLOT_COUNT_FILTER = FilterRangedInt.get(0, Integer.MAX_VALUE);

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
            CRTraitElement_ActiveAttributeMod attributeElement = (CRTraitElement_ActiveAttributeMod) traitElement;

            GUILabeledTextInput attribute = new GUILabeledTextInput(gui, " Attribute Name: ", attributeElement.attributeName.equals("") ? "generic.name" : attributeElement.attributeName, FilterNotEmpty.INSTANCE);
            GUILabeledTextInput isGood = new GUILabeledTextInput(gui, " Is Good Attribute: ", "" + attributeElement.isGood, FilterBoolean.INSTANCE);
            GUILabeledTextInput operation = new GUILabeledTextInput(gui, " Operation: ", "" + attributeElement.operation, OPERATION_FILTER);
            GUILabeledTextInput minAmount = new GUILabeledTextInput(gui, " Min Amount: ", "" + attributeElement.minAmount, FilterFloat.INSTANCE);
            GUILabeledTextInput maxAmount = new GUILabeledTextInput(gui, " Max Amount: ", "" + attributeElement.maxAmount, FilterFloat.INSTANCE);
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
                attributeElement.attributeName = attribute.getText();
                attributeElement.isGood = FilterBoolean.INSTANCE.parse(isGood.getText());
                attributeElement.operation = FilterInt.INSTANCE.parse(operation.getText());
                attributeElement.minAmount = FilterFloat.INSTANCE.parse(minAmount.getText());
                attributeElement.maxAmount = FilterFloat.INSTANCE.parse(maxAmount.getText());


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement instanceof CRTraitElement_PassiveAttributeMod)
        {
            CRTraitElement_PassiveAttributeMod attributeElement = (CRTraitElement_PassiveAttributeMod) traitElement;

            GUILabeledTextInput attribute = new GUILabeledTextInput(gui, " Attribute Name: ", attributeElement.attributeName.equals("") ? "generic.name" : attributeElement.attributeName, FilterNotEmpty.INSTANCE);
            GUILabeledTextInput isGood = new GUILabeledTextInput(gui, " Is Good Attribute: ", "" + attributeElement.isGood, FilterBoolean.INSTANCE);
            GUILabeledTextInput operation = new GUILabeledTextInput(gui, " Operation: ", "" + attributeElement.operation, OPERATION_FILTER);
            GUILabeledTextInput minAmount = new GUILabeledTextInput(gui, " Min Amount: ", "" + attributeElement.minAmount, FilterFloat.INSTANCE);
            GUILabeledTextInput maxAmount = new GUILabeledTextInput(gui, " Max Amount: ", "" + attributeElement.maxAmount, FilterFloat.INSTANCE);
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
                attributeElement.attributeName = attribute.getText();
                attributeElement.isGood = FilterBoolean.INSTANCE.parse(isGood.getText());
                attributeElement.operation = FilterInt.INSTANCE.parse(operation.getText());
                attributeElement.minAmount = FilterFloat.INSTANCE.parse(minAmount.getText());
                attributeElement.maxAmount = FilterFloat.INSTANCE.parse(maxAmount.getText());


                //Close GUI
                gui.close();
            });
        }
        else if (traitElement instanceof CRTraitElement_PartSlot)
        {
            CRTraitElement_PartSlot partSlotElement = (CRTraitElement_PartSlot) traitElement;

            GUILabeledTextInput partSlotType = new GUILabeledTextInput(gui, " Part Slot Type: ", partSlotElement.partSlotType.equals("") ? "PartSlotType" : partSlotElement.partSlotType, FilterNotEmpty.INSTANCE);
            GUILabeledTextInput required = new GUILabeledTextInput(gui, " Required: ", "" + partSlotElement.required, FilterBoolean.INSTANCE);
            GUILabeledTextInput minCount = new GUILabeledTextInput(gui, " Min Count: ", "" + partSlotElement.minCount, SLOT_COUNT_FILTER);
            GUILabeledTextInput maxCount = new GUILabeledTextInput(gui, " Max Count: ", "" + partSlotElement.maxCount, SLOT_COUNT_FILTER);
            gui.root.addAll(
                    new GUIElement(gui, 1, 0),
                    partSlotType,
                    new GUIElement(gui, 1, 0),
                    required,
                    new GUIElement(gui, 1, 0),
                    minCount,
                    new GUIElement(gui, 1, 0),
                    maxCount
            );

            //Add main header actions
            done.addClickActions(() ->
            {
                //Validation
                if (!partSlotType.valid()) return;


                //Processing
                partSlotElement.partSlotType = partSlotType.getText();
                partSlotElement.required = FilterBoolean.INSTANCE.parse(required.getText());
                partSlotElement.minCount = SLOT_COUNT_FILTER.parse(minCount.getText());
                partSlotElement.maxCount = SLOT_COUNT_FILTER.parse(maxCount.getText());


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
