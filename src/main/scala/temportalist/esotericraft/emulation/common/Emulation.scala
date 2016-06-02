package temportalist.esotericraft.emulation.common

import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTBase
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import temportalist.esotericraft.api.emulation.IAbility
import temportalist.esotericraft.api.init.IEsoTeriCraft
import temportalist.esotericraft.api.init.IEsoTeriCraft.PluginEsoTeriCraft
import temportalist.esotericraft.emulation.common.ability.AbilityLoader
import temportalist.origin.api.common.IModDetails
import temportalist.origin.foundation.common.IModPlugin
import temportalist.origin.foundation.common.registers.OptionRegister

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  *
  * Created by TheTemportalist on 6/2/2016.
  *
  * @author TheTemportalist
  */
object Emulation extends IModPlugin with IModDetails {

	private var plugin: Plugin = _

	@PluginEsoTeriCraft
	class Plugin extends IEsoTeriCraft {
		override def onCreated(): Unit = plugin = this
	}



	override def getModId: String = "esoteric_emulation"

	override def getModName: String = "Esoteric Emulation"

	override def getModVersion: String = "@MOD_VERSION@"

	override def getDetails: IModDetails = this

	override def getNetworkName: String = "esoteric_emulation"

	override def getOptions: OptionRegister = null

	// ~~~~~~~~~~ Inits ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\

	def preInit(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(event)
		AbilityLoader.preInit(event)
		FetchResources.runMorph()
		FetchResources.runEmulation()
	}

	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event)

	}

	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)

	}

	// ~~~~~~~~~~ Entity Abilities ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	val MAP_STRING_to_CLASS_ABILITIES = mutable.Map[String, Class[_ <: IAbility[_ <: NBTBase]]]()
	val MAP_CLASS_to_ABILITIES =
		mutable.Map[Class[_ <: EntityLivingBase], Array[IAbility[_ <: NBTBase]]]()

	def getAbilitiesFor(entity: EntityLivingBase): Iterable[IAbility[_ <: NBTBase]] = {
		val list = ListBuffer[IAbility[_ <: NBTBase]]()
		var clazz: Class[_ <: EntityLivingBase] = entity.getClass
		while (clazz != null && clazz != classOf[EntityLivingBase]) {
			if (MAP_CLASS_to_ABILITIES.contains(clazz)) {
				val abilities = MAP_CLASS_to_ABILITIES
						.getOrElse(clazz, Array[IAbility[_ <: NBTBase]]())
				list ++= abilities
			}
			clazz = clazz.getSuperclass.asInstanceOf[Class[_ <: EntityLivingBase]]
		}
		list
	}

	def createAbility(name: String, argStrings: String, entry: String): IAbility[_] = {

		// Parse arguments

		var args = ListBuffer[AnyRef]()

		if (argStrings.contains(",")) {
			args ++= (
					for (arg <- argStrings.split(",")) yield parseAbilityArgument(arg.trim)
					)
		}
		else {
			args += parseAbilityArgument(argStrings.trim)
		}

		// Ability Object Creation

		val classAbility: Class[_ <: IAbility[_ <: NBTBase]] =
			MAP_STRING_to_CLASS_ABILITIES.getOrElse(name, null)
		if (classAbility != null) {
			var ability: IAbility[_ <: NBTBase] = null
			try {
				ability = classAbility.getConstructor().newInstance()
				ability.parseMappingArguments(args.toArray, entry)
			}
			catch {
				case e: Exception =>

			}
			ability
		}
		else {
			Emulation.log("[Abilities] Ability named \'" + name +
					"\' with mapping \'" + entry +
					"\' was not registered. The ability key in the mapping may be misspelled (have a typo).")
			null
		}
	}

	def parseAbilityArgument(value: String): AnyRef = {
		if (value.equalsIgnoreCase("null")) null
		else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("true"))
			Boolean.box(value.equalsIgnoreCase("true"))
		else if (value.endsWith("F"))
			Float.box(
				try {value.substring(0, value.length - 1).toFloat}
				catch {case e: Exception => 0F}
			)
		else if (value.endsWith("D"))
			Double.box(
				try {value.substring(0, value.length - 1).toDouble}
				catch {case e: Exception => 0D}
			)
		else if (value.endsWith("B"))
			Byte.box(
				try {value.substring(0, value.length - 1).toByte}
				catch {case e: Exception => 0.toByte}
			)
		else if (value.endsWith("S"))
			Short.box(
				try {value.substring(0, value.length - 1).toShort}
				catch {case e: Exception => 0.toShort}
			)
		else if (value.endsWith("L"))
			Long.box(
				try {value.substring(0, value.length - 1).toLong}
				catch {case e: Exception => 0.toLong}
			)
		else
			try {Int.box(value.substring(0, value.length - 1).toInt)}
			catch {case e: Exception => value}
	}

}
