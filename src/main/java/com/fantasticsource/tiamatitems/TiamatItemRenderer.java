package com.fantasticsource.tiamatitems;

import com.evilnotch.iitemrender.handlers.IItemRenderer;
import com.evilnotch.iitemrender.handlers.IItemRendererHandler;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.LinkedHashMap;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;
import static org.lwjgl.opengl.GL11.GL_QUADS;

public class TiamatItemRenderer implements IItemRenderer
{
    public static final VertexFormat VOXEL = new VertexFormat();
    private static double voxelSizeX, voxelSizeY;
    private static final double Z1 = -0.5d / 16, Z2 = 0.5d / 16;
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
            IItemRendererHandler.renderItemStack(stack, model);
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

        int width = textureSize.getKey(), height = textureSize.getValue();
        voxelSizeX = 1d / width;
        voxelSizeY = 1d / height;

        LinkedHashMap<Integer, String> recoloredTextures = readyTextures.computeIfAbsent(textureName, o -> new LinkedHashMap<>());

        for (String key : new String[]{"r", "g", "b"})
        {
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


            GlStateManager.pushMatrix();
            GlStateManager.translate(0.5, 0.5, 0.5);
            IItemRendererHandler.applyTransforms(model);

            GlStateManager.enableCull();
            GlStateManager.disableLighting();
            GlStateManager.disableTexture2D();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(GL_QUADS, VOXEL);
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    voxel(bufferBuilder, x, y, 0, (int) (255 * ((double) x / width)), 0, (int) (255 * ((double) y / height)), 90);
                }
            }
            tessellator.draw();

            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.disableCull();

            GlStateManager.popMatrix();
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

        //All orientations are as seen from holder

        double x1 = -0.5 + voxelSizeX * x, x2 = -0.5 + voxelSizeX * (x + 1);
        double y1 = -0.5 + voxelSizeY * y, y2 = -0.5 + voxelSizeY * (y + 1);

        //Front
        bufferBuilder.pos(x1, y1, Z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x2, y1, Z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x2, y2, Z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x1, y2, Z2).color(r, g, b, a).endVertex();

        //Back
        bufferBuilder.pos(x1, y1, Z1).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x1, y2, Z1).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x2, y2, Z1).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x2, y1, Z1).color(r, g, b, a).endVertex();

        //Left
        bufferBuilder.pos(x1, y2, Z1).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x1, y1, Z1).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x1, y1, Z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x1, y2, Z2).color(r, g, b, a).endVertex();

        //Right
        bufferBuilder.pos(x2, y2, Z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x2, y1, Z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x2, y1, Z1).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x2, y2, Z1).color(r, g, b, a).endVertex();

        //Top
        bufferBuilder.pos(x1, y2, Z1).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x1, y2, Z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x2, y2, Z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x2, y2, Z1).color(r, g, b, a).endVertex();

        //Bottom
        bufferBuilder.pos(x2, y1, Z1).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x2, y1, Z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x1, y1, Z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x1, y1, Z1).color(r, g, b, a).endVertex();
    }
}
