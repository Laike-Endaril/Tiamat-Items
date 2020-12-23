package com.fantasticsource.tiamatitems;

import com.fantasticsource.mctools.items.ItemMatcher;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.List;

public class TooltipFixer
{
    //Itemstacks hash per-instance (no hashed equality).  Equality needs to be checked manually.
    protected static final HashMap<ItemStack, Integer> REQUESTED_TOOLTIP_UPDATES = new HashMap<>();
    protected static final HashMap<Integer, ItemStack> REQUESTED_TOOLTIP_UPDATES_REVERSED = new HashMap<>();
    protected static final HashMap<ItemStack, ItemStack> UPDATED_TOOLTIPS = new HashMap<>();
    protected static int nextID = 0;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void tooltip(ItemTooltipEvent event)
    {
        if (ClientData.serverItemGenConfigVersion == -1) return;

        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || !stack.hasTagCompound()) return;

        String itemTypeName = MiscTags.getItemTypeName(stack);
        if (itemTypeName.equals("")) return;

        if (MiscTags.getItemGenVersion(stack) == ClientData.serverItemGenConfigVersion) return;


        List<String> tooltip = event.getToolTip();
        tooltip.clear();
        ItemStack newTooltipStack = UPDATED_TOOLTIPS.get(stack);
        if (newTooltipStack == null)
        {
            for (ItemStack other : UPDATED_TOOLTIPS.keySet())
            {
                if (ItemMatcher.stacksMatch(stack, other))
                {
                    newTooltipStack = UPDATED_TOOLTIPS.get(other);
                    UPDATED_TOOLTIPS.put(stack, newTooltipStack);
                    break;
                }
            }
        }
        if (newTooltipStack != null)
        {
            if (newTooltipStack.isEmpty())
            {
                tooltip.add(TextFormatting.RED + "OUTDATED TIAMAT ITEM WARNING!!!");
                tooltip.add(TextFormatting.RED + "This item will not actually exist if you obtain it!");
                tooltip.add(TextFormatting.RED + "This should only happen if another mod uses client-side-only items or very strange itemstack generation!");
            }
            else tooltip.addAll(newTooltipStack.getTooltip(event.getEntityPlayer(), event.getFlags()));
        }
        else if (!REQUESTED_TOOLTIP_UPDATES.containsKey(stack))
        {
            tooltip.add(TextFormatting.DARK_AQUA + "Loading...");
            REQUESTED_TOOLTIP_UPDATES.put(stack, nextID);
            REQUESTED_TOOLTIP_UPDATES_REVERSED.put(nextID, stack);
            Network.WRAPPER.sendToServer(new Network.RequestTooltipUpdatePacket(stack, nextID++));
        }
    }

    @SubscribeEvent
    public static void clientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        REQUESTED_TOOLTIP_UPDATES.clear();
        REQUESTED_TOOLTIP_UPDATES_REVERSED.clear();
        UPDATED_TOOLTIPS.clear();
        nextID = 0;
    }
}
