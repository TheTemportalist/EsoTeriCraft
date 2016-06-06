package temportalist.esotericraft.galvanization.common.init

import temportalist.esotericraft.galvanization.common.entity.EntityEmpty
import temportalist.esotericraft.main.common.EsoTeriCraft
import temportalist.origin.foundation.common.registers.EntityRegister

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
object ModEntities extends EntityRegister {

	override def register(): Unit = {

		this.addEntity(classOf[EntityEmpty], "Empty", EsoTeriCraft)

	}

}
