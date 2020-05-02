package com.fantasticsource.tiamatitems.api;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.fantasticsource.fantasticlib.Compat;
import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.Slottings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;

public class TiamatItemsAPI
{
    public static final String DOMAIN = "tiamatrpg";

    public static ArrayList<ItemStack> getValidEquippedItems(EntityPlayer player)
    {
        ArrayList<ItemStack> result = GlobalInventory.getAllEquippedNonAWItems(player);

        //Vanilla slots
        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++)
        {
            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (!Slottings.slotValidForSlotting(getItemSlotting(stack), slot, player)) continue;

            result.add(stack);
        }

        //Baubles slots
        if (Compat.baubles)
        {
            IBaublesItemHandler inventory = BaublesApi.getBaublesHandler(player);
            for (int slot = 0; slot < inventory.getSlots(); slot++)
            {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (!Slottings.slotValidForSlotting(getItemSlotting(stack), slot + Slottings.BAUBLES_OFFSET, player)) continue;

                result.add(stack);
            }
        }

        //Tiamat slots
        if (Compat.tiamatrpg)
        {
            int slot = 0;
            for (ItemStack stack : GlobalInventory.getAllTiamatItems(player))
            {
                if (!Slottings.slotValidForSlotting(getItemSlotting(stack), slot + Slottings.TIAMAT_OFFSET, player)) continue;

                result.add(stack);
            }
        }

        return result;
    }


    public static void setItemSlotting(ItemStack stack, String slotting)
    {
        if (slotting == null || slotting.equals("") || slotting.equals("None"))
        {
            clearItemSlotting(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setString("slotting", slotting);
    }

    public static String getItemSlotting(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return "None";

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return "None";

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("slotting")) return "None";

        return compound.getString("slotting");
    }

    public static void clearItemSlotting(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("slotting")) return;

        compound.removeTag("slotting");
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
    }

    public static boolean stackFitsSlot(ItemStack stack, String slotting)
    {
        String itemSlotting = getItemSlotting(stack);
        if (itemSlotting.equals("Any") || itemSlotting.equals(slotting)) return true;

        switch (slotting)
        {
            case "Head":
            case "Chest":
            case "Legs":
            case "Feet":
            case "Tiamat Shoulders":
            case "Tiamat Cape":
            case "Armor":
                return itemSlotting.equals("Armor");

            case "Mainhand":
            case "Offhand":
            case "Hand":
                return itemSlotting.equals("Hand") || itemSlotting.equals("Tiamat 2H");

            case "Baubles Amulet":
            case "Baubles Ring":
            case "Baubles Belt":
            case "Baubles Head":
            case "Baubles Body":
            case "Baubles Charm":
                return itemSlotting.equals("Baubles Trinket");

            case "Tiamat Skill":
                return itemSlotting.equals("Tiamat Active Skill");
        }

        return false;
    }

    public static boolean isTwoHanded(ItemStack stack)
    {
        return stack.getItem() == Items.BOW || getItemSlotting(stack).equals("Tiamat 2H");
    }
}
