package temportalist.esotericraft.api.sorcery;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.common.registry.PersistentRegistryManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 *
 * Created by TheTemportalist on 4/23/2016.
 * @author TheTemportalist
 */
public class Spell extends IForgeRegistryEntry.Impl<Spell> {

	public static final RegistryNamespaced<ResourceLocation, Spell> REGISTRY =
			PersistentRegistryManager.createRegistry(
					new ResourceLocation("esotericsorcery:spells"),
					Spell.class, null, 0, 31999, true, null, null, null
			);

	private final String name;
	private ResourceLocation textureLocation;

	public Spell(String name) {
		this.name = name;
		this.textureLocation = null;
	}

	public Spell(String name, ResourceLocation texture) {
		this(name);
		this.textureLocation = texture;
	}

	public Spell(String mod_id, String name) {
		this(name);
		this.setRegistryName(mod_id, name);
		GameRegistry.register(this);
	}

	public Spell(String mod_id, String name, ResourceLocation texture) {
		this(name, texture);
		this.setRegistryName(mod_id, name);
		GameRegistry.register(this);
	}

	public final String getName() {
		return this.name;
	}

	public ResourceLocation getTexture() {
		return this.textureLocation;
	}

	@SideOnly(Side.CLIENT)
	public final void draw(double x, double y, float opacity) {
		GlStateManager.pushMatrix();
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.getTexture());
		double w = 16, h = 16;
		double uMin = this.getU_min(), uMax = this.getU_max();
		double vMin = this.getV_min(), vMax = this.getV_max();

		Tessellator tess = Tessellator.getInstance();
		VertexBuffer buffer = tess.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

		// min max
		buffer.pos(x + 0, y + h, 0D).tex(uMin, vMax).color(1F, 1F, 1F, opacity).endVertex();
		// max max
		buffer.pos(x + w, y + h, 0D).tex(uMax, vMax).color(1F, 1F, 1F, opacity).endVertex();
		// max min
		buffer.pos(x + w, y + 0, 0D).tex(uMax, vMin).color(1F, 1F, 1F, opacity).endVertex();
		// min min
		buffer.pos(x + 0, y + 0, 0D).tex(uMin, vMin).color(1F, 1F, 1F, opacity).endVertex();

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
