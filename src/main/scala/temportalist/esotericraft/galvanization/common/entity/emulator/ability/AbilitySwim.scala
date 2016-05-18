package temportalist.esotericraft.galvanization.common.entity.emulator.ability

import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.{Gui, ScaledResolution}
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{EntityLivingBase, SharedMonsterAttributes}
import net.minecraft.nbt.NBTTagByte
import net.minecraft.util.DamageSource
import net.minecraft.util.math.MathHelper
import net.minecraftforge.client.GuiIngameForge
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.api.galvanize.IAbility.Ability
import temportalist.esotericraft.api.galvanize.ability.IAbilitySwim
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.origin.api.client.TessRenderer

/**
  *
  * Created by TheTemportalist on 5/18/2016.
  *
  * @author TheTemportalist
  */
@Ability(id = "swim")
class AbilitySwim extends AbilityBase[NBTTagByte] with IAbilitySwim {

	private var (canSurviveOutOfWater, canMaintainDepth) = (false, false)
	private var (swimSpeed, landSpeed) = (0F, 0F)
	private var air = 0

	// ~~~~~ Naming

	override def getName: String = "Swim"

	// ~~~~~ Map Parsing

	override def parseMappingArguments(args: Array[AnyRef]): Unit = {
		try {
			this.canSurviveOutOfWater = args(0).toString.toLowerCase.toBoolean
			this.swimSpeed = args(1).toString.toLowerCase.toFloat
			this.landSpeed = args(2).toString.toLowerCase.toFloat
			this.canMaintainDepth = args(3).toString.toLowerCase.toBoolean
		}
		catch {
			case e: Exception =>
				Galvanize.log("[AbilitySwim] Cannot parse mapping argument.")
				e.printStackTrace()
		}
	}

	override def encodeMappingArguments(): Array[String] = {
		Array[String](
			if (this.canSurviveOutOfWater) "true" else "false",
			this.swimSpeed.toString + "F",
			this.landSpeed.toString + "F",
			if (this.canMaintainDepth) "true" else "false"
		)
	}

	// ~~~~~ Entity Handling

	override def onUpdate(entity: EntityLivingBase): Unit = {

		if (this.air == 8008135)
			this.air = entity.getAir

		if (entity.isInWater) {

			if (entity.getEntityWorld.isRemote) {
				if (GuiIngameForge.renderAir)
					GuiIngameForge.renderAir = false
			}

			entity.setAir(300)
			this.air = entity.getAir

			if (this.swimSpeed != 1F && !(entity.isInstanceOf[EntityPlayer] && entity.asInstanceOf[EntityPlayer].capabilities.isFlying)) {
				if (entity.motionX > -this.swimSpeed && entity.motionX < this.swimSpeed)
					entity.motionX *= this.swimSpeed * 0.995F
				if (entity.motionZ > -this.swimSpeed && entity.motionZ < this.swimSpeed)
					entity.motionZ *= this.swimSpeed * 0.995F
			}

			if (this.canMaintainDepth) {
				val isJumping = this.isJumping(entity)
				if (!entity.isSneaking && !isJumping && entity.isInsideOfMaterial(Material.WATER)) {
					entity.motionY = 0F
				}
				else {
					if (isJumping) entity.motionY *= this.swimSpeed
				}
			}

		}
		else if (!this.canSurviveOutOfWater) {
			val j = EnchantmentHelper.getRespirationModifier(entity)
			this.air = if (j > 0 && entity.getRNG.nextInt(j + 1) > 0) this.air else air - 1

			if (this.air == -20) {
				this.air = 0
				entity.attackEntityFrom(DamageSource.drown, 2F)
			}

			if (this.landSpeed != 1F && this.air < 285 &&
					!(entity.isInstanceOf[EntityPlayer] && entity.asInstanceOf[EntityPlayer].capabilities.isFlying)) {
				if (entity.motionX > -this.landSpeed && entity.motionX < this.landSpeed)
					entity.motionX *= this.landSpeed
				if (entity.motionZ > -this.landSpeed && entity.motionZ < this.landSpeed)
					entity.motionZ *= this.landSpeed
			}

		}

	}

	override def onRemovalFrom(entity: EntityLivingBase): Unit = {
		if (entity.getEntityWorld.isRemote) {
			if (!GuiIngameForge.renderAir) GuiIngameForge.renderAir = true
		}
	}

	def isJumping(entity: EntityLivingBase): Boolean = {
		ObfuscationReflectionHelper.getPrivateValue(classOf[EntityLivingBase], entity,
			"isJumping", "field_70703_bu").asInstanceOf[Boolean]
	}

	// ~~~~~ Rendering

	@SideOnly(Side.CLIENT)
	override def renderPost(entity: EntityLivingBase): Unit = {
		if (!this.canSurviveOutOfWater) {
			val mc = Minecraft.getMinecraft
			if (mc.currentScreen == null && mc.thePlayer == entity &&
					!mc.thePlayer.isInsideOfMaterial(Material.WATER) &&
					!mc.thePlayer.capabilities.disableDamage) {

				mc.getTextureManager.bindTexture(Gui.ICONS)

				val resolution = new ScaledResolution(mc)
				val width = resolution.getScaledWidth
				val height = resolution.getScaledHeight

				val l1 = width / 2 + 91
				val i2 = height - 39

				val attributeInst = mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				val f = attributeInst.getAttributeValue.toFloat
				val f1 = mc.thePlayer.getAbsorptionAmount
				val j2 = MathHelper.ceiling_float_int((f + f1) / 2F / 10F)
				val k2 = Math.max(10 - (j2 - 2), 3)
				val l2 = i2 - (j2 - 1) * k2 - 10
				val k3 = this.air
				val l4 = MathHelper.ceiling_double_int((this.air - 2) * 10D / 300D)
				val i4 = MathHelper.ceiling_double_int(this.air * 10D / 300D) - 14

				for (k4 <- 0 until l4 + i4) {
					if (k4 < l4)
						this.drawTexturedModalRect(l1 - k4 * 8 - 9, l2, 16, 18, 9, 9)
					else
						this.drawTexturedModalRect(l1 - k4 * 8 - 9, l2, 25, 18, 9, 9)
				}

			}
		}
	}

	@SideOnly(Side.CLIENT)
	def drawTexturedModalRect(x: Int, y: Int, u: Int, v: Int, w: Int, h: Int): Unit = {
		val f = 0.00390625F
		val buffer = TessRenderer.getBuffer
		TessRenderer.startQuads(DefaultVertexFormats.POSITION_TEX)
		buffer.pos(x + 0, y + h, 0).tex((u + 0) * f, (v + h) * f).endVertex()
		buffer.pos(x + w, y + h, 0).tex((u + w) * f, (v + h) * f).endVertex()
		buffer.pos(x + w, y + 0, 0).tex((u + w) * f, (v + 0) * f).endVertex()
		buffer.pos(x + 0, y + 0, 0).tex((u + 0) * f, (v + 0) * f).endVertex()
		TessRenderer.draw()
	}

}
