package com.fantasticsource.tiamatitems;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.component.CItemStack;
import com.fantasticsource.tiamatactions.action.CAction;
import com.fantasticsource.tiamatitems.itemeditor.ItemEditorGUI;
import com.fantasticsource.tiamatitems.settings.CRarity;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tiamatitems.settings.gui.SettingsGUI;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper(MODID);
    private static int discriminator = 0;
    protected static final HashMap<UUID, SaveSettingsPacketPart[]> SAVE_SETTINGS_PACKET_PARTS = new HashMap<>();

    public static void init()
    {
        WRAPPER.registerMessage(OpenItemEditorPacketHandler.class, OpenItemEditorPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(EditItemPacketHandler.class, EditItemPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(OpenSettingsPacketHandler.class, OpenSettingsPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(SaveSettingsPacketPartHandler.class, SaveSettingsPacketPart.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(ClientDataPacketHandler.class, ClientDataPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(RequestTooltipUpdatePacketHandler.class, RequestTooltipUpdatePacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(UpdatedTooltipPacketHandler.class, UpdatedTooltipPacket.class, discriminator++, Side.CLIENT);
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
            list = CAction.ALL_ACTIONS.keySet().toArray(new String[0]);
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
            CSettings.PENDING_LOCAL_SETTINGS.write(buf);
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


    public static class SaveSettingsPacketPart implements IMessage
    {
        UUID groupID;
        int partIndex, partCount;
        byte[] bytes;

        public SaveSettingsPacketPart()
        {
            //Required
        }

        public SaveSettingsPacketPart(UUID groupID, int partIndex, int partCount, byte[] bytes)
        {
            this.groupID = groupID;
            this.partIndex = partIndex;
            this.partCount = partCount;
            this.bytes = bytes;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeLong(groupID.getMostSignificantBits());
            buf.writeLong(groupID.getLeastSignificantBits());
            buf.writeInt(partIndex);
            buf.writeInt(partCount);
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            groupID = new UUID(buf.readLong(), buf.readLong());
            partIndex = buf.readInt();
            partCount = buf.readInt();
            bytes = new byte[buf.readInt()];
            buf.readBytes(bytes);
        }
    }

    public static class SaveSettingsPacketPartHandler implements IMessageHandler<SaveSettingsPacketPart, IMessage>
    {
        @Override
        public IMessage onMessage(SaveSettingsPacketPart packet, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() ->
            {
                if (!MCTools.isOP(player)) return;


                SaveSettingsPacketPart[] parts;
                if (packet.partIndex == 0)
                {
                    parts = new SaveSettingsPacketPart[packet.partCount];
                    SAVE_SETTINGS_PACKET_PARTS.put(packet.groupID, parts);
                    parts[0] = packet;
                }
                else
                {
                    parts = SAVE_SETTINGS_PACKET_PARTS.get(packet.groupID);
                    parts[packet.partIndex] = packet;
                }

                if (packet.partIndex == packet.partCount - 1)
                {
                    int totalSize = 0;
                    for (SaveSettingsPacketPart part : parts)
                    {
                        totalSize += part.bytes.length;
                    }

                    byte[] bytes = new byte[totalSize];
                    int i = 0;
                    for (SaveSettingsPacketPart part : parts)
                    {
                        System.arraycopy(part.bytes, 0, bytes, i++ * 32000, part.bytes.length);
                    }

                    PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
                    buffer.writeBytes(bytes);

                    //Set edited version 1 higher than current
                    CSettings.PENDING_LOCAL_SETTINGS = new CSettings().read(buffer);
                    CSettings.PENDING_LOCAL_SETTINGS.itemGenConfigVersion = CSettings.LOCAL_SETTINGS.itemGenConfigVersion + 1;

                    CSettings.updateVersionAndSave(ctx.getServerHandler().player);
                }
            });
            return null;
        }
    }


    public static class ClientDataPacket implements IMessage
    {
        public LinkedHashMap<String, CRarity> rarities;
        public long version;

        public ClientDataPacket()
        {
            //Required
        }

        public ClientDataPacket(long version)
        {
            this.version = version;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeLong(version);
            buf.writeInt(CSettings.LOCAL_SETTINGS.rarities.size());
            for (CRarity rarity : CSettings.LOCAL_SETTINGS.rarities.values()) rarity.write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            version = buf.readLong();
            rarities = new LinkedHashMap<>();
            CRarity rarity;
            for (int i = buf.readInt(); i > 0; i--)
            {
                rarity = new CRarity().read(buf);
                rarities.put(rarity.name, rarity);
            }
        }
    }

    public static class ClientDataPacketHandler implements IMessageHandler<ClientDataPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(ClientDataPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() ->
            {
                ClientData.serverItemGenConfigVersion = packet.version;
                RarityData.rarities = packet.rarities;
            });
            return null;
        }
    }


    public static class RequestTooltipUpdatePacket implements IMessage
    {
        public ItemStack stack;
        public int id;

        public RequestTooltipUpdatePacket()
        {
            //Required
        }

        public RequestTooltipUpdatePacket(ItemStack stack, int id)
        {
            this.stack = stack;
            this.id = id;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            new CItemStack().set(stack).write(buf);
            buf.writeInt(id);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            stack = new CItemStack().read(buf).value;
            id = buf.readInt();
        }
    }

    public static class RequestTooltipUpdatePacketHandler implements IMessageHandler<RequestTooltipUpdatePacket, IMessage>
    {
        @Override
        public IMessage onMessage(RequestTooltipUpdatePacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
            {
                //When the request packet is received, the itemstack in it is automatically validated on creation, so no further action is necessary
                Network.WRAPPER.sendTo(new UpdatedTooltipPacket(packet.stack, packet.id), ctx.getServerHandler().player);
            });
            return null;
        }
    }


    public static class UpdatedTooltipPacket implements IMessage
    {
        public ItemStack stack;
        public int id;

        public UpdatedTooltipPacket()
        {
            //Required
        }

        public UpdatedTooltipPacket(ItemStack stack, int id)
        {
            this.stack = stack;
            this.id = id;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            new CItemStack().set(stack).write(buf);
            buf.writeInt(id);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            stack = new CItemStack().read(buf).value;
            id = buf.readInt();
        }
    }

    public static class UpdatedTooltipPacketHandler implements IMessageHandler<UpdatedTooltipPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(UpdatedTooltipPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() ->
            {
                int id = packet.id;
                ClientItemStackFixer.UPDATED_TOOLTIP_STACKS.put(ClientItemStackFixer.REQUESTED_TOOLTIP_UPDATES_REVERSED.get(id), packet.stack);
            });
            return null;
        }
    }
}
