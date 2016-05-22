package temportalist.esotericraft.galvanization.common.entity.ai

import temportalist.esotericraft.api.galvanize.ai.{AIEmptyHelper, EntityAIEmpty}

/**
  *
  * Created by TheTemportalist on 5/22/2016.
  *
  * @author TheTemportalist
  */
@AIEmptyHelper
class EntityAIItemDepositHelper extends IEntityAIOriginHelper {

	override def getClassAI: Class[_ <: EntityAIEmpty] = classOf[EntityAIItemDeposit]

}
