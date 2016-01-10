package temportalist.esotericraft.common

import net.minecraft.entity.ai.{EntityAIBase, EntityAIFleeSun, EntityAIRestrictSun}
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.{Entity, EntityLiving}
import net.minecraftforge.event.entity.living.LivingSpawnEvent
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.eventhandler.Event.Result
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.{Mod, ObfuscationReflectionHelper, SidedProxy}
import temportalist.esotericraft.api.ApiEsotericraft
import temportalist.esotericraft.common.extended.EsotericPlayer
import temportalist.esotericraft.common.init.ModBlocks
import temportalist.esotericraft.common.world.{StructureNexus, WorldEsoteric}
import temportalist.origin.api.common.resource.{IModDetails, IModResource}
import temportalist.origin.foundation.common.IMod
import temportalist.origin.foundation.common.proxy.IProxy
import temportalist.origin.foundation.common.register.Registry

import scala.collection.mutable.ListBuffer

/**
  * The main class for the mod
  * Created by TheTemportalist on 12/31/2015.
  */
@Mod(modid = EsoTeriCraft.MOD_ID, name = EsoTeriCraft.MOD_NAME,
	version = EsoTeriCraft.MOD_VERSION, modLanguage = "scala",
	guiFactory = EsoTeriCraft.proxyClient,
	dependencies = ""
)
object EsoTeriCraft extends IMod with IModResource {

	// ~~~~~~~~~~~ Mod Setup ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final val MOD_ID = "esotericraft"
	final val MOD_NAME = "EsoTeriCraft"
	final val MOD_VERSION = "1.0.0"

	override def getModID: String = this.MOD_ID

	override def getModVersion: String = this.MOD_VERSION

	override def getModName: String = this.MOD_NAME

	override def getDetails: IModDetails = this

	// ~~~~~~~~~~~ Proxy ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	final val proxyClient = "temportalist.esotericraft.client.ProxyClient"
	final val proxyServer = "temportalist.esotericraft.server.ProxyServer"

	@SidedProxy(clientSide = proxyClient, serverSide = proxyServer)
	var proxy: IProxy = null

	// ~~~~~~~~~~~ Inits ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	@Mod.EventHandler
	def preInit(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(this, event, this.proxy, ModOptions, ModBlocks,
			WorldEsoteric, StructureNexus)
		Registry.registerExtendedPlayer(ApiEsotericraft.KEY_EXTENDED,
			classOf[EsotericPlayer], deathPersistence = true)

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event, this.proxy)
	}

	@Mod.EventHandler
	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event, this.proxy)
	}

	@SubscribeEvent
	def checkSpawn(event: LivingSpawnEvent.CheckSpawn): Unit = {
		val world = event.world
		if (world.getBiomeGenForCoords(event.entityLiving.getPosition) == WorldEsoteric.biomeUmbra) {
			event.entityLiving match {
				case mob: EntityMob =>
					this.setEntityLightResistant(mob)
					event.setResult(Result.ALLOW)
				case _ =>
					event.setResult(Result.DENY)
			}
		}
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// TODO Move these functions:

	def setEntityLightResistant(entity: EntityLiving): Unit = {
		try {
			// set immune to fire (isImmuneToFire)
			ObfuscationReflectionHelper.setPrivateValue(
				classOf[Entity], entity, Boolean.box(true), 51)

			// strip fire AI
			val tasks = entity.tasks.taskEntries
			val tasksToRemove = ListBuffer[EntityAIBase]()
			Array[Class[_ <: EntityAIBase]](
				classOf[EntityAIFleeSun], classOf[EntityAIRestrictSun]
			).foreach(taskClass => {
				for (i <- 0 until tasks.size())
					if (taskClass.isAssignableFrom(tasks.get(i).action.getClass))
						tasksToRemove += tasks.get(i).action
			})
			tasksToRemove.foreach(task => entity.tasks.removeTask(task))
		}
		catch {
			case e: Exception => e.printStackTrace()
		}
	}

}
