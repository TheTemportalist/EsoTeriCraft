package temportalist.esotericraft.galvanization.common.entity.ai

import net.minecraft.entity.EntityCreature
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.{EnumFacing, EnumHand}
import temportalist.esotericraft.api.galvanize.ai.{AIEmpty, EntityAIEmpty}
import temportalist.origin.api.common.lib.Vect

/**
  *
  * Created by TheTemportalist on 5/19/2016.
  *
  * @author TheTemportalist
  */
@AIEmpty(name = "Deposit Items")
class EntityAIItemDeposit(
		private val owner: EntityCreature
) extends EntityAIHelper
		with IEntityAIInventory
		with IEntityAIOrigin
		with EntityAIEmpty
		with IEntityAIFlyCheck {

	private val speed: Double = 1D

	this.setMutexBits(EnumAIMutex.EVERYTHING_OKAY)
	this.checkCanFly(this.owner)

	override def initWith(infoStack: ItemStack): Unit = {

		if (infoStack.hasTagCompound && infoStack.getTagCompound.hasKey("origin")) {
			val origin = Vect.readFrom(infoStack.getTagCompound, "origin")
			val face = EnumFacing.values()(infoStack.getTagCompound.getInteger("face_ordinal"))
			this.setOrigin(origin, face)
		}

	}

	override def shouldExecute(): Boolean = {
		if (this.getOriginPosition == null) return false

		this.getOriginPosition.getTile(this.owner.getEntityWorld) match {
			case inv: IInventory => // Block at target pos is an inventory
			case _ =>
				return false
		}

		// Check if there is SOMETHING being held
		for (hand <- EnumHand.values())
			if (this.owner.getHeldItem(hand) != null) {
				return true
			}

		false
	}

	override def updateTask(): Unit = {

		var hasItem = false
		for (hand <- EnumHand.values()) {
			if (this.owner.getHeldItem(hand) != null) hasItem = true
		}
		if (!hasItem) return

		val position = this.getPosition
		val ownerDistanceToInventory = (new Vect(this.owner) - position).length
		if (ownerDistanceToInventory > 1.25D)
			this.moveEntityTowards(this.owner,
				position.x, position.y, position.z,
				this.speed, this.getCanFly)
		else {
			this.depositItems()
		}
	}

	final def depositItems(): Unit = {
		this.getOriginPosition.getTile(this.owner.getEntityWorld) match {
			case toInv: IInventory =>
				var fromStack: ItemStack = null
				for (hand <- EnumHand.values()) {
					fromStack = this.owner.getHeldItem(hand)
					if (fromStack != null) {
						fromStack = this.transferStackTo(fromStack.copy(), toInv)
						this.owner.setHeldItem(hand, fromStack)
					}
				}
			case _ =>
		}
	}

}
