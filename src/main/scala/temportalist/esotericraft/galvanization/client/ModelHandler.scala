package temportalist.esotericraft.galvanization.client

import javax.annotation.Nullable

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.{Render, RenderLivingBase}
import net.minecraft.entity.EntityLivingBase
import temportalist.esotericraft.galvanization.common.Galvanize

import scala.collection.{JavaConversions, mutable}

/**
  *
  * Created by TheTemportalist on 5/8/2016.
  *
  * @author TheTemportalist
  */
object ModelHandler {

	private val entityModels = mutable.Map[Class[_], EntityModel[_ <: EntityLivingBase, _ <: EntityLivingBase]]()

	def loadEntityModels(): Unit = {

		val map = JavaConversions.mapAsScalaMap(Minecraft.getMinecraft.getRenderManager.entityRenderMap)
		for (entry <- map) {
			if (classOf[EntityLivingBase].isAssignableFrom(entry._1)) {
				this.loadModel(
					entry._1.asInstanceOf[Class[_ <: EntityLivingBase]],
					entry._2.asInstanceOf[Render[_ <: EntityLivingBase]]
				)
			}
		}

	}

	def loadModel[C <: EntityLivingBase, R <: EntityLivingBase](clazz: Class[C], renderer: Render[R]): Unit = {
		renderer match {
			case renderLiving: RenderLivingBase[R] =>
				this.entityModels.put(clazz, new EntityModel(clazz, renderer, renderLiving.getMainModel))
			case _ =>
		}
	}

	@Nullable
	def getEntityModel[E <: EntityLivingBase](entity: E): EntityModel[E, E] = {
		getEntityModel(entity.getClass.asInstanceOf[Class[E]])
	}

	@Nullable
	def getEntityModel[E <: EntityLivingBase](clazz: Class[E]): EntityModel[E, E] = {
		var classCurrent: Class[_] = clazz
		var info: EntityModel[E, E] = null
		while (clazz != classOf[EntityLivingBase] && info == null) {
			info = this.entityModels(classCurrent).asInstanceOf[EntityModel[E, E]]
			classCurrent = classCurrent.getSuperclass
		}
		//Galvanize.log("Found render for " + clazz.getSimpleName + " to " + classCurrent.getSimpleName)
		info
	}

}
