package temportalist.esotericraft.galvanization.common.task.ai.world

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.entity.EntityCreature
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.init.Blocks
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.server.SPacketBlockChange
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraft.world.WorldSettings.GameType
import net.minecraft.world.{World, WorldServer}
import net.minecraftforge.common.util.FakePlayer
import net.minecraftforge.common.{ForgeHooks, MinecraftForge}
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import temportalist.esotericraft.api.galvanize.ai.GalvanizeTask
import temportalist.esotericraft.api.init.Details
import temportalist.esotericraft.galvanization.common.task.ai.interfaces.IFakePlayer

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  *
  * Created by TheTemportalist on 5/26/2016.
  *
  * @author TheTemportalist
  */
@GalvanizeTask(modid = Details.MOD_ID,
	name = "harvestTree",
	displayName = "Harvest (Tree)"
)
class TaskHarvestTree(
		pos: BlockPos, face: EnumFacing
) extends TaskHarvest(pos, face) with IFakePlayer {

	override def isBlockValid(world: World, pos: BlockPos, state: IBlockState): Boolean = {
		state.getBlock.isWood(world, pos)
	}

	override def harvestState(world: World, pos: BlockPos, state: IBlockState,
			entity: EntityCreature): Unit = {
		world match {
			case worldServer: WorldServer =>

				val fakePlayer = this.getFakePlayer(worldServer)
				entity.swingArm(EnumHand.MAIN_HAND)
				this.harvestTree(world, pos, fakePlayer)

			case _ =>
		}
	}

	def harvestTree(world: World, pos: BlockPos, fakePlayer: FakePlayer): Unit = {
		new TaskHarvestTree.RunMe(world, pos, fakePlayer, 1)
	}

}
object TaskHarvestTree {

	/**
	  * https://github.com/SlimeKnights/TinkersConstruct/blob/f6dd1ea51486cfd1ae5b39ca8021e93cfa1413bb/src/main/java/slimeknights/tconstruct/tools/item/LumberAxe.java#L193
	  */
	class RunMe(
			private val world: World,
			private val start: BlockPos,
			private val player: EntityPlayer,
			private val blocksPerTick: Int
	) {

		val blocksToBreak = mutable.Queue[BlockPos]()
		val blocksBroken = ListBuffer[BlockPos]()

		this.blocksToBreak += this.start

		MinecraftForge.EVENT_BUS.register(this)

		def finish(): Unit = {
			MinecraftForge.EVENT_BUS.unregister(this)
		}

		@SubscribeEvent
		def onWorldTick(event: WorldTickEvent): Unit = {
			if (event.side.isClient) {
				this.finish()
				return
			}

			var pos: BlockPos = null
			var state: IBlockState = null
			var blocksRemainingInTick = this.blocksPerTick
			while (blocksRemainingInTick > 0) {

				if (this.blocksToBreak.isEmpty) {
					this.finish()
					return
				}

				pos = this.blocksToBreak.dequeue()
				state = this.world.getBlockState(pos)

				if (state.getBlock.isWood(this.world, pos)) {

					// Get the neighbors
					for (face <- EnumFacing.HORIZONTALS) {
						val posNeighbor = pos.offset(face)
						if (!this.blocksToBreak.contains(posNeighbor))
							this.blocksToBreak += posNeighbor
					}

					// Support for Acacia Trees (diagonal wood)
					for {
						x <- 0 until 3
						z <- 0 until 3
					} {
						val posNeighbor = pos.add(-1 + x, 1, -1 + z)
						if (!this.blocksToBreak.contains(posNeighbor))
							this.blocksToBreak += posNeighbor
					}

					breakBlock(this.world, pos, this.player, stateIn = state)
					blocksRemainingInTick -= 1
				}

			}

		}

	}

	def breakBlock(world: World, pos: BlockPos, player: EntityPlayer, stateIn: IBlockState = null): Unit = {
		val state = if (stateIn == null) world.getBlockState(pos) else stateIn
		val block = state.getBlock

		if (!ForgeHooks.canHarvestBlock(block, player, world, pos))
			return

		if (player.capabilities.isCreativeMode) {
			block.onBlockHarvested(world, pos, state, player)
			if (block.removedByPlayer(state, world, pos, player, false))
				block.onBlockDestroyedByPlayer(world, pos, state)
			if (!world.isRemote)
				player.asInstanceOf[EntityPlayerMP].connection.sendPacket(
					new SPacketBlockChange(world, pos))
			return
		}

		if (!world.isRemote) {
			player match {
				case playerMP: EntityPlayerMP =>
					val xp = onBlockBreakEvent(world,
						playerMP.interactionManager.getGameType, playerMP, pos)
					if (xp == -1) return

					block.onBlockHarvested(world, pos, state, player)
					if (block.removedByPlayer(state, world, pos, player, true)) {
						block.onBlockDestroyedByPlayer(world, pos, state)
						block.harvestBlock(world, player, pos, state,
							world.getTileEntity(pos), null)
						block.dropXpOnBlockBreak(world, pos, xp)
					}

					if (playerMP.connection != null)
						playerMP.connection.sendPacket(new SPacketBlockChange(world, pos))
				case _ =>
			}
		}
		else {
			world.playEvent(2001, pos, Block.getStateId(state))
			if (block.removedByPlayer(state, world, pos, player, true)) {
				block.onBlockDestroyedByPlayer(world, pos, state)
			}

			sendUpdateDiggingPacket(pos)

		}

	}

	def onBlockBreakEvent(world: World, gameType: GameType, playerMP: EntityPlayerMP,
			pos: BlockPos): Int = {
		//ForgeHooks.onBlockBreakEvent(world, gameType, playerMP, pos)
		var preCancelEvent = false
		if (gameType.isCreative && playerMP.getHeldItemMainhand != null &&
				playerMP.getHeldItemMainhand.getItem.isInstanceOf[ItemSword]) {
			preCancelEvent = true
		}

		if (gameType.isAdventure) {
			if (gameType == GameType.SPECTATOR)
				preCancelEvent = true
			if (!playerMP.isAllowEdit) {
				val stack = playerMP.getHeldItemMainhand
				if (stack == null || !stack.canDestroy(world.getBlockState(pos).getBlock))
					preCancelEvent = true
			}
		}

		if (world.getTileEntity(pos) == null && playerMP.connection != null) {
			val packet = new SPacketBlockChange(world, pos)
			packet.blockState = Blocks.AIR.getDefaultState
			playerMP.connection.sendPacket(packet)
		}

		val state = world.getBlockState(pos)
		val event = new BlockEvent.BreakEvent(world, pos, state, playerMP)
		event.setCanceled(preCancelEvent)
		MinecraftForge.EVENT_BUS.post(event)

		if (event.isCanceled) {
			if (playerMP.connection != null)
				playerMP.connection.sendPacket(new SPacketBlockChange(world, pos))

			val tile = world.getTileEntity(pos)
			if (tile != null && playerMP.connection != null) {
				val pkt = tile.getUpdatePacket
				if (pkt != null)
					playerMP.connection.sendPacket(pkt)
			}

		}

		if (event.isCanceled) -1 else event.getExpToDrop
	}

	@SideOnly(Side.CLIENT)
	def sendUpdateDiggingPacket(pos: BlockPos): Unit = {
		Minecraft.getMinecraft.getConnection.sendPacket(
			new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
				pos, Minecraft.getMinecraft.objectMouseOver.sideHit
			)
		)
	}

}
