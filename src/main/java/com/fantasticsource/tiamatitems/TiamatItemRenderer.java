package com.fantasticsource.tiamatitems;

import com.evilnotch.iitemrender.handlers.IItemRenderer;
import com.evilnotch.iitemrender.handlers.IItemRendererHandler;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;

import java.util.LinkedHashMap;
import java.util.List;

import static com.evilnotch.iitemrender.handlers.IItemRendererHandler.allowEnchants;
import static com.evilnotch.iitemrender.handlers.IItemRendererHandler.applyTransforms;
import static com.fantasticsource.tiamatitems.TiamatItems.MODID;
import static org.lwjgl.opengl.GL11.GL_QUADS;

public class TiamatItemRenderer implements IItemRenderer
{
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public static final VertexFormat VOXEL = new VertexFormat();
    private static LinkedHashMap<String, LinkedHashMap<Integer, String>> readyTextures = new LinkedHashMap<>();

    static
    {
        VOXEL.addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.COLOR_4UB);
    }

    @Override
    public void render(ItemStack stack, IBakedModel model, ItemCameraTransforms.TransformType transformType, float v)
    {
        NBTTagCompound mainTag = stack.getTagCompound();
        if (mainTag == null || !mainTag.hasKey(MODID))
        {
            //START TEST CODE


            int color = 0x99ff0000;

            boolean cachedEnch = allowEnchants;
            allowEnchants = true;
            GlStateManager.enableCull();

            GlStateManager.pushMatrix();
            if (IItemRendererHandler.isRunning)
            {
                GlStateManager.translate(0.5, 0.5, 0.5);
                applyTransforms(model);
                renderItem(stack, model, color);
            }
            else
            {
                applyTransforms(model);
                renderItem(stack, model, color);
            }
            GlStateManager.popMatrix();

            GlStateManager.disableCull();
            allowEnchants = cachedEnch;


            //END TEST CODE


            return;
        }

        NBTTagCompound compound = mainTag.getCompoundTag(MODID);
        if (!compound.hasKey("texture"))
        {
            IItemRendererHandler.renderItemStack(stack, model);
            return;
        }

        String textureName = compound.getString("texture");
        Pair<Integer, Integer> textureSize = TiamatItems.validTextureNamesAndSizes.get(textureName);
        if (textureSize == null)
        {
            IItemRendererHandler.renderItemStack(stack, model);
            return;
        }

        LinkedHashMap<Integer, String> recoloredTextures = readyTextures.computeIfAbsent(textureName, o -> new LinkedHashMap<>());

        for (String key : new String[]{"r", "g", "b"})
        {
            //START TEST CODE


            GlStateManager.enableCull();
            GlStateManager.disableLighting();
            GlStateManager.disableTexture2D();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(GL_QUADS, VOXEL);
            voxel(bufferBuilder, 0, 0, 0, 255, 0, 0, 90);
            tessellator.draw();

            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.disableCull();


            //END TEST CODE


//            if (!compound.hasKey(key)) continue;
//
//            String recoloredTexture = recoloredTextures.computeIfAbsent(compound.getInteger(key), o ->
//            {
//                StringBuilder name = new StringBuilder(textureName);
//                int color = compound.getInteger(key);
//                String colorString = Integer.toHexString(color);
//
//                for (int i = colorString.length(); i < 6; i++) name.append("0");
//                name.append(colorString);
//
//                return name.toString();
//            });
        }
    }

    @Override
    public TransformPreset getTransformPreset()
    {
        return TransformPreset.NONE;
    }

    private static void voxel(BufferBuilder bufferBuilder, double x, double y, double z, int r, int g, int b, int a)
    {
        //THE BUFFERBUILDER ONLY USES COLOR PER-QUAD, NOT PER-VERTEX!  IT WILL ALWAYS USE THE COLOR OF THE LAST VERTEX IN THE QUAD FOR THE ENTIRE QUAD!
        bufferBuilder.pos(0, 0, 0).color(r, g, b, a).endVertex();
        bufferBuilder.pos(1, 0, 0).color(r, g, b, a).endVertex();
        bufferBuilder.pos(1, 1, 0).color(r, g, b, a).endVertex();
        bufferBuilder.pos(0, 1, 0).color(r, g, b, a).endVertex();
    }


    private void renderItem(ItemStack stack, IBakedModel model, int color)
    {
        if (!stack.isEmpty())
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5, -0.5, -0.5);

            if (model.isBuiltInRenderer())
            {
                GlStateManager.color(1, 1, 1, 1);
                GlStateManager.enableRescaleNormal();
                stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
            }
            else
            {
                renderModel(model, color);

                if (stack.hasEffect())
                {
                    renderEffect(model);
                }
            }

            GlStateManager.popMatrix();
        }
    }

    private void renderEffect(IBakedModel model)
    {
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        textureManager.bindTexture(RES_ITEM_GLINT);
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(8, 8, 8);
        float f = (float) (Minecraft.getSystemTime() % 3000) / 3000 / 8;
        GlStateManager.translate(f, 0, 0);
        GlStateManager.rotate(-50, 0, 0, 1);
        renderModel(model, -8372020);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8, 8, 8);
        float f1 = (float) (Minecraft.getSystemTime() % 4873) / 4873 / 8;
        GlStateManager.translate(-f1, 0, 0);
        GlStateManager.rotate(10, 0, 0, 1);
        renderModel(model, -8372020);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }

    private void renderModel(IBakedModel model, int color)
    {
        List<BakedQuad> quads = model.getQuads(null, null, 0);
        if (quads.size() == 0) return;


        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_QUADS, quads.get(0).getFormat());

        for (BakedQuad quad : quads)
        {
            bufferbuilder.addVertexData(quad.getVertexData());
            ForgeHooksClient.putQuadColor(bufferbuilder, quad, color);
        }

        tessellator.draw();
    }
}
