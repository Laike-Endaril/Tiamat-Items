package com.fantasticsource.tiamatitems;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamatitems.api.IPartSlot;
import com.fantasticsource.tiamatitems.nbt.AssemblyTags;
import com.fantasticsource.tiamatitems.nbt.MiscTags;
import com.fantasticsource.tiamatitems.nbt.PassiveAttributeModTags;
import com.fantasticsource.tiamatitems.nbt.TextureTags;
import com.fantasticsource.tiamatitems.settings.CRarity;
import com.fantasticsource.tools.Tools;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.*;

import static com.fantasticsource.tiamatitems.TiamatItems.DOMAIN;
import static com.fantasticsource.tiamatitems.TiamatItems.MODID;

public class TiamatItem extends Item
{
    public TiamatItem()
    {
        setCreativeTab(TiamatItems.creativeTab);

        setUnlocalizedName(MODID + ":tiamatitem");
        setRegistryName("tiamatitem");
    }

    public static ItemStack get(boolean cacheLayers, boolean cacheTexture, String... fullStateLayers)
    {
        ItemStack stack = new ItemStack(TiamatItems.tiamatItem);
        for (String layer : fullStateLayers)
        {
            if (layer != null) TextureTags.addItemLayer(stack, AssemblyTags.STATE_FULL, layer);
        }
        if (cacheLayers) TextureTags.addItemLayerCacheTag(stack);
        if (cacheTexture) TextureTags.addItemTextureCacheTag(stack);
        return stack;
    }

