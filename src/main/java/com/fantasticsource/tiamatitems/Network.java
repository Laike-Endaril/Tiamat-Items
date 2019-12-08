package com.fantasticsource.tiamatitems;

import com.fantasticsource.mctools.MCTools;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper(MODID);
    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(SaveItemTexturePacketHandler.class, SaveItemTexturePacket.class, discriminator++, Side.SERVER);
    }


    public static class SaveItemTexturePacket implements IMessage
    {
        String[] layers;

        public SaveItemTexturePacket()
        {
            //Required
        }

        public SaveItemTexturePacket(String[] layers)
        {
            this.layers = layers;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(layers.length);
            for (String layer : layers) ByteBufUtils.writeUTF8String(buf, layer);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            layers = new String[buf.readInt()];
            for (int i = 0; i < layers.length; i++) layers[i] = ByteBufUtils.readUTF8String(buf);
        }
    }

    public static class SaveItemTexturePacketHandler implements IMessageHandler<SaveItemTexturePacket, IMessage>
    {
        @Override
        public IMessage onMessage(SaveItemTexturePacket packet, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            server.addScheduledTask(() ->
            {
                if (player.isCreative() || MCTools.isOP(player))
                {
                    ItemStack stack = player.getHeldItemMainhand();
                    if (stack.getItem() == TiamatItems.tiamatItem)
                    {
                        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
                        NBTTagCompound compound = stack.getTagCompound();

                        if (!compound.hasKey(MODID)) compound.setTag(MODID, new NBTTagCompound());
                        compound = compound.getCompoundTag(MODID);

                        if (!compound.hasKey("layers")) compound.setTag("layers", new NBTTagList());
                        NBTTagList layerList = compound.getTagList("layers", Constants.NBT.TAG_STRING);

                        for (String layer : packet.layers)
                        {
                            layerList.appendTag(new NBTTagString(layer));
                        }
                    }
                }
            });
            return null;
        }
    }
}
