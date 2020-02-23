package com.fantasticsource.tiamatitems.trait.element;

import com.fantasticsource.mctools.aw.ForcedAWSkinOverrides;
import com.fantasticsource.tiamatitems.trait.CTraitElement;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;

public class CTraitElement_ForcedAWSkinTypeOverride extends CTraitElement
{
    public String skinType = "";

    @Override
    public String getDescription()
    {
        return "Forced AW Skin Type Override: " + skinType;
    }

    @Override
    public String getDescription(int wholeNumberPercentage)
    {
        return getDescription();
    }

    @Override
    public void applyToItem(ItemStack stack, int wholeNumberPercentage)
    {
        ForcedAWSkinOverrides.setForcedAWSkinType(stack, skinType);
    }


    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof CTraitElement_ForcedAWSkinTypeOverride && ((CTraitElement_ForcedAWSkinTypeOverride) obj).skinType.equals(skinType);
    }


    @Override
    public CTraitElement_ForcedAWSkinTypeOverride write(ByteBuf buf)
    {
        super.write(buf);

        ByteBufUtils.writeUTF8String(buf, skinType);

        return this;
    }

    @Override
    public CTraitElement_ForcedAWSkinTypeOverride read(ByteBuf buf)
    {
        super.read(buf);

        skinType = ByteBufUtils.readUTF8String(buf);

        return this;
    }

    @Override
    public CTraitElement_ForcedAWSkinTypeOverride save(OutputStream stream)
    {
        super.save(stream);

        new CStringUTF8().set(skinType).save(stream);

        return this;
    }

    @Override
    public CTraitElement_ForcedAWSkinTypeOverride load(InputStream stream)
    {
        super.load(stream);

        skinType = new CStringUTF8().load(stream).value;

        return this;
    }
}
