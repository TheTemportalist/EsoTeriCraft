package temportalist.esotericraft.galvanization.client

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.{TextureAtlasSprite, TextureMap}
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraftforge.client.event.{RenderWorldLastEvent, TextureStitchEvent}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent
import org.lwjgl.opengl.GL11
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.entity.ai.LoaderAI
import temportalist.esotericraft.galvanization.common.task.ITask
import temportalist.origin.api.client.TessRenderer
import temportalist.origin.api.common.lib.Vect

import scala.collection.mutable

/**
  *
  * Created by TheTemportalist on 5/24/2016.
  *
  * @author TheTemportalist
  */
object ClientTask {

	private val taskMap = mutable.Map[
			Int, mutable.Map[BlockPos,
				mutable.Map[EnumFacing, ITask]
			]
	]()

	def updateTasks(doSpawn: Boolean, dimension: Int, task: ITask): Unit = {

		val containsDim = this.taskMap.contains(dimension)
		val pos = task.getPosition
		val containsPos = containsDim && this.taskMap(dimension).contains(pos)

		if (doSpawn) {
			if (!containsDim)
				this.taskMap(dimension) = mutable.Map[BlockPos, mutable.Map[EnumFacing, ITask]]()
			if (!containsPos)
				this.taskMap(dimension)(pos) = mutable.Map[EnumFacing, ITask]()
			this.taskMap(dimension)(pos)(task.getFace) = task
		}
		else {
			if (containsDim && containsPos) {
				val faceToTask = this.taskMap(dimension)(pos)
				if (faceToTask.contains(task.getFace)) faceToTask.remove(task.getFace)
				if (faceToTask.isEmpty) this.taskMap(dimension).remove(pos)
				if (this.taskMap(dimension).isEmpty) this.taskMap.remove(dimension)
			}
		}

	}

	//@SubscribeEvent
	def onRenderTick(event: RenderTickEvent): Unit = {

		val mc = Minecraft.getMinecraft
		val renderViewEntity = mc.getRenderViewEntity
		if (renderViewEntity == null) return
		val partialTicks = event.renderTickTime

		val xRenderEntity = renderViewEntity.prevPosX +
				(renderViewEntity.posX - renderViewEntity.prevPosX) * partialTicks
		val yRenderEntity = renderViewEntity.prevPosY +
				(renderViewEntity.posY - renderViewEntity.prevPosY) * partialTicks
		val zRenderEntity = renderViewEntity.prevPosZ +
				(renderViewEntity.posZ - renderViewEntity.prevPosZ) * partialTicks
		val posRenderEntity = new Vect(xRenderEntity, yRenderEntity, zRenderEntity)

		val dim = mc.theWorld.provider.getDimension
		if (!this.taskMap.contains(dim)) return

		GlStateManager.pushMatrix()
		GlStateManager.translate(xRenderEntity, yRenderEntity, zRenderEntity)

		val posToFaceToTask = this.taskMap(dim)
		for (posEntry <- posToFaceToTask.values) {
			for (task <- posEntry.values) {

				val posTask = new Vect(task.getPosition) + Vect.CENTER +
						new Vect(task.getFace) * 0.5

				val diffVect = posTask - posRenderEntity
				val dist = diffVect.magnitude

				var avgBBEdgeLength = 0.2D // average length around the rendered task
				avgBBEdgeLength = avgBBEdgeLength * 64D

				if (dist < avgBBEdgeLength * avgBBEdgeLength) {
					this.renderTask(task, posTask)
				}

			}
		}

		GlStateManager.popMatrix()

	}

