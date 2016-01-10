package temportalist.enhancing.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Created by TheTemportalist on 12/31/2015.
 */
public class Enhancement {

	private final String identifier;
	private final ResourceLocation texture;

	public Enhancement(String name, String prefix) {
		this.identifier = name;
		this.texture = new ResourceLocation(prefix, "textures/enhancements/" + name + ".png");
		ApiEsotericEnhancing.register(this);
	}

	public Enhancement(String name) {
		this(name, "esotericenhancing");
	}

	public final String getName() {
		return this.identifier;
	}

	public final  int getGlobalID() {
		return ApiEsotericEnhancing.getGlobalID(this);
	}

	public float computePower(float[] powers) {
		return 0;
	}

	public void onPlayerTick(EntityPlayer player, float power) {}

	public void onPlayerAttacking(EntityPlayer player, Entity entity, float power) {}

	public void onPlayerAttacked(EntityPlayer player, Entity entity, float power) {}

	public void onEquipChange(boolean isEquipped) {}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Enhancement && this.getGlobalID() == ((Enhancement)obj).getGlobalID();
	}

	@SideOnly(Side.CLIENT)
	public void draw(Minecraft mc, double x, double y) {
		mc.getTextureManager().bindTexture(this.texture);
		Tessellator tess = Tessellator.getInstance();
		WorldRenderer wr = tess.getWorldRenderer();
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		int size = 16;
		int halfSize = size / 2;
		wr.pos(x - halfSize, y + halfSize, 0D).tex(0, 1).endVertex();
		wr.pos(x + halfSize, y + halfSize, 0D).tex(1, 1).endVertex();
		wr.pos(x + halfSize, y - halfSize, 0D).tex(1, 0).endVertex();
		wr.pos(x - halfSize, y - halfSize, 0D).tex(0, 0).endVertex();
		tess.draw();
	}

	public static class Additive extends Enhancement {

		public Additive(String name, String prefix) {
			super(name, prefix);
		}

		@Override
		public float computePower(float[] powers) {
			float sum = 0f;
			for (float power : powers) sum += power;
			return sum;
		}

	}

}
