package temportalist.esotericraft.galvanization.common.entity.ai

import temportalist.esotericraft.api.galvanize.ai.{AIEmptyHelper, EntityAIEmpty, EntityAIHelperObj}

/**
  *
  * Created by TheTemportalist on 5/22/2016.
  *
  * @author TheTemportalist
  */
@AIEmptyHelper
class EntityAIItemPickUpHelper extends EntityAIHelperObj with IEntityAIOriginHelper {

	override def getClassAI: Class[_ <: EntityAIEmpty] = classOf[EntityAIItemPickUp]

}
