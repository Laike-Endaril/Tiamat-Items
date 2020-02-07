package com.fantasticsource.tiamatitems;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.tiamatactions.action.CAction;
import com.fantasticsource.tiamatitems.compat.Compat;
import com.fantasticsource.tiamatitems.globalsettings.GlobalSettingsGUI;
import com.fantasticsource.tiamatitems.itemeditor.ItemEditorGUI;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper(MODID);
    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(OpenItemEditorPacketHandler.class, OpenItemEditorPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(EditItemPacketHandler.class, EditItemPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(OpenGlobalSettingsPacketHandler.class, OpenGlobalSettingsPacket.class, discriminator++, Side.CLIENT);
    }


    public static class OpenItemEditorPacket implements IMessage
    {
        String[] list = null;

        public OpenItemEditorPacket()
        {
            //Required
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(Compat.tiamatactions);

            if (Compat.tiamatactions)
            {
                list = CAction.allActions.keySet().toArray(new String[0]);
                buf.writeInt(list.length);
                for (String s : list) ByteBufUtils.writeUTF8String(buf, s);
            }
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            if (buf.readBoolean())
            {
                int size = buf.readInt();
                list = new String[size];

                for (int i = 0; i < size; i++) list[i] = ByteBufUtils.readUTF8String(buf);
            }
        }
    }

    public static class OpenItemEditorPacketHandler implements IMessageHandler<OpenItemEditorPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(OpenItemEditorPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> ItemEditorGUI.show(packet.list));
            return null;
        }
    }


    public static class EditItemPacket implements IMessage
    {
        public CItemStack stack = new CItemStack();

        public EditItemPacket()
        {
            //Required
        }

        public EditItemPacket(ItemStack stack)
        {
            this.stack.set(stack);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            stack.write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            stack.read(buf);
        }
    }

    public static class EditItemPacketHandler implements IMessageHandler<EditItemPacket, IMessage>
    {
        @Override
        public IMessage onMessage(EditItemPacket packet, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() ->
            {
                if (player.isCreative() || MCTools.isOP(player))
                {
                    InventoryPlayer inv = ctx.getServerHandler().player.inventory;
                    inv.setInventorySlotContents(inv.currentItem, packet.stack.value);
                }
            });
            return null;
        }
    }


    public static class OpenGlobalSettingsPacket implements IMessage
    {
        public OpenGlobalSettingsPacket()
        {
            //Required
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
        }
    }

    public static class OpenGlobalSettingsPacketHandler implements IMessageHandler<OpenGlobalSettingsPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(OpenGlobalSettingsPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(GlobalSettingsGUI::show);
            return null;
        }
    }
}
