package com.fantasticsource.tiamatitems;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.fantasticsource.mctools.Slottings;
import com.fantasticsource.tiamatitems.compat.Compat;
import com.fantasticsource.tiamatitems.nbt.PassiveAttributeModTags;
import com.fantasticsource.tiamatitems.nbt.SlottingTags;
import com.fantasticsource.tiamatrpg.inventory.TiamatPlayerInventory;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.UUID;

import static com.fantasticsource.tiamatitems.TiamatItems.DOMAIN;
import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class AttributeHandler
{
    //ALSO SEE ATTRIBUTETAGS CLASS

    public static UUID getTiamatModIDForSlot(int slot, int operation)
    {
        return new UUID(619375061579634L, (((long) slot) << 32) + operation);
    }

    public static Pair<String, AttributeModifier> getTiamatModifierForSlot(int slot, String modString)
    {
        String[] tokens = Tools.fixedSplit(modString, ";");
        if (tokens.length != 3) return null;

        int operation = Integer.parseInt(tokens[2]);
        return new Pair<>(tokens[0], new AttributeModifier(getTiamatModIDForSlot(slot, operation), DOMAIN + slot + ":" + operation, Double.parseDouble(tokens[1]), operation));
    }


    @SubscribeEvent
    public static void handlePassiveAttributeMods(TickEvent.ServerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START) return;


        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

        server.profiler.startSection(MODID + " - slot attribute modifiers");

        for (EntityPlayerMP player : server.getPlayerList().getPlayers())
        {
            AbstractAttributeMap attributeMap = player.getAttributeMap();

            //Vanilla slots
            for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++)
            {
                for (IAttributeInstance attributeInstance : attributeMap.getAllAttributes().toArray(new IAttributeInstance[0]))
                {
                    attributeInstance.removeModifier(getTiamatModIDForSlot(slot, 0));
                    attributeInstance.removeModifier(getTiamatModIDForSlot(slot, 1));
                    attributeInstance.removeModifier(getTiamatModIDForSlot(slot, 2));
                }

                ItemStack stack = player.inventory.getStackInSlot(slot);
                boolean valid = false;
                for (String slotting : SlottingTags.getItemSlottings(stack))
                {
                    if (Slottings.slotValidForSlotting(slotting, slot, player))
                    {
                        valid = true;
                        break;
                    }
                }
                if (valid)
                {
                    for (String modString : PassiveAttributeModTags.getPassiveMods(stack))
                    {
                        Pair<String, AttributeModifier> pair = getTiamatModifierForSlot(slot, modString);
                        if (pair == null)
                        {
                            System.err.println("WARNING: Quite possibly corrupted item; bad attribute modifier string: " + modString);
                            System.err.println("Player: " + player.getName() + ", Item name: " + stack.getDisplayName() + ", Vanilla slot: " + slot);
                            continue;
                        }

                        IAttributeInstance attributeInstance = attributeMap.getAttributeInstanceByName(pair.getKey());
                        if (attributeInstance != null) attributeInstance.applyModifier(pair.getValue());
                    }
                }
            }

            //Baubles slots
            if (Compat.baubles)
            {
                IBaublesItemHandler inventory = BaublesApi.getBaublesHandler(player);
                for (int slot = 0; slot < inventory.getSlots(); slot++)
                {
                    for (IAttributeInstance attributeInstance : attributeMap.getAllAttributes().toArray(new IAttributeInstance[0]))
                    {
                        attributeInstance.removeModifier(getTiamatModIDForSlot(slot + Slottings.BAUBLES_OFFSET, 0));
                        attributeInstance.removeModifier(getTiamatModIDForSlot(slot + Slottings.BAUBLES_OFFSET, 1));
                        attributeInstance.removeModifier(getTiamatModIDForSlot(slot + Slottings.BAUBLES_OFFSET, 2));
                    }

                    ItemStack stack = inventory.getStackInSlot(slot);
                    boolean valid = false;
                    for (String slotting : SlottingTags.getItemSlottings(stack))
                    {
                        if (Slottings.slotValidForSlotting(slotting, slot + Slottings.BAUBLES_OFFSET, player))
                        {
                            valid = true;
                            break;
                        }
                    }
                    if (valid)
                    {
                        for (String modString : PassiveAttributeModTags.getPassiveMods(stack))
                        {
                            Pair<String, AttributeModifier> pair = getTiamatModifierForSlot(slot + Slottings.BAUBLES_OFFSET, modString);
                            if (pair == null)
                            {
                                System.err.println("WARNING: Quite possibly corrupted item; bad attribute modifier string: " + modString);
                                System.err.println("Player: " + player.getName() + ", Item name: " + stack.getDisplayName() + ", Baubles slot: " + slot);
                                continue;
                            }

                            IAttributeInstance attributeInstance = attributeMap.getAttributeInstanceByName(pair.getKey());
                            if (attributeInstance != null) attributeInstance.applyModifier(pair.getValue());
                        }
                    }
                }
            }

            //Tiamat slots
            if (Compat.tiamatrpg)
            {
                IInventory inventory = TiamatPlayerInventory.tiamatServerInventories.get(player.getPersistentID());
                for (int slot = 0; slot < inventory.getSizeInventory(); slot++)
                {
                    for (IAttributeInstance attributeInstance : attributeMap.getAllAttributes().toArray(new IAttributeInstance[0]))
                    {
                        attributeInstance.removeModifier(getTiamatModIDForSlot(slot + Slottings.TIAMAT_OFFSET, 0));
                        attributeInstance.removeModifier(getTiamatModIDForSlot(slot + Slottings.TIAMAT_OFFSET, 1));
                        attributeInstance.removeModifier(getTiamatModIDForSlot(slot + Slottings.TIAMAT_OFFSET, 2));
                    }

                    ItemStack stack = inventory.getStackInSlot(slot);
                    boolean valid = false;
                    for (String slotting : SlottingTags.getItemSlottings(stack))
                    {
                        if (Slottings.slotValidForSlotting(slotting, slot + Slottings.TIAMAT_OFFSET, player))
                        {
                            valid = true;
                            break;
                        }
                    }
                    if (valid)
                    {
                        for (String modString : PassiveAttributeModTags.getPassiveMods(stack))
                        {
                            Pair<String, AttributeModifier> pair = getTiamatModifierForSlot(slot + Slottings.TIAMAT_OFFSET, modString);
                            if (pair == null)
                            {
                                System.err.println("WARNING: Quite possibly corrupted item; bad attribute modifier string: " + modString);
                                System.err.println("Player: " + player.getName() + ", Item name: " + stack.getDisplayName() + ", Tiamat slot: " + slot);
                                continue;
                            }

                            IAttributeInstance attributeInstance = attributeMap.getAttributeInstanceByName(pair.getKey());
                            if (attributeInstance != null) attributeInstance.applyModifier(pair.getValue());
                        }
                    }
                }
            }
        }

        server.profiler.endSection();
    }
}
