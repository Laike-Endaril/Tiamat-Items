package com.fantasticsource.tiamatitems.trait.recalculable.element;

import com.fantasticsource.tiamatitems.nbt.DropTransformationTags;
import com.fantasticsource.tiamatitems.trait.recalculable.CRecalculableTraitElement;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CRTraitElement_TransformItemOnDrop extends CRecalculableTraitElement
{
    public String rarity = "";
    public int minLevel = 1, maxLevel = 1;
    public ArrayList<String> itemTypes = new ArrayList<>();


    @Override
    public int requiredArgumentCount()
    {
        return 0;
    }


    @Override
    public String getDescriptionInternal(ArrayList<Integer> baseArgs, double[] multipliedArgs)
    {
        StringBuilder result = new StringBuilder("When dropped, transform this item into a level " + minLevel + " - " + maxLevel + " " + rarity + " item (" + (itemTypes.size() == 0 ? "no item types selected" : itemTypes.get(0)));
        for (int i = 1; i < itemTypes.size(); i++) result.append(", ").append(itemTypes.get(i));
        return result + ")";
    }


    @Override
    public void applyToItemInternal(ItemStack stack, int[] baseArgs, double[] multipliedArgs)
    {
        if (itemTypes.size() == 0) return;

        DropTransformationTags.setDropTransformation(stack, rarity, minLevel, maxLevel, itemTypes.toArray(new String[0]));
    }


    @Override
    public CRTraitElement_TransformItemOnDrop write(ByteBuf buf)
    {
        super.write(buf);

        ByteBufUtils.writeUTF8String(buf, rarity);
        buf.writeInt(minLevel);
        buf.writeInt(maxLevel);

        buf.writeInt(itemTypes.size());
        for (String itemType : itemTypes) ByteBufUtils.writeUTF8String(buf, itemType);

        return this;
    }

    @Override
    public CRTraitElement_TransformItemOnDrop read(ByteBuf buf)
    {
        super.read(buf);

        rarity = ByteBufUtils.readUTF8String(buf);
        minLevel = buf.readInt();
        maxLevel = buf.readInt();

        itemTypes.clear();
        for (int i = buf.readInt(); i > 0; i--) itemTypes.add(ByteBufUtils.readUTF8String(buf));

        return this;
    }

    @Override
    public CRTraitElement_TransformItemOnDrop save(OutputStream stream)
    {
        super.save(stream);

        CStringUTF8 cs = new CStringUTF8().set(rarity).save(stream);
        new CInt().set(minLevel).save(stream).set(maxLevel).save(stream).set(itemTypes.size()).save(stream);
        for (String itemType : itemTypes) cs.set(itemType).save(stream);

        return this;
    }

    @Override
    public CRTraitElement_TransformItemOnDrop load(InputStream stream)
    {
        super.load(stream);

        CStringUTF8 cs = new CStringUTF8();
        CInt ci = new CInt();

        rarity = cs.load(stream).value;
        minLevel = ci.load(stream).value;
        maxLevel = ci.load(stream).value;

        itemTypes.clear();
        for (int i = ci.load(stream).value; i > 0; i--) itemTypes.add(cs.load(stream).value);

        return this;
    }
}
