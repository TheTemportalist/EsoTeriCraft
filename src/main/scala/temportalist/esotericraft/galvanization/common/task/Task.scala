package temportalist.esotericraft.galvanization.common.task

import net.minecraft.block.Block
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraft.world.World
import temportalist.esotericraft.api.galvanize.ai.{IGalvanizeTask, ITaskBoundingBox}
import temportalist.esotericraft.galvanization.common.task.core.ControllerTask

/**
  *
  * Created by TheTemportalist on 5/24/2016.
  *
  * @author TheTemportalist
  */
final class Task(private val world: World) extends ITask with INBTCreator {

	private var position: BlockPos = null
	private var face: EnumFacing = null

	private var aiModID: String = null
	private var aiName: String = null
	private var aiDisplayName: String = null
	private var aiClass: Class[_ <: IGalvanizeTask] = null

	private var aiInstance: IGalvanizeTask = null
	private var iconLocation: ResourceLocation = null

	// ~~~~~~~~~~ Getters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def getModID: String = this.aiModID

	override def getName: String = this.aiName

	override def getIconLocation: ResourceLocation = this.iconLocation

	override def getWorld: World = this.world

	override def getPosition: BlockPos = this.position

	override def getFace: EnumFacing = this.face

	override def getAI: IGalvanizeTask = {
		if (this.aiInstance == null) this.createInstanceOfAI()
		//Galvanize.log("" + this.aiInstance)
		this.aiInstance
	}

	override def isValid: Boolean = ControllerTask.getTaskAt(this.getWorld, this.getPosition, this.getFace) == this

	// ~~~~~~~~~~ Setters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def setPosition(position: BlockPos, face: EnumFacing): Unit = {
		this.position = position
		this.face = face
	}

	override def setInfoAI(modid: String, name: String, displayName: String,
			classAI: Class[_ <: IGalvanizeTask]): Unit = {
		this.aiModID = modid
		this.aiName = name
		this.createIconLocation()
		this.aiDisplayName = displayName
		this.aiClass = classAI
		this.createInstanceOfAI()
	}

	private def createIconLocation(): Unit = {
		this.iconLocation =
				if (this.aiModID == null || this.aiName == null) null
				else new ResourceLocation(this.aiModID, "textures/tasks/" + this.aiName + ".png")
	}

	private def createInstanceOfAI(): Unit = {
		if (this.aiClass == null) this.aiInstance = null
		else {
			try {
				this.aiInstance = this.aiClass.getConstructor(
					classOf[BlockPos], classOf[EnumFacing]
				).newInstance(this.getPosition, this.getFace)
				this.aiInstance match {
					case bb: ITaskBoundingBox => bb.updateBoundingBox()
					case _ =>
				}
			}
			catch {
				case e: Exception => e.printStackTrace()
			}
		}
	}

	// ~~~~~~~~~~ Logic ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def onSpawn(world: World, pos: BlockPos, face: EnumFacing): Unit = {}

	override def onBreak(world: World, pos: BlockPos, face: EnumFacing): Unit = {}

	override def onBroken(doDrop: Boolean): Unit = {
		if (doDrop) this.dropTaskItem()
	}

	def dropTaskItem(): Unit = {
		val world = this.getWorld
		if (world.isRemote) return
		Block.spawnAsEntity(world, this.getPosition,
			ControllerTask.getNewItemStackForAIClass(this.aiClass)
		)
	}

	override def onUpdateServer(): Unit = {

		this.checkBlockAtPosition()

	}

	private def checkBlockAtPosition(): Unit = {
		val state = this.getWorld.getBlockState(this.getPosition)
		if (!state.getMaterial.isSolid) this.break()
	}

	def break(): Unit = {
		ControllerTask.breakTask(this.getWorld, this.getPosition, this.getFace)
	}

	// ~~~~~~~~~~ NBT Serialization ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def serializeNBT(): NBTTagCompound = {
		val tag = this.getCompoundNew

		if (this.getPosition != null) {
			tag.setInteger("x", this.getPosition.getX)
			tag.setInteger("y", this.getPosition.getY)
			tag.setInteger("z", this.getPosition.getZ)
		}

		if (this.getFace != null)
			tag.setInteger("face", this.getFace.ordinal())

		val tagAI = new NBTTagCompound
		if (this.aiModID != null) tagAI.setString("modid", this.aiModID)
		if (this.aiName != null) tagAI.setString("name", this.aiName)
		if (this.aiDisplayName != null) tagAI.setString("displayName", this.aiDisplayName)
		if (this.aiClass != null) tagAI.setString("class", this.aiClass.getName)
		if (!tagAI.hasNoTags) tag.setTag("ai", tagAI)

		tag
	}

	override def deserializeNBT(tag: NBTTagCompound): Unit = {

		if (tag.hasKey("x") && tag.hasKey("y") && tag.hasKey("z")) {
			this.position = new BlockPos(
				tag.getInteger("x"),
				tag.getInteger("y"),
				tag.getInteger("z")
			)
		}

		if (tag.hasKey("face"))
			this.face = EnumFacing.values()(tag.getInteger("face"))

		if (tag.hasKey("ai")) {
			val tagAI = tag.getCompoundTag("ai")
			if (tagAI.hasKey("modid")) this.aiModID = tagAI.getString("modid")
			if (tagAI.hasKey("name")) this.aiName = tagAI.getString("name")
			this.createIconLocation()
			if (tagAI.hasKey("displayName")) this.aiDisplayName = tagAI.getString("displayName")
			if (tagAI.hasKey("class"))
				try {
					this.aiClass = Class.forName(tagAI.getString("class"))
							.asInstanceOf[Class[_ <: IGalvanizeTask]]
					this.createInstanceOfAI()
				}
				catch {case e: Exception =>}
		}

	}

}
