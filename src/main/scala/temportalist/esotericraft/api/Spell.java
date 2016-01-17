package temportalist.esotericraft.api;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Created by TheTemportalist on 1/12/2016.
 */
public class Spell {

	private final String name;
	private ResourceLocation texture;

	protected Spell(String name) {
		this.name = name;
		ApiEsotericraft.Spells.register(this);
	}

	protected Spell(String name, ResourceLocation texture) {
		this(name);
		this.setTexture(texture);
	}

	public final Spell setTexture(ResourceLocation texture) {
		this.texture = texture;
		return this;
	}

	public final ResourceLocation getTexture() {
		return this.texture;
	}

	public final String getName() {
		return this.name;
	}

	public final int getGlobalID() {
		return ApiEsotericraft.Spells.getGlobalID(this);
	}

	@SideOnly(Side.CLIENT)
	public final void draw(double x, double y, float opacity) {
		GlStateManager.pushMatrix();
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.getTexture());
		double w = 16, h = 16;
		double uMin = this.getU_min(), uMax = this.getU_max();
		double vMin = this.getV_min(), vMax = this.getV_max();

		Tessellator tess = Tessellator.getInstance();
		WorldRenderer wr = tess.getWorldRenderer();
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

		// min max
		wr.pos(x + 0, y + h, 0D).tex(uMin, vMax).color(1F, 1F, 1F, opacity).endVertex();
		// max max
		wr.pos(x + w, y + h, 0D).tex(uMax, vMax).color(1F, 1F, 1F, opacity).endVertex();
		// max min
		wr.pos(x + w, y + 0, 0D).tex(uMax, vMin).color(1F, 1F, 1F, opacity).endVertex();
		// min min
		wr.pos(x + 0, y + 0, 0D).tex(uMin, vMin).color(1F, 1F, 1F, opacity).endVertex();

		tess.draw();
		GlStateManager.popMatrix();
	}

	protected double getSize() {
		return 16;
	}

	protected double getU_min() {
		return 0D;
	}

	protected double getU_max() {
		return 1D;
	}

	protected double getV_min() {
		return 0D;
	}

	protected double getV_max() {
		return 1D;
	}

}
