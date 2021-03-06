package temportalist.esotericraft.galvanization.client

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.opengl.GL11
import temportalist.esotericraft.galvanization.common.init.ModItems
import temportalist.esotericraft.galvanization.common.network.PacketUpdateClientTasks
import temportalist.esotericraft.galvanization.common.task.ITask
import temportalist.origin.api.client.{Rendering, TessRenderer}

import scala.collection.mutable

/**
  *
  * Created by TheTemportalist on 5/24/2016.
  *
  * @author TheTemportalist
  */
@SideOnly(Side.CLIENT)
object ClientTask {

	private val taskMap = mutable.Map[BlockPos, mutable.Map[EnumFacing, ITask]]()

	def clear(): Unit = {
		this.taskMap.clear()
	}

	def updateTasks(func: Int, task: ITask): Unit = {

		val pos = task.getPosition
		val containsPos = this.taskMap.contains(pos)

		if (func == PacketUpdateClientTasks.SPAWN) {
			if (!containsPos)
				this.taskMap(pos) = mutable.Map[EnumFacing, ITask]()
			this.taskMap(pos)(task.getFace) = task
		}
		else if (func == PacketUpdateClientTasks.BREAK) {
			if (containsPos) {
				val faceToTask = this.taskMap(pos)
				if (faceToTask.contains(task.getFace)) faceToTask.remove(task.getFace)
				if (faceToTask.isEmpty) this.taskMap.remove(pos)
				if (this.taskMap.isEmpty) this.taskMap.remove(pos)
			}
		}

	}

	def updateTasks(tasks: ITask*): Unit = {
		this.taskMap.clear()
		for (task <- tasks) this.updateTasks(PacketUpdateClientTasks.SPAWN, task)
	}

	/**
	  * NOTE: This is HEAVILY based on Azanor's Thaumcraft 1.8.9 version 5.2.4
	  * todo thaumcraft.client.lib.RenderEventHandler#drawSeals ln 488
	  */
	@SubscribeEvent
	def onRenderWorldLast(event: RenderWorldLastEvent): Unit = {

		val mc = Minecraft.getMinecraft
		val rendEnt = mc.getRenderViewEntity
		if (rendEnt == null) return
		val partialTicks = event.getPartialTicks

		val posToFaceToTask = this.taskMap
		if (posToFaceToTask.size <= 0) return

		val renderBox = rendEnt match {
			case player: EntityPlayer =>
				val held = player.getHeldItemMainhand
				held != null && held.getItem == ModItems.debugTask
			case _ => false
		}

		GlStateManager.pushMatrix()

		if (rendEnt.isSneaking) GlStateManager.disableDepth()
		GlStateManager.enableBlend()
		// GL11.glBlendFunc(770, 771); SRCALPHA -> SRC ONE MINUS
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
		GlStateManager.disableCull()

		val xRenderEntity = rendEnt.prevPosX + (rendEnt.posX - rendEnt.prevPosX) * partialTicks
		val yRenderEntity = rendEnt.prevPosY + (rendEnt.posY - rendEnt.prevPosY) * partialTicks
		val zRenderEntity = rendEnt.prevPosZ + (rendEnt.posZ - rendEnt.prevPosZ) * partialTicks
		GlStateManager.translate(-xRenderEntity, -yRenderEntity, -zRenderEntity)

		for (faceToTask <- posToFaceToTask.values) for (task <- faceToTask.values) {
			if (rendEnt.getDistanceSqToCenter(task.getPosition) <= 256D) {

				GlStateManager.pushMatrix()
				GlStateManager.color(1F, 1F, 1F, 1F)
				this.translate(task.getPosition, task.getFace, 0.05F, scale = 0.5F)
				this.renderTexture(task.getIconLocation, 0.1F)
				GlStateManager.popMatrix()
				if (renderBox) this.renderBoundingBox(task)

			}
		}

		GlStateManager.disableBlend()
		GlStateManager.enableCull()
		if (rendEnt.isSneaking) GlStateManager.enableDepth()

		GlStateManager.popMatrix()

	}

