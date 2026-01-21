package mastef_chief.gitwebbuilder.app.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * GWBLogoModel - Mastef_Chief
 * Created using Tabula 7.0.0
 */
public class GWBLogoModel extends Model {
	private final ModelPart bb_main;
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("gitwebbuilder:logo"), "logo");

    public GWBLogoModel(ModelPart modelPart) {
		super(RenderType::entityCutout);
//        this.textureWidth = 64;
//        this.textureHeight = 32;
//        this.baseblock = new ModelRenderer(this, 0, 0);
//        this.baseblock.setRotationPoint(-6.0F, 12.0F, -6.0F);
//        this.baseblock.addBox(0.0F, 0.0F, 0.0F, 12, 12, 12, 0.0F);
//		PiglinModel
		this.bb_main = modelPart.getChild("bb_main");
    }

	public static LayerDefinition createTexturedModelData() {
		MeshDefinition meshDefinition1 = new MeshDefinition();
		PartDefinition meshDefinition = meshDefinition1.getRoot();
		//PartDefinition bb_main = meshDefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -16.0F, -0.5F, 10.0F, 16.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.rotation(0.0F, 8.0F, 0.0F));
		PartDefinition bb_main2 = meshDefinition
				.addOrReplaceChild("bb_main",
						CubeListBuilder.create()
								.texOffs(0, 0)
								.addBox(0.0F, 0.0F, 0.0F, 12, 12, 12,
										new CubeDeformation(0.0F)
								),
						PartPose.ZERO //PartPose.offset(-60.0F, 12.0F, -6.0F)
				);
		return LayerDefinition.create(meshDefinition1, 64, 32);

	}


	public ModelPart getBb_main() {
		return bb_main;
	}

	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		bb_main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
	}

//    /**
//     * This is a helper function from Tabula to set the rotation of model parts
//     */
//    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
//        modelRenderer.rotateAngleX = x;
//        modelRenderer.rotateAngleY = y;
//        modelRenderer.rotateAngleZ = z;
//    }

	public static EntityRendererProvider.Context createContext() {
		return new EntityRendererProvider.Context(
				Minecraft.getInstance().getEntityRenderDispatcher(),
				Minecraft.getInstance().getItemRenderer(),
				Minecraft.getInstance().getBlockRenderer(),
				Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer(),
				Minecraft.getInstance().getResourceManager(),
				Minecraft.getInstance().getEntityModels(),
				Minecraft.getInstance().font
		);
	}
}
