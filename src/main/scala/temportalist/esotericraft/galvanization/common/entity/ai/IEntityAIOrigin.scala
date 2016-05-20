package temportalist.esotericraft.galvanization.common.entity.ai

import net.minecraft.entity.ai.EntityAIBase
import net.minecraft.util.EnumFacing
import temportalist.origin.api.common.lib.Vect

/**
  *
  * Created by TheTemportalist on 5/20/2016.
  *
  * @author TheTemportalist
  */
trait IEntityAIOrigin extends EntityAIBase {

	private var origin: Vect = null
	private var face: EnumFacing = null

	final def setOrigin(origin: Vect, face: EnumFacing = null): EntityAIBase = {
		this.origin = origin
		this.face = face
		this
	}

	final def getOriginPosition: Vect = this.origin

	final def getFace: EnumFacing = this.face

	final def getPosition: Vect = {
		if (this.origin == null) null
		else if (this.face == null) this.origin
		else this.origin + this.face
	}

}