	// todo thaumcraft.client.lib.RenderEventHandler#translateSeal ln 622
	def translate(pos: BlockPos, face: EnumFacing, offset: Float, scale: Float = 1F): Unit = {
		val lostArea = 1F - scale
		val halfLost = lostArea / 2
		face match {
			case EnumFacing.DOWN =>
				GlStateManager.translate(
					pos.getX + halfLost,
					pos.getY,
					pos.getZ + halfLost
				)
				GlStateManager.rotate(+90F, 1F, 0F, 0F)
			case EnumFacing.UP =>
				GlStateManager.translate(
					pos.getX + halfLost,
					pos.getY + 1,
					pos.getZ - halfLost + 1
				)
				GlStateManager.rotate(-90F, 1F, 0F, 0F)
			case EnumFacing.NORTH =>
				GlStateManager.translate(
					pos.getX - halfLost + 1,
					pos.getY + halfLost,
					pos.getZ
				)
				GlStateManager.rotate(180F, 0F, 1F, 0F)
			case EnumFacing.SOUTH =>
				GlStateManager.translate(
					pos.getX + halfLost,
					pos.getY + halfLost,
					pos.getZ + 1
				)
			case EnumFacing.WEST =>
				GlStateManager.translate(
					pos.getX,
					pos.getY + halfLost,
					pos.getZ + halfLost
				)
				GlStateManager.rotate(-90F, 0F, 1F, 0F)
			case EnumFacing.EAST =>
				GlStateManager.translate(
					pos.getX + 1,
					pos.getY + halfLost,
					pos.getZ - halfLost + 1
				)
				GlStateManager.rotate(+90F, 0F, 1F, 0F)
			case _ =>
		}
		GlStateManager.scale(scale, scale, scale)
		GlStateManager.translate(0F, 0F, offset)
	}

	// todo thaumcraft.client.lib.UtilsFX#renderItemIn2D ln 758
	def renderTexture(icon: ResourceLocation, thickness: Float): Unit = {
		if (icon == null) return

		GlStateManager.pushMatrix()
		val uMax = 1 // icon.getMaxU
		val vMin = 0 // icon.getMinV
		val uMin = 0 // icon.getMinU
		val vMax = 1 // icon.getMaxV
		Minecraft.getMinecraft.getTextureManager.bindTexture(icon)

		val buf = TessRenderer.getBuffer

		///*
		TessRenderer.startQuads(DefaultVertexFormats.POSITION_TEX_NORMAL)
		buf.pos(0.0D, 0.0D, 0.0D).tex(uMax, vMax).normal(0, 0, 1).endVertex()
		buf.pos(1.0D, 0.0D, 0.0D).tex(uMin, vMax).normal(0, 0, 1).endVertex()
		buf.pos(1.0D, 1.0D, 0.0D).tex(uMin, vMin).normal(0, 0, 1).endVertex()
		buf.pos(0.0D, 1.0D, 0.0D).tex(uMax, vMin).normal(0, 0, 1).endVertex()
		TessRenderer.draw()
		//*/

		/*
		TessRenderer.startQuads(DefaultVertexFormats.POSITION_TEX_NORMAL)
		buf.pos(0.0D, 1.0D, 0.0F - thickness).tex(uMax, vMin).normal(0, 0, -1).endVertex()
		buf.pos(1.0D, 1.0D, 0.0F - thickness).tex(uMin, vMin).normal(0, 0, -1).endVertex()
		buf.pos(1.0D, 0.0D, 0.0F - thickness).tex(uMin, vMax).normal(0, 0, -1).endVertex()
		buf.pos(0.0D, 0.0D, 0.0F - thickness).tex(uMax, vMax).normal(0, 0, -1).endVertex()
		TessRenderer.draw()
		*/

		//val w = 1 // icon.getIconWidth
		//val h = 1 // icon.getIconHeight
		/*
		if (thickness > 0.0F) {
			val f5 = 0.5F * (uMax - uMin) / w
			val f6 = 0.5F * (vMax - vMin) / h

			TessRenderer.startQuads(DefaultVertexFormats.POSITION_TEX_NORMAL)
			for (k <- 0 until w) {
				val f7 = k / w
				val f8 = uMax + (uMin - uMax) * f7 - f5
				buf.pos(f7, 0, 0F - thickness  ).tex(f8, vMax).normal(-1, 0, 0).endVertex()
				buf.pos(f7, 0, 0D              ).tex(f8, vMax).normal(-1, 0, 0).endVertex()
				buf.pos(f7, 1, 0D              ).tex(f8, vMin).normal(-1, 0, 0).endVertex()
				buf.pos(f7, 1, 0F - thickness  ).tex(f8, vMin).normal(-1, 0, 0).endVertex()
			}
			TessRenderer.draw()

			TessRenderer.startQuads(DefaultVertexFormats.POSITION_TEX_NORMAL)
			for (k <- 0 until w) {
				val f7 = k / w
				val f8 = uMax + (uMin - uMax) * f7 - f5
				val f9 = f7 + 1.0F / w
				buf.pos(f9, 1, 0F - thickness  ).tex(f8, vMin).normal(1, 0, 0).endVertex()
				buf.pos(f9, 1, 0D              ).tex(f8, vMin).normal(1, 0, 0).endVertex()
				buf.pos(f9, 0, 0D              ).tex(f8, vMax).normal(1, 0, 0).endVertex()
				buf.pos(f9, 0, 0F - thickness  ).tex(f8, vMax).normal(1, 0, 0).endVertex()
			}
			TessRenderer.draw()

			TessRenderer.startQuads(DefaultVertexFormats.POSITION_TEX_NORMAL)
			for (k <- 0 until h) {
				val f7 = k / h
				val f8 = vMax + (vMin - vMax) * f7 - f6
				val f9 = f7 + 1.0F / icon.getIconHeight
				buf.pos(0, f9, 0D              ).tex(uMax, f8).normal(0, 1, 0).endVertex()
				buf.pos(1, f9, 0D              ).tex(uMin, f8).normal(0, 1, 0).endVertex()
				buf.pos(1, f9, 0F - thickness  ).tex(uMin, f8).normal(0, 1, 0).endVertex()
				buf.pos(0, f9, 0F - thickness  ).tex(uMax, f8).normal(0, 1, 0).endVertex()
			}
			TessRenderer.draw()

			TessRenderer.startQuads(DefaultVertexFormats.POSITION_TEX_NORMAL)
			for (k <- 0 until h) {
				val f7 = k / h
				val f8 = vMax + (vMin - vMax) * f7 - f6
				buf.pos(1, f7, 0D              ).tex(uMin, f8).normal(0, -1, 0).endVertex()
				buf.pos(0, f7, 0D              ).tex(uMax, f8).normal(0, -1, 0).endVertex()
				buf.pos(0, f7, 0F - thickness  ).tex(uMax, f8).normal(0, -1, 0).endVertex()
				buf.pos(1, f7, 0F - thickness  ).tex(uMin, f8).normal(0, -1, 0).endVertex()
			}
			TessRenderer.draw()

		}
		*/

		GlStateManager.popMatrix()

	}

