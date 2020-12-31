package com.fantasticsource.tiamatitems;

import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.tiamatitems.nbt.ActiveAttributeModTags;
import com.fantasticsource.tiamatitems.nbt.PassiveAttributeModTags;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class TransientAttributeModEvent extends LivingEvent
{
    //ALSO SEE ATTRIBUTETAGS CLASS

    public static final String TRANSIENT_MOD_PREFIX = MODID + ":transient:";


    /**
     * @param livingBase The EntityLivingBase this event correlates to
     *                   <p>
     *                   This event has strict timing, and should therefore never be fired from anywhere except within this class
     *                   Call this event's applyTransientModifier() method to apply an attribute modifier which will last from now until the next TransientAttributeModEvent (once per server tick)
     */
    public TransientAttributeModEvent(EntityLivingBase livingBase)
    {
        super(livingBase);
    }


    public void applyTransientModifier(@Nullable String description, String attributeName, int operation, double amount)
    {
        applyTransientModifier(getEntityLiving(), description, attributeName, operation, amount);
    }

    public static void applyTransientModifier(EntityLivingBase livingBase, @Nullable String modName, String attributeName, int operation, double amount)
    {
        AbstractAttributeMap attributeMap = livingBase.getAttributeMap();

        IAttributeInstance attributeInstance = attributeMap.getAttributeInstanceByName(attributeName);
        if (attributeInstance == null)
        {
            System.err.println("Attribute for transient modifier not found!");
            System.err.println("Entity: " + livingBase.getName() + ", Attribute name: " + attributeName);
            return;
        }

        attributeInstance.applyModifier(new AttributeModifier(TRANSIENT_MOD_PREFIX + (modName == null ? "" : modName), amount, operation));
    }


    @SubscribeEvent
    public static void handleTransientMods(InventoryChangedEvent event)
    {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityLivingBase)) return;

        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        server.profiler.startSection(MODID + " - transient modifiers");


        EntityLivingBase livingBase = (EntityLivingBase) entity;


        //Remove transient mods from last tick
        for (IAttributeInstance attributeInstance : livingBase.getAttributeMap().getAllAttributes().toArray(new IAttributeInstance[0]))
        {
            for (AttributeModifier modifier : attributeInstance.getModifiers().toArray(new AttributeModifier[0]))
            {
                if (modifier.getName().contains(TRANSIENT_MOD_PREFIX)) attributeInstance.removeModifier(modifier);
            }
        }


        for (ItemStack stack : GlobalInventory.getValidEquippedItems(entity))
        {
            handleTransientModsForSlot(livingBase, stack);
        }


        //Fire event
        MinecraftForge.EVENT_BUS.post(new TransientAttributeModEvent(livingBase));


        server.profiler.endSection();
    }


    private static void handleTransientModsForSlot(EntityLivingBase livingBase, ItemStack stack)
    {
        //Passive mods
        for (String modString : PassiveAttributeModTags.getPassiveMods(stack))
        {
            String[] tokens = Tools.fixedSplit(modString, ";");
            if (tokens.length != 3)
            {
                System.err.println("Wrong number of arguments for attribute modifier string: " + modString);
                System.err.println("Entity: " + livingBase.getName() + ", Item name: " + stack.getDisplayName());
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
                System.err.println("Entity: " + livingBase.getName() + ", Item name: " + stack.getDisplayName());
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
                System.err.println("Entity: " + livingBase.getName() + ", Item name: " + stack.getDisplayName());
                continue;
            }


            applyTransientModifier(livingBase, stack.getDisplayName(), tokens[0], operation, amount);
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
                    System.err.println("Entity: " + livingBase.getName() + ", Item name: " + stack.getDisplayName());
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
                    System.err.println("Entity: " + livingBase.getName() + ", Item name: " + stack.getDisplayName());
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
                    System.err.println("Entity: " + livingBase.getName() + ", Item name: " + stack.getDisplayName());
                    continue;
                }


                applyTransientModifier(livingBase, stack.getDisplayName(), tokens[0], operation, amount);
            }
        }
    }
}
