package temportalist.esotericraft.main.common

import temportalist.origin.foundation.common.registers.OptionRegister

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
object Options extends OptionRegister {

	override def register(): Unit = {

	}

	/**
	  * If [[hasDefaultConfig]] returns true, this is used to determine the config's file extension
	  *
	  * @return The extension for the file. 'cfg' and 'json' are supported.
	  */
	override def getExtension: String = "json"

}
