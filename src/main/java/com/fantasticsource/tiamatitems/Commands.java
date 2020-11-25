package com.fantasticsource.tiamatitems;

import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.PlayerData;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.settings.CRarity;
import com.fantasticsource.tiamatitems.settings.CSettings;
import com.fantasticsource.tiamatitems.trait.CItemType;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nullable;
import java.util.*;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;
import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.WHITE;

public class Commands extends CommandBase
{
    private static LinkedHashMap<String, Integer> subcommands = new LinkedHashMap<>();

    static
    {
        subcommands.put("generate", 2);
        subcommands.put("setvalue", 2);
    }


    @Override
    public String getName()
    {
        return MODID;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        if (sender.canUseCommand(2, getName()))
        {
            return AQUA + "/" + getName() + " generate <itemType> <level> <rarity> [playername]" + WHITE + " - " + I18n.translateToLocalFormatted(MODID + ".cmd.generate.comment")
                    + "\n" + AQUA + "/" + getName() + " setvalue <value>" + WHITE + " - " + I18n.translateToLocalFormatted(MODID + ".cmd.setvalue.comment");
        }

        return I18n.translateToLocalFormatted("commands.generic.permission");
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        if (args.length == 0) sender.getCommandSenderEntity().sendMessage(new TextComponentString(getUsage(sender)));
        else subCommand(sender, args);
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        ArrayList<String> result = new ArrayList<>();

        String partial = args[args.length - 1];
        if (args.length == 1)
        {
            for (Map.Entry<String, Integer> entry : subcommands.entrySet())
            {
                if (sender.canUseCommand(entry.getValue(), getName())) result.add(entry.getKey());
            }
        }
        else if (args.length == 2)
        {
            switch (args[0])
            {
                case "generate":
                    for (String itemType : CSettings.SETTINGS.itemTypes.keySet()) result.add(itemType.replaceAll(" ", "_"));
                    break;
            }
        }
        else if (args.length == 3)
        {
            switch (args[0])
            {
                case "generate":
                    ArrayList<String> ints = new ArrayList<>();
                    for (int i = 0; i < 10; i++) ints.add("" + i);
                    result.addAll(ints);
                    break;
            }
        }
        else if (args.length == 4)
        {
            switch (args[0])
            {
                case "generate":
                    result.addAll(CSettings.SETTINGS.rarities.keySet());
                    break;
            }
        }
        else if (args.length == 5)
        {
            switch (args[0])
            {
                case "generate":
                    result.addAll(Arrays.asList(server.getPlayerList().getOnlinePlayerNames()));
                    break;
            }
        }

        if (partial.length() != 0) result.removeIf(k -> partial.length() > k.length() || !k.substring(0, partial.length()).equalsIgnoreCase(partial));
        return result;
    }

    private void subCommand(ICommandSender sender, String[] args)
    {
        String cmd = args[0];

        if (!sender.canUseCommand(subcommands.get(cmd), getName()))
        {
            notifyCommandListener(sender, this, "commands.generic.permission");
            return;
        }

        switch (cmd)
        {
            case "generate":
                if (args.length < 4 || args.length > 5)
                {
                    notifyCommandListener(sender, this, getUsage(sender));
                    return;
                }
                if (args.length == 4 && !(sender instanceof EntityPlayerMP))
                {
                    notifyCommandListener(sender, this, MODID + ".error.notPlayer");
                    return;
                }

                CItemType gen = CSettings.SETTINGS.itemTypes.get(args[1].replaceAll("_", " "));
                CRarity rarity = CSettings.SETTINGS.rarities.get(args[3]);
                EntityPlayerMP target = args.length == 4 ? (EntityPlayerMP) sender : (EntityPlayerMP) PlayerData.getEntity(args[4]);
                if (gen == null || rarity == null || target == null)
                {
                    notifyCommandListener(sender, this, getUsage(sender));
                    return;
                }
                int level;
                try
                {
                    level = Integer.parseInt(args[2]);
                }
                catch (NumberFormatException e)
                {
                    notifyCommandListener(sender, this, getUsage(sender));
                    return;
                }

                MCTools.give(target, gen.generateItem(level, rarity));

                break;


            case "setvalue":
                if (!(sender instanceof EntityPlayerMP))
                {
                    notifyCommandListener(sender, this, MODID + ".error.notPlayer");
                    return;
                }
                if (args.length != 2)
                {
                    notifyCommandListener(sender, this, getUsage(sender));
                    return;
                }

                int value;
                try
                {
                    value = Integer.parseInt(args[1]);
                }
                catch (NumberFormatException e)
                {
                    notifyCommandListener(sender, this, getUsage(sender));
                    return;
                }

                ItemStack stack = GlobalInventory.getVanillaMainhandItem((EntityPlayerMP) sender);
                if (stack.isEmpty())
                {
                    notifyCommandListener(sender, this, getUsage(sender));
                    return;
                }

                MiscTags.setItemValue(stack, value);

                break;


            default:
                notifyCommandListener(sender, this, getUsage(sender));
                break;
        }
    }
}
