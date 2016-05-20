package temportalist

package esotericraft
package galvanization
package common

import net.minecraft.entity.EntityLivingBase
import net.minecraft.nbt.NBTBase
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import temportalist.esotericraft.api.galvanize.IAbility
import temportalist.esotericraft.api.init.IEsoTeriCraft
import temportalist.esotericraft.api.init.IEsoTeriCraft.PluginEsoTeriCraft
import temportalist.esotericraft.galvanization.common.capability.HandlerPlayerGalvanize
import temportalist.esotericraft.galvanization.common.entity.ai.AILoader
import temportalist.esotericraft.galvanization.common.entity.emulator.ability.AbilityLoader
import temportalist.esotericraft.galvanization.common.init.{ModEntities, ModItems}
import temportalist.esotericraft.galvanization.server.CommandSetPlayerModel
import temportalist.origin.foundation.common.modTraits.IHasCommands
import temportalist.origin.foundation.common.registers.{OptionRegister, Register}
import temportalist.origin.foundation.common.{IProxy, ModBase}
import temportalist.origin.foundation.server.ICommand

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  *
  * Created by TheTemportalist on 5/5/2016.
  *
  * @author TheTemportalist
  */
@Mod(modid = Galvanize.MOD_ID, name = Galvanize.MOD_NAME, version = Galvanize.MOD_VERSION,
	modLanguage = "scala",
	guiFactory = Galvanize.proxyClient,
	dependencies = "required-after:Forge;" + "required-after:origin;" +
			"required-after:esotericraft;"
)
object Galvanize extends ModBase with IHasCommands {

	private var plugin: Plugin = _

	@PluginEsoTeriCraft
	class Plugin extends IEsoTeriCraft {

		override def onCreated(): Unit = plugin = this

	}

	// ~~~~~~~~~~ Details & Proxy ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final val MOD_ID = "esoteric" + "galvanization"
	final val MOD_NAME = "Esoteric " + "Galvanization"
	final val MOD_VERSION = "@MOD_VERSION@"
	final val proxyClient = "temportalist.esotericraft.galvanization.client.ProxyClient"
	final val proxyServer = "temportalist.esotericraft.galvanization.server.ProxyServer"

	/**
	  *
	  * @return A mod's ID
	  */
	override def getModId: String = this.MOD_ID

	/**
	  *
	  * @return A mod's name
	  */
	override def getModName: String = this.MOD_NAME

	/**
	  *
	  * @return A mod's version
	  */
	override def getModVersion: String = this.MOD_VERSION

	@SidedProxy(clientSide = this.proxyClient, serverSide = this.proxyServer)
	var proxy: IProxy = _

	override def getProxy: IProxy = this.proxy

	// ~~~~~~~~~~ Inits ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def getOptions: OptionRegister = Options

	override def getRegisters: Seq[Register] = Seq(ModItems, ModEntities)

	@Mod.EventHandler
	def preInit(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(event)

		this.registerNetwork()
		HandlerPlayerGalvanize.init(this, "PlayerGalvanize")

		AbilityLoader.preInit(event)
		AILoader.preInit(event)
		FetchResources.runMorph()
		FetchResources.runGalvanize()

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event)

	}

	@Mod.EventHandler
	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)

	}

	override def getCommands: Seq[ICommand] = Seq(CommandSetPlayerModel)

	// ~~~~~~~~~~ Entity Abilities ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	val MAP_STRING_to_CLASS_ABILITIES = mutable.Map[String, Class[_ <: IAbility[_ <: NBTBase]]]()
	val MAP_CLASS_to_ABILITIES = mutable
			.Map[Class[_ <: EntityLivingBase], Array[IAbility[_ <: NBTBase]]]()

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
			Galvanize.log("[Abilities] Ability named \'" + name +
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
