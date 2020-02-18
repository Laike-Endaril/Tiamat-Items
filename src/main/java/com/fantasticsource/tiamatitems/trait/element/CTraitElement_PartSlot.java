package com.fantasticsource.tiamatitems.trait.element;

import com.fantasticsource.tiamatitems.nbt.PartSlotTags;
import com.fantasticsource.tiamatitems.trait.CTraitElement;
import com.fantasticsource.tiamatitems.trait.IUnmultipliedRangeTrait;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;

public class CTraitElement_PartSlot extends CTraitElement implements IUnmultipliedRangeTrait
{
    public String partSlotType = "";
    public boolean required = false;

    @Override
    public String getDescription()
    {
        return "Add " + Tools.max(0, (int) minimum) + " to " + Tools.max(0, (int) maximum) + (required ? " required " : " optional ") + partSlotType + " slots";
    }

    @Override
    public String getDescription(int wholeNumberPercentage)
    {
        return "Add " + getIntAmount(wholeNumberPercentage) + (required ? " required " : " optional ") + partSlotType + " slots";
    }

    @Override
    public void applyToItem(ItemStack stack, int wholeNumberPercentage)
    {
        for (int i = getIntAmount(wholeNumberPercentage); i > 0; i--) PartSlotTags.addPartSlot(stack, partSlotType, required);
    }


    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof CTraitElement_PartSlot)) return false;

        CTraitElement_PartSlot other = (CTraitElement_PartSlot) obj;
        return other.partSlotType.equals(partSlotType) && other.required == required;
    }


    @Override
    public CTraitElement_PartSlot write(ByteBuf buf)
    {
        super.write(buf);

        ByteBufUtils.writeUTF8String(buf, partSlotType);
        buf.writeBoolean(required);

        return this;
    }

    @Override
    public CTraitElement_PartSlot read(ByteBuf buf)
    {
        super.read(buf);

        partSlotType = ByteBufUtils.readUTF8String(buf);
        required = buf.readBoolean();

        return this;
    }

    @Override
    public CTraitElement_PartSlot save(OutputStream stream)
    {
        super.save(stream);

        new CStringUTF8().set(partSlotType).save(stream);
        new CBoolean().set(required).save(stream);

        return this;
    }

    @Override
    public CTraitElement_PartSlot load(InputStream stream)
    {
        super.load(stream);

        partSlotType = new CStringUTF8().load(stream).value;
        required = new CBoolean().load(stream).value;

        return this;
    }
}