	def renderTask(task: ITask, pos: Vect): Unit = {
		GlStateManager.pushMatrix()
		//GlStateManager.translate(pos.x, pos.y, pos.z)

		//Galvanize.log("render task")

		val mc = Minecraft.getMinecraft

		val stick = new ItemStack(Items.STICK)
		mc.getRenderItem.renderItem(stick, ItemCameraTransforms.TransformType.GROUND)

		//GlStateManager.scale(0.1, 0.1, 0.1)
		//Galvanize.log("RendTask " + pos)
		/*
		Minecraft.getMinecraft.getBlockRendererDispatcher.renderBlock(
			Blocks.BEDROCK.getDefaultState, pos.toBlockPos,
			Minecraft.getMinecraft.theWorld,
			TessRenderer.getBuffer
		)
		*/
		/*
		Minecraft.getMinecraft.getRenderManager.doRenderEntity(
			Minecraft.getMinecraft.thePlayer,
			0, 0, 0, 0, 0, false
		)
		*/
		/*
		Minecraft.getMinecraft.getRenderItem.renderItemIntoGUI(
			new ItemStack(Items.STICK), 0, 0
		)
		*/
		/*
		val x = pos.x
		val y = pos.y
		val z = pos.z
		val mc = Minecraft.getMinecraft
		val modid = Galvanize.getModId
		mc.getTextureManager.bindTexture(new ResourceLocation(modid, "textures/gui/guiSelected.png"))
		val buf = TessRenderer.getBuffer
		TessRenderer.startQuads(DefaultVertexFormats.POSITION_TEX)
		buf.pos(x,      y,        z + 10).tex(0, 1).endVertex()
		buf.pos(x + 10, y,        z + 10).tex(1, 1).endVertex()
		buf.pos(x + 10, y,        z).tex(1, 0).endVertex()
		buf.pos(x,      y,        z).tex(0, 0).endVertex()
		TessRenderer.draw()
		*/

		GlStateManager.popMatrix()
	}

	private var testTask: TextureAtlasSprite = null
	private val MAP_NAME_TO_ICON = mutable.Map[String, TextureAtlasSprite]()

	@SubscribeEvent
	def onTextureStitch(event: TextureStitchEvent.Post): Unit = {
		this.testTask = event.getMap.registerSprite(new ResourceLocation(
			Galvanize.getModId, "textures/tasks/test.png"
		))
		for (classOfAI <- LoaderAI.getClassInstances) {
			val info = LoaderAI.getAnnotationInfo(classOfAI)
			val name = info.getOrElse("name", null)
			val modid = info.getOrElse("modid", null)
			if (name != null && modid != null) {
				this.MAP_NAME_TO_ICON(name.toString) = event.getMap.registerSprite(
					new ResourceLocation(modid.toString,
						"textures/tasks/" + name.toString + ".png")
				)
			}
		}
	}

	/**
	  * NOTE: This is HEAVILY based on Azanor's Thaumcraft 1.8.9 version 5.2.4
	  *     todo thaumcraft.client.lib.RenderEventHandler#drawSeals ln 488
	  */
	@SubscribeEvent
	def onRenderWorldLast(event: RenderWorldLastEvent): Unit = {

		val mc = Minecraft.getMinecraft
		val player = mc.getRenderViewEntity
		if (player == null) return
		val partialTicks = event.getPartialTicks

		val dim = mc.theWorld.provider.getDimension
		if (!this.taskMap.contains(dim)) return

		val posToFaceToTask = this.taskMap(dim)
		if (posToFaceToTask.size <= 0) return

		GlStateManager.pushMatrix()

		if (player.isSneaking) GlStateManager.disableDepth()
		GlStateManager.enableBlend()
		// GL11.glBlendFunc(770, 771); SRCALPHA -> SRC ONE MINUS
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
		GlStateManager.disableCull()

		val xRenderEntity = player.prevPosX +
				(player.posX - player.prevPosX) * partialTicks
		val yRenderEntity = player.prevPosY +
				(player.posY - player.prevPosY) * partialTicks
		val zRenderEntity = player.prevPosZ +
				(player.posZ - player.prevPosZ) * partialTicks
		GlStateManager.translate(-xRenderEntity, -yRenderEntity, -zRenderEntity)

		for (posEntry <- posToFaceToTask.values) {
			for (task <- posEntry.values) {
				val dist = player.getDistanceSqToCenter(task.getPosition)
				if (dist <= 256D) {
					val alpha = 1F - (dist / 256).toFloat
					this.renderTask2(task, alpha)
				}
			}
		}

		GlStateManager.disableBlend()
		GlStateManager.enableCull()
		if (player.isSneaking) GlStateManager.enableDepth()

		GlStateManager.popMatrix()

	}

	// todo thaumcraft.client.lib.RenderEventHandler#renderSeal ln 611
	def renderTask2(task: ITask, alpha: Float): Unit = {
		GlStateManager.pushMatrix()
		GlStateManager.color(1F, 1F, 1F, alpha)

		this.translate(task.getPosition, task.getFace, 0.05F)
		this.renderTexture(this.MAP_NAME_TO_ICON.getOrElse(task.getName, this.testTask), 0.1F)

		GlStateManager.color(1F, 1F, 1F, 1F)
		GlStateManager.popMatrix()
	}

