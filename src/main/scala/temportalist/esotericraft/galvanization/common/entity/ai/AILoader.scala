package temportalist.esotericraft.galvanization.common.entity.ai

import net.minecraft.entity.ai.EntityAIBase
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import temportalist.esotericraft.api.galvanize.ai.AIEmpty
import temportalist.esotericraft.main.common.api.AnnotationLoader

/**
  *
  * Created by TheTemportalist on 5/20/2016.
  *
  * @author TheTemportalist
  */
object AILoader extends AnnotationLoader(classOf[AIEmpty], classOf[EntityAIBase]) {

	def preInit(event: FMLPreInitializationEvent): Unit = {
		this.loadAnnotations(event)
	}

}