    @Override
    public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity)
    {
        if (AssemblyTags.getState(stack) < AssemblyTags.STATE_USABLE) return false;

        String slotting = "None";
        if (stack.hasTagCompound())
        {
            NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), DOMAIN);
            if (compound != null && compound.hasKey("slotting")) slotting = compound.getString("slotting");
        }

        if (slotting.equals("Any")) return true;

        switch (armorType)
        {
            case HEAD:
                return slotting.equals("Head") || slotting.equals("Armor");
            case CHEST:
                return slotting.equals("Chest") || slotting.equals("Armor");
            case LEGS:
                return slotting.equals("Legs") || slotting.equals("Armor");
            case FEET:
                return slotting.equals("Feet") || slotting.equals("Armor");

            case MAINHAND:
                return slotting.equals("Mainhand") || slotting.equals("Hand") || slotting.equals("Hotbar");
            case OFFHAND:
                return slotting.equals("Offhand") || slotting.equals("Hand");
        }

        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag flag)
    {
        addTooltipLines(tooltip, stack);

        if (flag.isAdvanced()) tooltip.add("");
    }

    @SideOnly(Side.CLIENT)
    protected void addTooltipLines(List<String> tooltip, ItemStack stack)
    {
        addTooltipLines(tooltip, stack, "");
    }

    @SideOnly(Side.CLIENT)
    protected void addTooltipLines(List<String> tooltip, ItemStack stack, String prefix)
    {
        //Rarity and level
        CRarity rarity = MiscTags.getItemRarity(stack);
        if (rarity != null) tooltip.add(prefix + rarity.textColor + "Level " + MiscTags.getItemLevel(stack) + " " + rarity.name);
        else tooltip.add(prefix + "Level " + MiscTags.getItemLevel(stack));


        //Durability
        int durability = getMaxDamage(stack);
        if (durability > 0)
        {
            int damage = getDamage(stack);
            if (damage < durability >> 1) tooltip.add(prefix + TextFormatting.GREEN + (durability - damage) + "/" + durability + TextFormatting.GRAY + " Durability");
            else if (damage < (durability * 3) >> 2) tooltip.add(prefix + TextFormatting.YELLOW + (durability - damage) + "/" + durability + TextFormatting.GRAY + " Durability");
            else tooltip.add(prefix + TextFormatting.RED + (durability - damage) + "/" + durability + TextFormatting.GRAY + " Durability");
        }


        //Passive attribute mods and value
        ArrayList<IPartSlot> partSlots = AssemblyTags.getPartSlots(stack);
        int value = MiscTags.getItemValue(stack);
        ArrayList<String> passiveModStrings = PassiveAttributeModTags.getPassiveMods(stack);
        if (GuiScreen.isShiftKeyDown())
        {
            for (IPartSlot partSlot : partSlots)
            {
                ItemStack part = partSlot.getPart();
                value -= MiscTags.getItemValue(part);
                for (String toRemove : PassiveAttributeModTags.getPassiveMods(part)) passiveModStrings.remove(toRemove);
            }
        }
        HashMap<String, Double>[] mods = new HashMap[3];
        mods[0] = new HashMap<>();
        mods[1] = new HashMap<>();
        mods[2] = new HashMap<>();
        for (String passiveModString : passiveModStrings)
        {
            String[] tokens = Tools.fixedSplit(passiveModString, ";");
            int operation = Integer.parseInt(tokens[2]);
            if (operation == 2)
            {
                mods[operation].put(tokens[0], mods[operation].getOrDefault(tokens[0], 1d) * Double.parseDouble(tokens[1]));
            }
            else
            {
                mods[operation].put(tokens[0], mods[operation].getOrDefault(tokens[0], 0d) + Double.parseDouble(tokens[1]));
            }
        }
        for (int i = 2; i >= 0; i--)
        {
            List<Map.Entry<String, Double>> list = Arrays.asList(mods[i].entrySet().toArray(new Map.Entry[0]));
            if (list.size() > 0) tooltip.add(prefix);
            list.sort((o1, o2) ->
            {
                double d1 = Math.abs(o1.getValue()), d2 = Math.abs(o2.getValue());
                if (d1 == d2) return 0;
                return d2 < d1 ? -1 : 1;
            });
            for (Map.Entry<String, Double> entry : list)
            {
                tooltip.add(prefix + MCTools.getAttributeModString(entry.getKey(), entry.getValue(), i));
            }
        }
        tooltip.add(prefix);
        tooltip.add(prefix + TextFormatting.YELLOW + "Value: " + value);


        //Part slots
        if (GuiScreen.isShiftKeyDown())
        {
            for (IPartSlot partSlot : partSlots)
            {
                ItemStack part = partSlot.getPart();
                tooltip.add(prefix);

                if (partSlot.getRequired())
                {
                    if (part.isEmpty()) tooltip.add(prefix + TextFormatting.BLUE + partSlot.getSlotType() + " Slot" + TextFormatting.RED + " (required, empty)");
                    else tooltip.add(prefix + TextFormatting.BLUE + partSlot.getSlotType() + " Slot" + TextFormatting.GREEN + " (required)");
                }
                else
                {
                    if (part.isEmpty()) tooltip.add(prefix + TextFormatting.BLUE + partSlot.getSlotType() + " Slot" + TextFormatting.GOLD + " (empty)");
                    else tooltip.add(prefix + TextFormatting.BLUE + partSlot.getSlotType() + " Slot");
                }

                if (!part.isEmpty())
                {
                    for (String line : part.getTooltip(null, ITooltipFlag.TooltipFlags.NORMAL))
                    {
                        tooltip.add(prefix + "#" + line);
                    }
                }
            }
        }
        else
        {
            if (partSlots.size() > 0)
            {
                tooltip.add("");
                tooltip.add(TextFormatting.BLUE + "Hold shift to see parts");
            }
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
            items.add(get(false, true));
        }
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return MiscTags.getTiamatItemStackLimit(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack)
    {
        return MiscTags.getItemDurability(stack);
    }

    public boolean hasDurability(ItemStack stack)
    {
        return MiscTags.hasItemDurability(stack);
    }

    @Override
    public boolean isDamageable()
    {
        //This is used by vanilla in such a way that I'd rather it be false than true
        //If it were stack-sensitive I could've used it, but it's not
        //I've done my own systems instead; see the methods below
        return false;
    }

    public boolean isDestroyable(ItemStack stack)
    {
        return MiscTags.isDestroyable(stack);
    }

    public void setDestroyable(ItemStack stack, boolean destroyable)
    {
        if (destroyable) MiscTags.setDestroyable(stack);
        else MiscTags.clearDestroyable(stack);
    }

    @Override
    public int getDamage(ItemStack stack)
    {
        return MiscTags.getItemDamage(stack);
    }

    public void damage(ItemStack stack, int damage)
    {
        setDamage(stack, damage + getDamage(stack));
    }

    @Override
    public void setDamage(ItemStack stack, int damage)
    {
        int durability = getMaxDamage(stack);
        if (durability == 0)
        {
            if (isDestroyable(stack)) MCTools.destroyItemStack(stack);
            return;
        }


        damage = Tools.min(damage, durability);
        if (isDestroyable(stack) && damage == durability) MCTools.destroyItemStack(stack);
        else MiscTags.setItemDamage(stack, damage);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return hasDurability(stack);
    }
}
