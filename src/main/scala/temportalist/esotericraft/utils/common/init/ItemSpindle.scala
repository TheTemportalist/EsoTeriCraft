package temportalist.esotericraft.utils.common.init

import net.minecraft.entity.Entity
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.player.EntityPlayer.SleepResult
import net.minecraft.entity.player.EntityPlayer.SleepResult._
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.play.server.SPacketUseBed
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.{ActionResult, EnumHand}
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.{PlayerSleepInBedEvent, SleepingLocationCheckEvent}
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import net.minecraftforge.fml.common.eventhandler.{Event, SubscribeEvent}
import temportalist.esotericraft.main.common.util.NBT
import temportalist.esotericraft.utils.common.Utils
import temportalist.origin.api.common.IModDetails
import temportalist.origin.api.common.item.ItemBase
import temportalist.origin.api.common.lib.Vect

/**
  *
  * Created by TheTemportalist on 4/26/2016.
  *
  * @author TheTemportalist
  */
class ItemSpindle(mod: IModDetails) extends ItemBase(mod) {

	Utils.registerHandler(this)

	private val KEY_SPAWN = "spawn"
	private val KEY_POSITION = "position"
	private val KEY_SLEEPING = "sleeping"

	override def onItemRightClick(itemStackIn: ItemStack, worldIn: World, playerIn: EntityPlayer,
			hand: EnumHand): ActionResult[ItemStack] = {

		if (worldIn.isRemote) return getActionSUCCESS(itemStackIn)

		this.putToSleep(worldIn, playerIn, itemStackIn)

		getActionSUCCESS(itemStackIn)
	}

	private def putToSleep(world: World, player: EntityPlayer, stackSpindle: ItemStack): Boolean = {

		val status = this.canPlayerSleep(world, player, player.getPosition)
		status match {
			case NOT_POSSIBLE_NOW =>
				this.addChatTranslate(player, "tile.bed.noSleep")
				return false
			case NOT_SAFE =>
				this.addChatTranslate(player, "tile.bed.notSafe")
				return false
			case OK =>
			case _ => return false
		}

		if (player.isRiding) player.dismountRidingEntity()

		ObfuscationReflectionHelper.setPrivateValue(classOf[EntityPlayer], player,
			true, "sleeping", "field_71083_bS")
		ObfuscationReflectionHelper.setPrivateValue(classOf[EntityPlayer], player,
			0, "sleepTimer", "field_71076_b")

		val tag = new NBTTagCompound
		tag.setTag(KEY_SPAWN, NBT.storePlayerSpawn(player))
		tag.setTag(KEY_POSITION, NBT.storeEntityPosition(player))
		tag.setBoolean(KEY_SLEEPING, true)
		stackSpindle.setTagCompound(tag)

		world.updateAllPlayersSleepingFlag()

		player match {
			case mp: EntityPlayerMP =>
				val sleepPacket = new SPacketUseBed(mp, player.getPosition)
				//mp.getServerWorld.getEntityTracker.sendToAllTrackingEntity(mp, sleepPacket)
				mp.connection.sendPacket(sleepPacket)
			case _ =>
		}

		true
	}

	private def canPlayerSleep(world: World, player: EntityPlayer, pos: BlockPos): SleepResult = {

		val event = new PlayerSleepInBedEvent(player, pos)
		MinecraftForge.EVENT_BUS.post(event)
		if (event.getResultStatus != null) return event.getResultStatus

		if (!world.isRemote) {
			if (player.isPlayerSleeping || !player.isEntityAlive) return OTHER_PROBLEM
			if (!world.provider.isSurfaceWorld) return NOT_POSSIBLE_HERE
			if (world.isDaytime) return NOT_POSSIBLE_NOW
			if (!world.getEntitiesWithinAABB(classOf[EntityMob],new AxisAlignedBB(
				pos.getX - 8, pos.getY - 5, pos.getZ - 8, pos.getX + 8, pos.getY + 5, pos.getZ + 8)
			).isEmpty) return NOT_SAFE

			if (!this.isNotSuffocatingPosition(world, pos) || !this.isSolidishGround(world, pos.down())) {
				this.addChatTranslateMod(player, "invalid_ground")
				return OTHER_PROBLEM
			}
		}

		OK
	}

	private def addChatTranslate(player: EntityPlayer, key: String): Unit = {
		player.addChatComponentMessage(new TextComponentTranslation(key))
	}

	private def addChatTranslateMod(player: EntityPlayer, key: String): Unit = {
		this.addChatTranslate(player,
			this.mod.getModId + "." + this.getClass.getSimpleName + "." + key)
	}

	private def canSleepEvent(world: World, player: EntityPlayer, pos: BlockPos): SleepResult = {
		if (!world.provider.isSurfaceWorld) return NOT_POSSIBLE_HERE
		if (world.isDaytime) return NOT_POSSIBLE_NOW

		val event = new PlayerSleepInBedEvent(player, pos)
		MinecraftForge.EVENT_BUS.post(event)
		if (event.getResultStatus != null) return event.getResultStatus

		if (!world.getEntitiesWithinAABB(classOf[EntityMob],new AxisAlignedBB(
			pos.getX - 8, pos.getY - 5, pos.getZ - 8, pos.getX + 8, pos.getY + 5, pos.getZ + 8)
		).isEmpty) return NOT_SAFE

		OK
	}

	private def isNotSuffocatingPosition(world: World, pos: BlockPos): Boolean = {
		world.isAirBlock(pos) || this.getCollisionFor(world, pos) == null
	}

	private def getCollisionFor(world: World, pos: BlockPos): AxisAlignedBB = {
		world.getBlockState(pos).getCollisionBoundingBox(world, pos)
	}

	private def isSolidishGround(world: World, pos: BlockPos): Boolean = {
		this.getCollisionFor(world, pos) match {
			case aabb: AxisAlignedBB =>
				val dist = (aabb.maxX - aabb.minX, aabb.maxY - aabb.minY, aabb.maxZ - aabb.minZ)
				dist._1 >= 0.5 &&  dist._2 >= 0.5 && dist._3 >= 0.5
			case _ => false
		}
	}

	override def onUpdate(itemStack: ItemStack, world: World, entity: Entity, itemSlot: Int,
			isSelected: Boolean): Unit = {
		entity match {
			case player: EntityPlayer =>
				if (player.isPlayerSleeping) return
				if (!itemStack.hasTagCompound) return

				//return

				val tag = itemStack.getTagCompound
				if (!tag.getBoolean(KEY_SLEEPING)) return
				tag.removeTag(KEY_SLEEPING)

				if (tag.hasKey(KEY_SPAWN)) {
					val pos = NBT.get[BlockPos](tag, KEY_SPAWN)
					if (pos != null) player.setSpawnChunk(pos, false, player.dimension)
					tag.removeTag(KEY_SPAWN)
				}

				if (tag.hasKey(KEY_POSITION)) {
					val pos = NBT.get[Vect](tag, KEY_POSITION)
					if (pos != null) player.setPosition(pos.x, pos.y, pos.z)
					tag.removeTag(KEY_POSITION)
				}

				itemStack.setTagCompound(null)
			case _ =>
		}
	}

	@SubscribeEvent
	def onSleepCheck(event: SleepingLocationCheckEvent): Unit = {
		val player = event.getEntityPlayer
		val stackHandMain = player.getHeldItemMainhand
		val stackHandOff = player.getHeldItemOffhand
		if (this.isSpindle(stackHandMain) || this.isSpindle(stackHandOff))
			event.setResult(Event.Result.ALLOW)
	}

	def isSpindle(stack: ItemStack): Boolean = stack != null && stack.getItem == this

}
