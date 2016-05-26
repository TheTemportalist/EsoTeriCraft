package temportalist.esotericraft.galvanization.common.task.ai.interfaces

import net.minecraft.entity.EntityLivingBase

/**
  *
  * Created by TheTemportalist on 5/26/2016.
  *
  * @author TheTemportalist
  */
trait ITargetEntity {

	private var target: EntityLivingBase = null

	final def getTarget: EntityLivingBase = this.target

	final def setTarget(target: EntityLivingBase): Unit = {
		this.target = target
	}

}
