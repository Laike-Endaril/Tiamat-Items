package com.fantasticsource.tiamatitems;

import com.evilnotch.iitemrender.handlers.IItemRendererHandler;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientInit
{
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void modelRegistry(ModelRegistryEvent event)
    {
        IItemRendererHandler.register(TiamatItems.tiamatItem, new TiamatItemRenderer());
    }
}
