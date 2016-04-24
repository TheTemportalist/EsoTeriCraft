package temportalist.esotericraft.main.server

import java.util

import net.minecraft.command.{CommandBase, ICommandSender}
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import temportalist.esotericraft.main.common.EsoTeriCraft
import temportalist.origin.api.common.IModDetails
import temportalist.origin.foundation.server.Command

/**
  *
  * Created by TheTemportalist on 4/23/2016.
  *
  * @author TheTemportalist
  */
object CommandETC extends Command {

	override def getDetails: IModDetails = EsoTeriCraft

	override def getCommandName: String = "esoteric"

	override def execute(server: MinecraftServer, sender: ICommandSender,
			args: Array[String]): Unit = {
		if (args.length < 1) {
			this.wrongUsage()
			return
		}

		/*
		-1          0       1       2       3
		esoteric    spell   player  add     name
		esoteric    spell   player  remove  name

		 */

		args(0) match {
			case "spell" =>
				if (args.length < 4) {
					this.wrongUsage("spell")
					return
				}

				val player = server.getPlayerList.getPlayerByUsername(args(1))

				args(2) match {
					case "add" =>
						val spellName = args(3)

					case "remove" =>
						val spellName = args(3)

					case _ => this.wrongUsage("spell")
				}
			case _ =>
		}

	}

	override def getTabCompletionOptions(server: MinecraftServer, sender: ICommandSender,
			args: Array[String], pos: BlockPos): util.List[String] = {
		args.length match {
			case 1 => CommandBase.getListOfStringsMatchingLastWord(args, "spell")
			case 2 =>
				if (Seq("spell").contains(args(1))) {
					CommandBase.getListOfStringsMatchingLastWord(args, "add", "remove")
				}
				else super.getTabCompletionOptions(server, sender, args, pos)
			case 3 =>
				if (Seq("spell").contains(args(1)) &&
						Seq("add", "remove").contains(args(2))) {
					CommandBase.getListOfStringsMatchingLastWord(args, server.getPlayerList.getAllUsernames:_*)
				}
				else super.getTabCompletionOptions(server, sender, args, pos)
			case 4 =>
				if (Seq("spell").contains(args(1)) &&
						Seq("add", "remove").contains(args(2))) {
					CommandBase.getListOfStringsMatchingLastWord(args, this.getSpellNames:_*)
				}
				else super.getTabCompletionOptions(server, sender, args, pos)
			case _ => super.getTabCompletionOptions(server, sender, args, pos)
		}
	}

	def getSpellNames: Seq[String] = {
		Seq()
	}

}
