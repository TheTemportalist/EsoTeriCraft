package temportalist.enhancing.common.init

import temportalist.enhancing.api.Enhancement
import temportalist.enhancing.common.enhancement.attack.EnArchery
import temportalist.enhancing.common.enhancement.gear.passive.{EnRegeneration, EnRespiration}
import temportalist.origin.foundation.common.register.Register

/**
  * Created by TheTemportalist on 12/31/2015.
  */
object Enhancements extends Register.Post {

	var archery: Enhancement = null
	var float: Enhancement = null
	var regeneration: Enhancement = null
	var respiration: Enhancement = null

	override def register(): Unit = {
		this.archery = new EnArchery
		//this.float = new EnFloat
		this.regeneration = new EnRegeneration
		this.respiration = new EnRespiration
	}

}
