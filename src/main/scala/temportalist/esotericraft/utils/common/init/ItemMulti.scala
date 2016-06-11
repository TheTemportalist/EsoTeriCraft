package temportalist.esotericraft.utils.common.init

import com.google.common.collect.Multimap
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{ActionResult, EnumActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World
import temportalist.esotericraft.main.common.EsoTeriCraft
import temportalist.origin.api.common.item.ItemBase

/**
  *
  * Notable Issues:
  *     - Tinker's Construct AOE items do not render extra bounding box and breaking texture. See:
  *         [[https://github.com/SlimeKnights/TinkersConstruct/blob/f6dd1ea51486cfd1ae5b39ca8021e93cfa1413bb/src/main/java/slimeknights/tconstruct/tools/client/RenderEvents.java#L55]]
  *
  * Created by TheTemportalist on 6/10/2016.
  * @author TheTemportalist
  */
class ItemMulti extends ItemBase(EsoTeriCraft) {

	def getActiveStack(stack: ItemStack): ItemStack = ItemMulti.getActiveStack(stack)

	def setActiveStack(stack: ItemStack, active: ItemStack): Unit = {
		if (stack == null || active == null) return
		if (!stack.hasTagCompound) stack.setTagCompound(new NBTTagCompound)
		stack.getTagCompound.setTag("activeStack", active.serializeNBT())
	}

	override def onItemRightClick(stack: ItemStack, worldIn: World,
			playerIn: EntityPlayer, hand: EnumHand): ActionResult[ItemStack] = {
		val activeStack = this.getActiveStack(stack)
		if (activeStack == null) super.onItemRightClick(stack, worldIn, playerIn, hand)
		else {
			val ret = activeStack.useItemRightClick(worldIn, playerIn, hand)
			this.setActiveStack(stack, ret.getResult)
			new ActionResult[ItemStack](ret.getType, stack)
		}
	}

	override def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos,
			hand: EnumHand, facing: EnumFacing,
			hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
		val activeStack = this.getActiveStack(stack)
		if (activeStack == null)
			super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ)
		else activeStack.onItemUse(playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ)
	}

	override def getStrVsBlock(stack: ItemStack, state: IBlockState): Float = {
		val activeStack = this.getActiveStack(stack)
		if (activeStack == null) super.getStrVsBlock(stack, state)
		else activeStack.getStrVsBlock(state)
	}

	override def onBlockStartBreak(stack: ItemStack, pos: BlockPos,
			player: EntityPlayer): Boolean = {
		val activeStack = this.getActiveStack(stack)
		if (activeStack == null) super.onBlockStartBreak(stack, pos, player)
		else activeStack.getItem.onBlockStartBreak(activeStack, pos, player)
	}

	override def onLeftClickEntity(stack: ItemStack, player: EntityPlayer,
			entity: Entity): Boolean = {
		val activeStack = this.getActiveStack(stack)
		if (activeStack == null) super.onLeftClickEntity(stack, player, entity)
		else activeStack.getItem.onLeftClickEntity(activeStack, player, entity)
	}

	override def onEntitySwing(entityLiving: EntityLivingBase, stack: ItemStack): Boolean = {
		val activeStack = this.getActiveStack(stack)
		if (activeStack == null) super.onEntitySwing(entityLiving, stack)
		else activeStack.getItem.onEntitySwing(entityLiving, activeStack)
	}

	override def hitEntity(stack: ItemStack, target: EntityLivingBase,
			attacker: EntityLivingBase): Boolean = {
		val activeStack = this.getActiveStack(stack)
		if (activeStack == null) super.hitEntity(stack, target, attacker)
		else {
			attacker match {
				case player: EntityPlayer =>
					activeStack.hitEntity(target, player)
					false
				case _ => activeStack.getItem.hitEntity(activeStack, target, attacker)
			}
		}
	}

	override def getAttributeModifiers(slot: EntityEquipmentSlot,
			stack: ItemStack): Multimap[String, AttributeModifier] = {
		val activeStack = this.getActiveStack(stack)
		if (activeStack == null) super.getAttributeModifiers(slot, stack)
		else activeStack.getAttributeModifiers(slot)
	}

	override def hasEffect(stack: ItemStack): Boolean = {
		val activeStack = this.getActiveStack(stack)
		if (activeStack == null) super.hasEffect(stack)
		else activeStack.hasEffect
	}

	override def getHarvestLevel(stack: ItemStack, toolClass: String): Int = {
		val activeStack = this.getActiveStack(stack)
		if (activeStack == null) super.getHarvestLevel(stack, toolClass)
		else activeStack.getItem.getHarvestLevel(activeStack, toolClass)
	}

	override def onUpdate(stack: ItemStack,
			worldIn: World, entityIn: Entity,
			itemSlot: Int, isSelected: Boolean): Unit = {
		val activeStack = this.getActiveStack(stack)
		if (activeStack == null) super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected)
		else activeStack.getItem.onUpdate(activeStack, worldIn, entityIn, itemSlot, isSelected)
	}

	override def onBlockDestroyed(stack: ItemStack, worldIn: World, state: IBlockState,
			pos: BlockPos, entity: EntityLivingBase): Boolean = {
		val activeStack = this.getActiveStack(stack)
		if (activeStack == null) super.onBlockDestroyed(stack, worldIn, state, pos, entity)
		else {
			entity match {
				case player: EntityPlayer =>
					activeStack.onBlockDestroyed(worldIn, state, pos, player)
					false
				case _ =>
					activeStack.getItem.onBlockDestroyed(activeStack, worldIn, state, pos, entity)
			}
		}
	}

	// TODO remove these:

	override def onEntityItemUpdate(thisEntity: EntityItem): Boolean = {
		if (thisEntity.getEntityItem.hasTagCompound &&
				thisEntity.getEntityItem.getTagCompound.hasKey("activeStack"))
			return super.onEntityItemUpdate(thisEntity)

		val thisStack = thisEntity.getEntityItem
		val entities = thisEntity.getEntityWorld.getEntitiesWithinAABB(classOf[EntityItem],
			thisEntity.getEntityBoundingBox.expand(2, 2, 2), null
		)
		if (!entities.isEmpty) {
			val thatEntity = entities.get(0)
			if (thatEntity != null) {
				val thatStack = thatEntity.getEntityItem
				if (thatStack != null && thatStack.getItem != ModItems.multi) {
					if (!thisStack.hasTagCompound) thisStack.setTagCompound(new NBTTagCompound)
					thisStack.getTagCompound.setTag("activeStack", thatStack.serializeNBT())
					thisEntity.setEntityItemStack(thisStack)
					thatEntity.setDead()
					//return true // prevent further update checks
				}
			}
		}

		super.onEntityItemUpdate(thisEntity)
	}

}
object ItemMulti {

	def getActiveStack(stack: ItemStack): ItemStack = {
		if (stack == null || !stack.hasTagCompound || !stack.getTagCompound.hasKey("activeStack"))
			return null
		ItemStack.loadItemStackFromNBT(stack.getTagCompound.getCompoundTag("activeStack"))
	}

	def createWith(stack: ItemStack): ItemStack = {
		val retStack = new ItemStack(ModItems.multi)
		retStack.setTagCompound(new NBTTagCompound)
		retStack.getTagCompound.setTag("activeStack", stack.serializeNBT())
		retStack
	}

}
