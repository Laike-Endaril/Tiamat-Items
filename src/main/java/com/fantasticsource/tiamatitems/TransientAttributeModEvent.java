package com.fantasticsource.tiamatitems;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.Slottings;
import com.fantasticsource.tiamatitems.compat.Compat;
import com.fantasticsource.tiamatitems.nbt.ActiveAttributeModTags;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.nbt.PassiveAttributeModTags;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class TransientAttributeModEvent extends PlayerEvent
{
    //ALSO SEE ATTRIBUTETAGS CLASS

    public static final String TRANSIENT_MOD_PREFIX = MODID + ":transient:";


    /**
     * @param player The player this event correlates to
     *               <p>
     *               This event is strictly meant for timing purposes, for the correct timing to apply/reapply transient attribute modifiers, and should therefore never be fired from anywhere except within this class
     *               Call this event's applyTransientModifier() method to apply an attribute modifier which will last from now until the next TransientAttributeModEvent (once per server tick)
     */
    public TransientAttributeModEvent(EntityPlayer player)
    {
        super(player);
    }


    public static void applyTransientModifier(EntityPlayerMP player, @Nullable String modName, String attributeName, int operation, double amount)
    {
        AbstractAttributeMap attributeMap = player.getAttributeMap();

        IAttributeInstance attributeInstance = attributeMap.getAttributeInstanceByName(attributeName);
        if (attributeInstance == null)
        {
            System.err.println("Attribute for transient modifier not found on player!");
            System.err.println("Player: " + player.getName() + ", Attribute name: " + attributeName);
            return;
        }

        attributeInstance.applyModifier(new AttributeModifier(TRANSIENT_MOD_PREFIX + (modName == null ? "" : modName), amount, operation));
    }


    @SubscribeEvent
    public static void handleTransientMods(TickEvent.ServerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START) return;


        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

        server.profiler.startSection(MODID + " - transient modifiers");


        for (EntityPlayerMP player : server.getPlayerList().getPlayers())
        {
            //Remove transient mods from last tick
            for (IAttributeInstance attributeInstance : player.getAttributeMap().getAllAttributes().toArray(new IAttributeInstance[0]))
            {
                for (AttributeModifier modifier : attributeInstance.getModifiers().toArray(new AttributeModifier[0]))
                {
                    if (modifier.getName().contains(TRANSIENT_MOD_PREFIX)) attributeInstance.removeModifier(modifier);
                }
            }


            //Vanilla slots
            for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++)
            {
                ItemStack stack = player.inventory.getStackInSlot(slot);
                handleTransientModsForSlot(player, slot, 0, "Vanilla", stack);
            }

            //Baubles slots
            if (Compat.baubles)
            {
                IBaublesItemHandler inventory = BaublesApi.getBaublesHandler(player);
                for (int slot = 0; slot < inventory.getSlots(); slot++)
                {
                    ItemStack stack = inventory.getStackInSlot(slot);
                    handleTransientModsForSlot(player, slot, Slottings.BAUBLES_OFFSET, "Baubles", stack);
                }
            }

            //Tiamat slots
            if (Compat.tiamatinventory)
            {
                int slot = 0;
                for (ItemStack stack : GlobalInventory.getAllTiamatItems(player))
                {
                    handleTransientModsForSlot(player, slot++, Slottings.TIAMAT_OFFSET, "Tiamat", stack);
                }
            }


            //Fire event
            MinecraftForge.EVENT_BUS.post(new TransientAttributeModEvent(player));
        }


        server.profiler.endSection();
    }


    private static void handleTransientModsForSlot(EntityPlayerMP player, int slot, int slotOffset, String slotType, ItemStack stack)
    {
        if (!Slottings.slotValidForSlotting(MiscTags.getItemSlotting(stack), slot + slotOffset, player)) return;


        //Passive mods
        for (String modString : PassiveAttributeModTags.getPassiveMods(stack))
        {
            String[] tokens = Tools.fixedSplit(modString, ";");
            if (tokens.length != 3)
            {
                System.err.println("Wrong number of arguments for attribute modifier string: " + modString);
                System.err.println("Player: " + player.getName() + ", Item name: " + stack.getDisplayName() + ", " + slotType + " slot: " + slot);
                continue;
            }

            double amount;
            try
            {
                amount = Double.parseDouble(tokens[1]);
            }
            catch (NumberFormatException e)
            {
                System.err.println("Amount (2nd argument) was not a double: " + modString);
                System.err.println("Player: " + player.getName() + ", Item name: " + stack.getDisplayName() + ", " + slotType + " slot: " + slot);
                continue;
            }

            int operation;
            try
            {
                operation = Integer.parseInt(tokens[2]);
            }
            catch (NumberFormatException e)
            {
                System.err.println("Operation (3nd argument) was not an integer: " + modString);
                System.err.println("Player: " + player.getName() + ", Item name: " + stack.getDisplayName() + ", " + slotType + " slot: " + slot);
                continue;
            }


            applyTransientModifier(player, stack.getDisplayName(), tokens[0], operation, amount);
        }


        //Active mods
        if (ActiveAttributeModTags.isActive(stack))
        {
            for (String modString : ActiveAttributeModTags.getActiveMods(stack))
            {
                String[] tokens = Tools.fixedSplit(modString, ";");
                if (tokens.length != 3)
                {
                    System.err.println("Wrong number of arguments for attribute modifier string: " + modString);
                    System.err.println("Player: " + player.getName() + ", Item name: " + stack.getDisplayName() + ", " + slotType + " slot: " + slot);
                    continue;
                }

                double amount;
                try
                {
                    amount = Double.parseDouble(tokens[1]);
                }
                catch (NumberFormatException e)
                {
                    System.err.println("Amount (2nd argument) was not a double: " + modString);
                    System.err.println("Player: " + player.getName() + ", Item name: " + stack.getDisplayName() + ", " + slotType + " slot: " + slot);
                    continue;
                }

                int operation;
                try
                {
                    operation = Integer.parseInt(tokens[2]);
                }
                catch (NumberFormatException e)
                {
                    System.err.println("Operation (3nd argument) was not an integer: " + modString);
                    System.err.println("Player: " + player.getName() + ", Item name: " + stack.getDisplayName() + ", " + slotType + " slot: " + slot);
                    continue;
                }


                applyTransientModifier(player, stack.getDisplayName(), tokens[0], operation, amount);
            }
        }
    }


    public void applyTransientModifier(@Nullable String description, String attributeName, int operation, double amount)
    {
        applyTransientModifier((EntityPlayerMP) getEntityPlayer(), description, attributeName, operation, amount);
    }
}
