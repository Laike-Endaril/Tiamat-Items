package com.fantasticsource.tiamatitems.globalsettings;

import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CAffix extends Component
{
    public String name = "";
    public ArrayList<CAffixMod> mods = new ArrayList<>();


    @Override
    public CAffix write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeInt(mods.size());
        for (CAffixMod mod : mods) mod.write(buf);

        return this;
    }

    @Override
    public CAffix read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);

        mods.clear();
        for (int i = buf.readInt(); i > 0; i--) mods.add(new CAffixMod().read(buf));

        return this;
    }

    @Override
    public CAffix save(OutputStream stream)
    {
        new CStringUTF8().set(name).save(stream);
        new CInt().set(mods.size()).save(stream);
        for (CAffixMod mod : mods) mod.save(stream);

        return this;
    }

    @Override
    public CAffix load(InputStream stream)
    {
        name = new CStringUTF8().load(stream).value;

        mods.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) mods.add(new CAffixMod().load(stream));

        return this;
    }


    public static class CAffixMod extends Component
    {
        String attributeName = "";
        int operation = 0, weight = 0;
        boolean passive = true;

        public CAffixMod()
        {
        }

        public CAffixMod(String attributeName, int operation, int weight, boolean passive)
        {
            this.attributeName = attributeName;
            this.operation = operation;
            this.weight = weight;
            this.passive = passive;
        }

        @Override
        public CAffixMod write(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, attributeName);
            buf.writeInt(operation);
            buf.writeInt(weight);
            buf.writeBoolean(passive);

            return this;
        }

        @Override
        public CAffixMod read(ByteBuf buf)
        {
            attributeName = ByteBufUtils.readUTF8String(buf);
            operation = buf.readInt();
            weight = buf.readInt();
            passive = buf.readBoolean();

            return this;
        }

        @Override
        public CAffixMod save(OutputStream stream)
        {
            new CStringUTF8().set(attributeName).save(stream);
            new CInt().set(operation).save(stream).set(weight).save(stream);
            new CBoolean().set(passive).save(stream);

            return this;
        }

        @Override
        public CAffixMod load(InputStream stream)
        {
            attributeName = new CStringUTF8().load(stream).value;

            CInt i = new CInt();
            operation = i.load(stream).value;
            weight = i.load(stream).value;

            passive = new CBoolean().load(stream).value;

            return this;
        }
    }
}
