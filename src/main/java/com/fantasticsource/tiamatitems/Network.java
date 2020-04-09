package com.fantasticsource.tiamatitems;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.tiamatactions.action.CAction;
import com.fantasticsource.tiamatitems.assembly.ItemAssembly;
import com.fantasticsource.tiamatitems.itemeditor.ItemEditorGUI;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tiamatitems.settings.gui.SettingsGUI;
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
        WRAPPER.registerMessage(OpenSettingsPacketHandler.class, OpenSettingsPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(SaveSettingsPacketHandler.class, SaveSettingsPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(RequestItemStackUpdatePacketHandler.class, RequestItemStackUpdatePacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(ItemStackUpdatePacketHandler.class, ItemStackUpdatePacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(ItemgenVersionPacketHandler.class, ItemgenVersionPacket.class, discriminator++, Side.CLIENT);
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
            list = CAction.allActions.keySet().toArray(new String[0]);
            buf.writeInt(list.length);
            for (String s : list) ByteBufUtils.writeUTF8String(buf, s);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            int size = buf.readInt();
            list = new String[size];

            for (int i = 0; i < size; i++) list[i] = ByteBufUtils.readUTF8String(buf);
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


    public static class OpenSettingsPacket implements IMessage
    {
        public CSettings settings;

        public OpenSettingsPacket()
        {
            //Required
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            CSettings.SETTINGS.write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            settings = new CSettings().read(buf);
        }
    }

    public static class OpenSettingsPacketHandler implements IMessageHandler<OpenSettingsPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(OpenSettingsPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> SettingsGUI.show(packet));
            return null;
        }
    }


    public static class SaveSettingsPacket implements IMessage
    {
        public CSettings settings;

        public SaveSettingsPacket()
        {
            //Required
        }

        public SaveSettingsPacket(CSettings settings)
        {
            this.settings = settings;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            settings.write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            settings = new CSettings().read(buf);
        }
    }

    public static class SaveSettingsPacketHandler implements IMessageHandler<SaveSettingsPacket, IMessage>
    {
        @Override
        public IMessage onMessage(SaveSettingsPacket packet, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() ->
            {
                if (MCTools.isOP(player))
                {
                    //Swap current version into incoming settings, just in case multiple people edited settings at once
                    int itemGenConfigVersion = CSettings.SETTINGS.itemGenConfigVersion;
                    CSettings.SETTINGS = packet.settings;
                    CSettings.SETTINGS.itemGenConfigVersion = itemGenConfigVersion;

                    CSettings.updateVersionAndSave(ctx.getServerHandler().player);
                }
            });
            return null;
        }
    }


    public static class RequestItemStackUpdatePacket implements IMessage
    {
        public CItemStack stack = new CItemStack();
        public int id;

        public RequestItemStackUpdatePacket()
        {
            //Required
        }

        public RequestItemStackUpdatePacket(ItemStack stack, int id)
        {
            this.stack.set(stack);
            this.id = id;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            stack.write(buf);
            buf.writeInt(id);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            stack.read(buf);
            id = buf.readInt();
        }
    }

    public static class RequestItemStackUpdatePacketHandler implements IMessageHandler<RequestItemStackUpdatePacket, IMessage>
    {
        @Override
        public IMessage onMessage(RequestItemStackUpdatePacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
            {
                ItemStack stack = packet.stack.value;
                ItemAssembly.recalc(stack);
                WRAPPER.sendTo(new ItemStackUpdatePacket(stack, packet.id), ctx.getServerHandler().player);
            });
            return null;
        }
    }


    public static class ItemStackUpdatePacket implements IMessage
    {
        public CItemStack stack = new CItemStack();
        public int id;

        public ItemStackUpdatePacket()
        {
            //Required
        }

        public ItemStackUpdatePacket(ItemStack stack, int id)
        {
            this.stack.set(stack);
            this.id = id;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            stack.write(buf);
            buf.writeInt(id);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            stack.read(buf);
            id = buf.readInt();
        }
    }

    public static class ItemStackUpdatePacketHandler implements IMessageHandler<ItemStackUpdatePacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(ItemStackUpdatePacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() ->
            {
                ClientData.badStackToGoodStack.put(ClientData.idToBadStack.get(packet.id), packet.stack.value);
            });
            return null;
        }
    }


    public static class ItemgenVersionPacket implements IMessage
    {
        public long version;

        public ItemgenVersionPacket()
        {
            //Required
        }

        public ItemgenVersionPacket(long version)
        {
            this.version = version;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeLong(version);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            version = buf.readLong();
        }
    }

    public static class ItemgenVersionPacketHandler implements IMessageHandler<ItemgenVersionPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(ItemgenVersionPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> ClientData.serverItemGenConfigVersion = packet.version);
            return null;
        }
    }
}
