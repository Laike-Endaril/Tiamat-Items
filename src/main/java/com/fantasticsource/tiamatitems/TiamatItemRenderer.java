package com.fantasticsource.tiamatitems;

import com.evilnotch.iitemrender.handlers.IItemRenderer;
import com.evilnotch.iitemrender.handlers.IItemRendererHandler;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.fantasticsource.tiamatitems.TiamatItems.MODID;
import static org.lwjgl.opengl.GL11.GL_QUADS;

public class TiamatItemRenderer implements IItemRenderer
{
    public static final VertexFormat VOXEL = new VertexFormat();
    private static final double Z1 = -0.5d / 16, Z2 = 0.5d / 16;
    private static double voxelSizeX, voxelSizeY;
    private static LinkedHashMap<String, LinkedHashMap<Integer, String>> readyTextures = new LinkedHashMap<>();

    static
    {
        VOXEL.addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.COLOR_4UB);
    }

    private static void voxel(BufferBuilder bufferBuilder, double x, double y, int r, int g, int b, int a, boolean renderLeft, boolean renderRight, boolean renderTop, boolean renderBottom)
    {
        //THE BUFFERBUILDER ONLY USES COLOR PER-QUAD, NOT PER-VERTEX!  IT WILL ALWAYS USE THE COLOR OF THE LAST VERTEX IN THE QUAD FOR THE ENTIRE QUAD!

        //All orientations are as seen from holder

        double x1 = -0.5 + voxelSizeX * x, x2 = -0.5 + voxelSizeX * (x + 1);
        double y1 = 0.5 - voxelSizeY * y, y2 = 0.5 - voxelSizeY * (y + 1);

        //Front
        bufferBuilder.pos(x1, y2, Z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x2, y2, Z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x2, y1, Z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x1, y1, Z2).color(r, g, b, a).endVertex();

        //Back
        bufferBuilder.pos(x2, y1, Z1).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x2, y2, Z1).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x1, y2, Z1).color(r, g, b, a).endVertex();
        bufferBuilder.pos(x1, y1, Z1).color(r, g, b, a).endVertex();

        //Left
        if (renderLeft)
        {
            bufferBuilder.pos(x1, y2, Z2).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x1, y1, Z2).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x1, y1, Z1).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x1, y2, Z1).color(r, g, b, a).endVertex();
        }

        //Right
        if (renderRight)
        {
            bufferBuilder.pos(x2, y2, Z1).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x2, y1, Z1).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x2, y1, Z2).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x2, y2, Z2).color(r, g, b, a).endVertex();
        }

        //Top
        if (renderTop)
        {
            bufferBuilder.pos(x1, y1, Z1).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x1, y1, Z2).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x2, y1, Z2).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x2, y1, Z1).color(r, g, b, a).endVertex();
        }

        //Bottom
        if (renderBottom)
        {
            bufferBuilder.pos(x2, y2, Z1).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x2, y2, Z2).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x1, y2, Z2).color(r, g, b, a).endVertex();
            bufferBuilder.pos(x1, y2, Z1).color(r, g, b, a).endVertex();
        }
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
        if (!compound.hasKey("layers"))
        {
            IItemRendererHandler.renderItemStack(stack, model);
            return;
        }

        NBTTagList layersNBT = compound.getTagList("layers", Constants.NBT.TAG_STRING);
        int count = layersNBT.tagCount();
        if (count == 0) return;

        String[] layerKeys = new String[count];
        for (int i = 0; i < count; i++)
        {
            String layer = layersNBT.getStringTagAt(i);
            int colons = 0;
            for (char c : layer.toCharArray()) if (c == ':') colons++;
            if (colons != 2) return;

            layerKeys[i] = layer;
        }


        //Check and load/generate/cache texture
        StringBuilder combinedReference = new StringBuilder(layerKeys[0]);
        for (int i = 1; i < layerKeys.length; i++) combinedReference.append("|").append(layerKeys[i]);

        int width = 0, height = 0;
        Texture texture = TextureCache.textures.get(combinedReference.toString());

        if (texture == null)
        {
            //Check and load/generate/cache layers
            ArrayList<Texture> layers = new ArrayList<>();
            for (String key : layerKeys)
            {
                Texture layer = TextureCache.textures.get(key);
                if (layer == null)
                {
                    //Get raw ("white") layer (may not actually be grayscale)
                    String[] tokens = Tools.fixedSplit(key, ":");
                    Texture whiteLayer = TextureCache.textures.get(tokens[0] + ":" + tokens[1] + ":ffffffff");
                    if (whiteLayer == null) return;


                    //Get blend color
                    Color blendColor = new Color(tokens[2].trim());


                    //Generate layer
                    layer = new Texture(whiteLayer.width, whiteLayer.height);
                    for (int x = 0; x < layer.width; x++)
                    {
                        for (int y = 0; y < layer.height; y++)
                        {
                            int[] c = whiteLayer.colors[x][y];
                            Color whiteColor = new Color(c[0], c[1], c[2], c[3]);

                            layer.colors[x][y][0] = (int) (blendColor.r() * whiteColor.rf());
                            layer.colors[x][y][1] = (int) (blendColor.g() * whiteColor.gf());
                            layer.colors[x][y][2] = (int) (blendColor.b() * whiteColor.bf());
                            layer.colors[x][y][3] = (int) (blendColor.a() * whiteColor.af());
                        }
                    }


                    //Cache
                    if (compound.getBoolean("cacheLayers"))
                    {
                        TextureCache.textures.put(key, layer);
                    }
                }


                width = Tools.max(width, layer.width);
                height = Tools.max(height, layer.height);

                layers.add(layer);
            }


            //Generate final texture
            texture = new Texture(width, height);
            int r, g, b, a;
            for (Texture layer : layers)
            {
                layer.xScale = width / layer.width;
                layer.yScale = height / layer.height;
            }

            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    r = 0;
                    b = 0;
                    g = 0;
                    a = 0;

                    for (Texture layer : layers)
                    {
                        int layerA = layer.colors[x * layer.xScale][y * layer.yScale][3];

                        r = (int) Tools.min(255, r + layer.colors[x * layer.xScale][y * layer.yScale][0] * layerA / 255d);
                        g = (int) Tools.min(255, g + layer.colors[x * layer.xScale][y * layer.yScale][1] * layerA / 255d);
                        b = (int) Tools.min(255, b + layer.colors[x * layer.xScale][y * layer.yScale][2] * layerA / 255d);

                        a = Tools.min(255, a + layerA);
                    }

                    texture.colors[x][y][0] = r;
                    texture.colors[x][y][1] = g;
                    texture.colors[x][y][2] = b;
                    texture.colors[x][y][3] = a;
                }
            }


            //Cache
            if (compound.getBoolean("cacheTexture"))
            {
                TextureCache.textures.put(combinedReference.toString(), texture);
            }
        }


        //Render
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0.5, 0.5);
        IItemRendererHandler.applyTransforms(model);

        GlStateManager.enableCull();
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL_QUADS, VOXEL);

        int[][][] colors = texture.colors;

        width = texture.width;
        height = texture.height;
        voxelSizeX = 1d / width;
        voxelSizeY = 1d / height;

        int color[], alpha, leftAlpha, rightAlpha, upAlpha, downAlpha;
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                color = colors[x][y];

                alpha = color[3];
                if (alpha == 0) continue;

                leftAlpha = x == 0 ? 0 : colors[x - 1][y][3];
                rightAlpha = x == width - 1 ? 0 : colors[x + 1][y][3];
                upAlpha = y == 0 ? 0 : colors[x][y - 1][3];
                downAlpha = y == height - 1 ? 0 : colors[x][y + 1][3];

                voxel(bufferBuilder, x, y, color[0], color[1], color[2], alpha, leftAlpha == 0 || (leftAlpha != 255 && alpha == 255), rightAlpha == 0 || (rightAlpha != 255 && alpha == 255), upAlpha == 0 || (upAlpha != 255 && alpha == 255), downAlpha == 0 || (downAlpha != 255 && alpha == 255));
            }
        }

        tessellator.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.disableCull();

        GlStateManager.popMatrix();
    }

    @Override
    public TransformPreset getTransformPreset()
    {
        return TransformPreset.NONE;
    }
}
