package com.fantasticsource.tiamatitems.assembly;

import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.textured.GUIItemStack;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.mctools.gui.screen.ItemstackSelectionGUI;
import com.fantasticsource.tiamatitems.Network;
import com.fantasticsource.tiamatitems.api.IPartSlot;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class ModifyItemGUI extends GUIScreen
{
    protected GUIItemStack assembly;
    protected GUIGradientBorder assemblyBorder;
    protected GUIAutocroppedView partSlotsView;

    public ModifyItemGUI(ItemStack assemblyStack)
    {
        drawStack = false;
        root.setSubElementAutoplaceMethod(GUIElement.AP_CENTERED_H_TOP_TO_BOTTOM);


        //Background
        root.add(new GUIDarkenedBackground(this));


        //Navbar
        GUINavbar navbar = new GUINavbar(this);
        root.add(navbar);


        //Main slot
        assembly = new GUIItemStack(this, 16, 16, assemblyStack);
        assemblyBorder = new GUIGradientBorder(this, 1, 1, 0.1, getIdleColor(Color.WHITE), Color.BLANK, getHoverColor(Color.WHITE), Color.BLANK, Color.WHITE, Color.BLANK);
        assembly.add(assemblyBorder);
        root.add(assembly);


        //Spacer
        GUITextSpacer spacer = new GUITextSpacer(this);
        root.add(spacer);


        //View for part slots
        partSlotsView = new GUIAutocroppedView(this);
        root.addAll(partSlotsView);


        updateAssemblyBorderColor();
        updatePartSlots();
    }

    public ItemStack getAssemblyStack()
    {
        return assembly.getItemStack();
    }

    public void setAssemblyStack(ItemStack assemblyStack)
    {
        assembly.setItemStack(assemblyStack);
        updateAssemblyBorderColor();
        updatePartSlots();
    }

    protected void updateAssemblyBorderColor()
    {
        //Change assembly border color
        Color color;
        switch (AssemblyTags.getState(assembly.getItemStack()))
        {
            case AssemblyTags.STATE_FULL:
                color = Color.GREEN;
                break;

            case AssemblyTags.STATE_USABLE:
                color = Color.YELLOW;
                break;

            default:
                color = Color.RED;
                break;
        }
        assemblyBorder.setColors(color, Color.BLANK, color, Color.BLANK, color, Color.BLANK);
    }

    protected void updatePartSlots()
    {
        //Remove old part slots
        partSlotsView.clear();

        //Add new part slots
        int i = 0;
        ArrayList<IPartSlot> partSlots = AssemblyTags.getPartSlots(assembly.getItemStack());
        for (IPartSlot partSlot : partSlots)
        {
            int i2 = i++;
            GUIItemStack element = new GUIItemStack(this, 16, 16, partSlot.getPart());
            GUIGradientBorder elementBorder = new GUIGradientBorder(this, 1, 1, 0.1, getIdleColor(Color.WHITE), Color.BLANK, getHoverColor(Color.WHITE), Color.BLANK, Color.WHITE, Color.BLANK);

            //Change part border color
            if (partSlot.partIsValidForSlot(element.getItemStack()))
            {
                elementBorder.setColors(getIdleColor(Color.GREEN), Color.BLANK, getHoverColor(Color.GREEN), Color.BLANK, Color.GREEN, Color.BLANK);
            }
            else if (!partSlot.getRequired())
            {
                elementBorder.setColors(getIdleColor(Color.WHITE), Color.BLANK, getHoverColor(Color.WHITE), Color.BLANK, Color.WHITE, Color.BLANK);
            }
            else
            {
                elementBorder.setColors(getIdleColor(Color.RED), Color.BLANK, getHoverColor(Color.RED), Color.BLANK, Color.RED, Color.BLANK);
            }

            element.add(elementBorder);
            partSlotsView.add(element.addClickActions(() ->
            {
                ItemStack prevPart = element.getItemStack();
                ArrayList<ItemStack> invParts = GlobalInventory.getAllNonSkinItems(Minecraft.getMinecraft().player);
                ArrayList<UUID> blacklist = new ArrayList<>();
                blacklist.add(MiscTags.getID(assembly.getItemStack()));
                for (ScreenEntry entry : GUIScreen.SCREEN_STACK)
                {
                    if (entry.screen instanceof ModifyItemGUI) blacklist.add(MiscTags.getID(((ModifyItemGUI) entry.screen).getAssemblyStack()));
                }
                invParts.removeIf(stack -> !partSlot.partIsValidForSlot(stack) || blacklist.contains(MiscTags.getID(stack)));
                invParts.add(0, prevPart);
                if (!prevPart.isEmpty()) invParts.add(1, ItemStack.EMPTY);
                ItemstackSelectionGUI gui2 = new ItemstackSelectionGUI(element, "Select Part for " + partSlot.getSlotType() + " Slot", invParts.toArray(new ItemStack[0]));
                gui2.closeIfStackedOn = true;
                gui2.addOnClosedActions(() ->
                {
                    if (element.getItemStack() != prevPart)
                    {
                        //Change part border color
                        if (partSlot.partIsValidForSlot(element.getItemStack()))
                        {
                            elementBorder.setColors(getIdleColor(Color.GREEN), Color.BLANK, getHoverColor(Color.GREEN), Color.BLANK, Color.GREEN, Color.BLANK);
                        }
                        else if (!partSlot.getRequired())
                        {
                            elementBorder.setColors(getIdleColor(Color.WHITE), Color.BLANK, getHoverColor(Color.WHITE), Color.BLANK, Color.WHITE, Color.BLANK);
                        }
                        else
                        {
                            elementBorder.setColors(getIdleColor(Color.RED), Color.BLANK, getHoverColor(Color.RED), Color.BLANK, Color.RED, Color.BLANK);
                        }

                        //Send server request to change server-side
                        Network.WRAPPER.sendToServer(new Network.RequestAssemblyChangePacket(assembly.getItemStack(), i2, element.getItemStack()));

                        //Change client-side immediately AFTER the request, so assembly matches correctly on server-side during request
                        MCTools.give(Minecraft.getMinecraft().player, MCTools.cloneItemStack(prevPart));
                        partSlot.setPart(element.getItemStack());
                        AssemblyTags.setPartSlots(assembly.getItemStack(), partSlots);
                        MCTools.destroyItemStack(prevPart);


                        updateAssemblyBorderColor();
                    }
                });
            }));
        }


        root.recalcAndRepositionSubElements(0);
    }

    @Override
    public String title()
    {
        return "Assembler";
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
