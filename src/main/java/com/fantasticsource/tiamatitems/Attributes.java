package com.fantasticsource.tiamatitems;

import com.fantasticsource.fantasticlib.Compat;
import com.fantasticsource.mctools.Slottings;
import com.fantasticsource.tiamatitems.nbt.AttributeTags;
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

public class Attributes
{
    //ALSO SEE ATTRIBUTETAGS CLASS

    public static UUID getTiamatModIDForSlot(int slot)
    {
        return new UUID(619375061579634L, slot);
    }

    public static Pair<String, AttributeModifier> getTiamatModifierForSlot(int slot, String modString)
    {
        String[] tokens = Tools.fixedSplit(modString, ";");
        if (tokens.length != 3) return null;

        return new Pair<>(tokens[0], new AttributeModifier(getTiamatModIDForSlot(slot), DOMAIN + slot, Double.parseDouble(tokens[1]), Integer.parseInt(tokens[2])));
    }


    @SubscribeEvent
    public static void handleTiamatTagAttributeMods(TickEvent.ServerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START) return;


        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

        server.profiler.startSection(MODID + " - generic slot attribute modifiers");

        for (EntityPlayerMP player : server.getPlayerList().getPlayers())
        {
            AbstractAttributeMap attributeMap = player.getAttributeMap();

            //Vanilla slots
            for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++)
            {
                for (IAttributeInstance attributeInstance : attributeMap.getAllAttributes().toArray(new IAttributeInstance[0]))
                {
                    attributeInstance.removeModifier(getTiamatModIDForSlot(slot));
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
                    for (String modString : AttributeTags.getItemAttributeMods(stack))
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

            //TODO Baubles slots
            if (Compat.baubles)
            {

            }

            //Tiamat slots
            if (Compat.tiamatrpg)
            {
                IInventory inventory = TiamatPlayerInventory.tiamatServerInventories.get(player.getPersistentID());
                for (int slot = 0; slot < inventory.getSizeInventory(); slot++)
                {
                    for (IAttributeInstance attributeInstance : attributeMap.getAllAttributes().toArray(new IAttributeInstance[0]))
                    {
                        attributeInstance.removeModifier(getTiamatModIDForSlot(slot + Slottings.TIAMAT_OFFSET));
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
                        for (String modString : AttributeTags.getItemAttributeMods(stack))
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