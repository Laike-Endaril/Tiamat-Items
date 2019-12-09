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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper(MODID);
    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(EditItemPacketHandler.class, EditItemPacket.class, discriminator++, Side.SERVER);
    }


    public static class EditItemPacket implements IMessage
    {
        String name;
        String lore;
        String[] layers = null;
        LinkedHashMap<String, ArrayList<String>> categoryTags;

        public EditItemPacket()
        {
            //Required
        }

        public EditItemPacket(String name, String lore, LinkedHashMap<String, ArrayList<String>> categoryTags)
        {
            this.name = name;
            this.lore = lore;
            this.categoryTags = categoryTags;
        }

        public EditItemPacket(String name, String lore, LinkedHashMap<String, ArrayList<String>> categoryTags, String[] layers)
        {
            this.name = name;
            this.lore = lore;
            this.categoryTags = categoryTags;

            this.layers = layers;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, name);
            ByteBufUtils.writeUTF8String(buf, lore);

            buf.writeInt(categoryTags.size());
            for (Map.Entry<String, ArrayList<String>> entry : categoryTags.entrySet())
            {
                ByteBufUtils.writeUTF8String(buf, entry.getKey());
                buf.writeInt(entry.getValue().size());
                for (String tag : entry.getValue()) ByteBufUtils.writeUTF8String(buf, tag);
            }

            buf.writeBoolean(layers != null);
            if (layers != null)
            {
                buf.writeInt(layers.length);
                for (String layer : layers) ByteBufUtils.writeUTF8String(buf, layer);
            }
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            name = ByteBufUtils.readUTF8String(buf);
            lore = ByteBufUtils.readUTF8String(buf);

            categoryTags = new LinkedHashMap<>();
            for (int i = buf.readInt(); i > 0; i--)
            {
                ArrayList<String> category = new ArrayList<>();
                categoryTags.put(ByteBufUtils.readUTF8String(buf), category);
                for (int i2 = buf.readInt(); i2 > 0; i2--) category.add(ByteBufUtils.readUTF8String(buf));
            }

            if (buf.readBoolean())
            {
                layers = new String[buf.readInt()];
                for (int i = 0; i < layers.length; i++) layers[i] = ByteBufUtils.readUTF8String(buf);
            }
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
                    ItemStack stack = player.getHeldItemMainhand();

                    stack.setStackDisplayName(packet.name);

                    MCTools.setLore(stack, packet.lore);

                    TiamatItems.clearItemCategories(stack);
                    for (Map.Entry<String, ArrayList<String>> entry : packet.categoryTags.entrySet())
                    {
                        for (String tag : entry.getValue()) TiamatItems.addItemCategoryTag(stack, entry.getKey(), tag);
                    }

                    if (packet.layers != null && stack.getItem() == TiamatItems.tiamatItem)
                    {
                        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
                        NBTTagCompound compound = stack.getTagCompound();

                        if (!compound.hasKey(MODID)) compound.setTag(MODID, new NBTTagCompound());
                        compound = compound.getCompoundTag(MODID);

                        compound.setTag("layers", new NBTTagList());
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
