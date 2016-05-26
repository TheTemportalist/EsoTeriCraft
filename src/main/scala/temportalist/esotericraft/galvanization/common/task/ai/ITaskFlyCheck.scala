package temportalist.esotericraft.galvanization.common.task.ai

import net.minecraft.entity.EntityCreature
import temportalist.esotericraft.api.galvanize.ai.IGalvanizeTask
import temportalist.esotericraft.galvanization.common.entity.EntityEmpty

/**
  *
  * Created by TheTemportalist on 5/22/2016.
  *
  * @author TheTemportalist
  */
trait ITaskFlyCheck extends IGalvanizeTask {

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
