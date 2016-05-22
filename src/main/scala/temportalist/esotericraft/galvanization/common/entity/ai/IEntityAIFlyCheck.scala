package temportalist.esotericraft.galvanization.common.entity.ai

import net.minecraft.entity.EntityCreature
import temportalist.esotericraft.galvanization.common.entity.EntityEmpty

/**
  *
  * Created by TheTemportalist on 5/22/2016.
  *
  * @author TheTemportalist
  */
trait IEntityAIFlyCheck {

	private var canFly = false

	final def checkCanFly(entity: EntityCreature): Boolean = {
		this.canFly = entity match {
			case empty: EntityEmpty => empty.canFly
			case _ => false
		}
		this.canFly
	}

	final def getCanFly: Boolean = this.canFly

}