	def renderBoundingBox(task: ITask): Unit = {
		if (!task.hasBoundingBox) return
		val aabb = task.getBoundingBox
		startLine()
		GlStateManager.color(0, 0, 0.9F, 0.4F)

		// ~~~~~ Four Corners ~~~~~
		// West (-X) by North (-Z): bottom to top
		GL11.glVertex3d(aabb.minX, aabb.minY, aabb.minZ)
		GL11.glVertex3d(aabb.minX, aabb.maxY, aabb.minZ)
		// East (+X) by North (-Z): bottom to top
		GL11.glVertex3d(aabb.maxX, aabb.minY, aabb.minZ)
		GL11.glVertex3d(aabb.maxX, aabb.maxY, aabb.minZ)
		// West (-X) by South (+Z): bottom to top
		GL11.glVertex3d(aabb.minX, aabb.minY, aabb.maxZ)
		GL11.glVertex3d(aabb.minX, aabb.maxY, aabb.maxZ)
		// East (+X) by South (+Z): bottom to top
		GL11.glVertex3d(aabb.maxX, aabb.minY, aabb.maxZ)
		GL11.glVertex3d(aabb.maxX, aabb.maxY, aabb.maxZ)

		// ~~~~~ Bottom ~~~~~

		// West (-X): north to south
		GL11.glVertex3d(aabb.minX, aabb.minY, aabb.minZ)
		GL11.glVertex3d(aabb.minX, aabb.minY, aabb.maxZ)
		// East (+X): north to south
		GL11.glVertex3d(aabb.maxX, aabb.minY, aabb.minZ)
		GL11.glVertex3d(aabb.maxX, aabb.minY, aabb.maxZ)
		// South (+Z): west to east
		GL11.glVertex3d(aabb.minX, aabb.minY, aabb.maxZ)
		GL11.glVertex3d(aabb.maxX, aabb.minY, aabb.maxZ)
		// North (-Z): west to east
		GL11.glVertex3d(aabb.minX, aabb.minY, aabb.minZ)
		GL11.glVertex3d(aabb.maxX, aabb.minY, aabb.minZ)

		// ~~~~~ Top ~~~~~

		// West (-X): north to south
		GL11.glVertex3d(aabb.minX, aabb.maxY, aabb.minZ)
		GL11.glVertex3d(aabb.minX, aabb.maxY, aabb.maxZ)
		// East (+X): north to south
		GL11.glVertex3d(aabb.maxX, aabb.maxY, aabb.minZ)
		GL11.glVertex3d(aabb.maxX, aabb.maxY, aabb.maxZ)
		// South (+Z): west to east
		GL11.glVertex3d(aabb.minX, aabb.maxY, aabb.maxZ)
		GL11.glVertex3d(aabb.maxX, aabb.maxY, aabb.maxZ)
		// North (-Z): west to east
		GL11.glVertex3d(aabb.minX, aabb.maxY, aabb.minZ)
		GL11.glVertex3d(aabb.maxX, aabb.maxY, aabb.minZ)

		endLine()
	}

	def startLine(): Unit = {
		GlStateManager.pushMatrix()
		GlStateManager.disableTexture2D()
		GlStateManager.enableBlend()
		Rendering.blendSrcAlpha()
		//GlStateManager.disableLighting()
		GL11.glLineWidth(1.5F)
		GL11.glBegin(GL11.GL_LINES)
	}

	def endLine(): Unit = {
		GL11.glEnd()
		//GlStateManager.enableLighting()
		GlStateManager.disableBlend()
		GlStateManager.enableTexture2D()
		GlStateManager.popMatrix()
	}

}
