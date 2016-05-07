package temportalist.esotericraft.galvanization.common.entity.ai

import net.minecraft.entity.EntityCreature
import net.minecraft.entity.ai.EntityAIBase
import temportalist.origin.api.common.lib.Vect

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
class EntityAIPlayer(private val entity: EntityCreature) extends EntityAIBase {

	private var target: Vect = _

	override def shouldExecute(): Boolean = {

		val player = this.entity.getEntityWorld.getClosestPlayerToEntity(this.entity, 20D)
		if (player != null) {
			this.target = new Vect(player)
			true
		} else false

	}

	override def startExecuting(): Unit = {
		this.entity.getNavigator.tryMoveToXYZ(this.target.x, this.target.y, this.target.z, 2)
	}

}
