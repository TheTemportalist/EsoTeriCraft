package temportalist.esotericraft.galvanization.server

import java.util

import net.minecraft.command.{CommandBase, ICommandSender}
import net.minecraft.entity.{EntityList, EntityLivingBase}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import temportalist.esotericraft.galvanization.common.Galvanize
import temportalist.esotericraft.galvanization.common.capability.HelperGalvanize
import temportalist.origin.api.common.IModDetails
import temportalist.origin.foundation.server.Command

import scala.collection.JavaConversions
import scala.collection.mutable.ListBuffer

/**
  *
  * Created by TheTemportalist on 5/7/2016.
  *
  * @author TheTemportalist
  */
object CommandSetPlayerModel extends Command {

	override def getDetails: IModDetails = Galvanize

	override def getCommandName: String = "setModel"

	override def execute(server: MinecraftServer, sender: ICommandSender,
			args: Array[String]): Unit = {
		// /setModel <player> <entity>
		if (args.length < 2) {
			wrongUsage()
			return
		}

		server.getPlayerList.getPlayerByUsername(args(0)) match {
			case player: EntityPlayer =>
				val entityName = args(1)

				if (entityName == "None") {
					HelperGalvanize.get(player).clearEntityState(player.getEntityWorld)
				}
				else {
					val map = EntityList.NAME_TO_CLASS

					if (!map.containsKey(entityName)) {
						wrongUsage("invalidEntity")
						return
					}

					val entityClass = map.get(entityName)
					if (classOf[EntityLivingBase].isAssignableFrom(entityClass)) {
						HelperGalvanize.get(player)
								.setEntityState(entityName, player.getEntityWorld)
					}
					else wrongUsage("notLivingEntity")
				}

			case _ => wrongUsage("invalidPlayer")
		}

	}

	override def getTabCompletionOptions(server: MinecraftServer, sender: ICommandSender,
			args: Array[String], pos: BlockPos): util.List[String] = {
		args.length match {
			case 1 => CommandBase.getListOfStringsMatchingLastWord(args, server.getPlayerList.getAllUsernames:_*)
			case 2 =>
				val buffer = ListBuffer[String]("None")
				buffer ++= JavaConversions.collectionAsScalaIterable(
					EntityList.CLASS_TO_NAME.values()
				)
				CommandBase.getListOfStringsMatchingLastWord(args, buffer:_*)
			case _ => super.getTabCompletionOptions(server, sender, args, pos)
		}
	}

}