	// todo thaumcraft.client.lib.RenderEventHandler#translateSeal ln 622
	def translate(pos: BlockPos, face: EnumFacing, offset: Float): Unit = {
		face match {
			case EnumFacing.DOWN =>
				GlStateManager.translate(pos.getX + 0.00F, pos.getY + 0.00F, pos.getZ + 0.00F)
				GlStateManager.rotate(+90F, 1F, 0F, 0F)
			case EnumFacing.UP =>
				GlStateManager.translate(pos.getX + 0.00F, pos.getY + 1.00F, pos.getZ + 1.00F)
				GlStateManager.rotate(-90F, 1F, 0F, 0F)
			case EnumFacing.NORTH =>
				GlStateManager.translate(pos.getX + 1.00F, pos.getY + 0.00F, pos.getZ + 0.00F)
				GlStateManager.rotate(180F, 0F, 1F, 0F)
			case EnumFacing.SOUTH =>
				GlStateManager.translate(pos.getX + 0.00F, pos.getY + 0.00F, pos.getZ + 1.00F)
			case EnumFacing.WEST =>
				GlStateManager.translate(pos.getX + 0.00F, pos.getY + 0.00F, pos.getZ + 0.00F)
				GlStateManager.rotate(-90F, 0F, 1F, 0F)
			case EnumFacing.EAST =>
				GlStateManager.translate(pos.getX + 1.00F, pos.getY + 0.00F, pos.getZ + 1.00F)
				GlStateManager.rotate(+90F, 0F, 1F, 0F)
			case _ =>
		}
		GlStateManager.translate(0F, 0F, offset)
	}

	def renderTexture(resourceLocation: ResourceLocation, thickness: Float): Unit = {
		this.renderTexture(resourceLocation.toString, thickness)
	}

	// todo thaumcraft.client.lib.UtilsFX#renderItemIn2D ln 753
	def renderTexture(resourceLocation: String, thickness: Float): Unit = {
		this.renderTexture(Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(resourceLocation), thickness)
	}

	// todo thaumcraft.client.lib.UtilsFX#renderItemIn2D ln 758
	def renderTexture(icon: TextureAtlasSprite, thickness: Float): Unit = {
		//Galvanize.log("" + icon)
		GlStateManager.pushMatrix()
		val uMax = icon.getMaxU
		val vMin = icon.getMinV
		val uMin = icon.getMinU
		val vMax = icon.getMaxV
		val w = icon.getIconWidth
		val h = icon.getIconHeight
		Minecraft.getMinecraft.getTextureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
		/*
		Minecraft.getMinecraft.getTextureManager.bindTexture(
			new ResourceLocation(Galvanize.getModId, "textures/items/egg.png")
		)
		*/

		val buf = TessRenderer.getBuffer

		TessRenderer.startQuads(DefaultVertexFormats.POSITION_TEX_NORMAL)
		buf.pos(0.0D, 0.0D, 0.0D).tex(uMax, vMax).normal(0, 0, 1).endVertex()
		buf.pos(1.0D, 0.0D, 0.0D).tex(uMin, vMax).normal(0, 0, 1).endVertex()
		buf.pos(1.0D, 1.0D, 0.0D).tex(uMin, vMin).normal(0, 0, 1).endVertex()
		buf.pos(0.0D, 1.0D, 0.0D).tex(uMax, vMin).normal(0, 0, 1).endVertex()
		TessRenderer.draw()

		TessRenderer.startQuads(DefaultVertexFormats.POSITION_TEX_NORMAL)
		buf.pos(0.0D, 1.0D, 0.0F - thickness).tex(uMax, vMin).normal(0, 0, -1).endVertex()
		buf.pos(1.0D, 1.0D, 0.0F - thickness).tex(uMin, vMin).normal(0, 0, -1).endVertex()
		buf.pos(1.0D, 0.0D, 0.0F - thickness).tex(uMin, vMax).normal(0, 0, -1).endVertex()
		buf.pos(0.0D, 0.0D, 0.0F - thickness).tex(uMax, vMax).normal(0, 0, -1).endVertex()
		TessRenderer.draw()

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
		GlStateManager.popMatrix()

	}

}
