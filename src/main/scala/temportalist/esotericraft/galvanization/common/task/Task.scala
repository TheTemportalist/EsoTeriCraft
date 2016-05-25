package temportalist.esotericraft.galvanization.common.task

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import temportalist.esotericraft.api.galvanize.ai.EntityAIEmpty

/**
  *
  * Created by TheTemportalist on 5/24/2016.
  *
  * @author TheTemportalist
  */
class Task(private val world: World) extends ITask with INBTCreator {

	private var position: BlockPos = null
	private var face: EnumFacing = null

	private var aiModID: String = null
	private var aiName: String = null
	private var aiDisplayName: String = null
	private var aiClass: Class[_ <: EntityAIEmpty] = null
	private var aiInstance: EntityAIEmpty = null

	// ~~~~~~~~~~ Getters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def getModID: String = this.aiModID

	override def getName: String = this.aiName

	override def getWorld: World = this.world

	override def getPosition: BlockPos = this.position

	override def getFace: EnumFacing = this.face

	// ~~~~~~~~~~ Setters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def setPosition(position: BlockPos, face: EnumFacing): Unit = {
		this.position = position
		this.face = face
	}

	override def setInfoAI(modid: String, name: String, displayName: String,
			classAI: Class[_ <: EntityAIEmpty]): Unit = {
		this.aiModID = modid
		this.aiName = name
		this.aiDisplayName = displayName
		this.aiClass = classAI
		this.createInstanceOfAI()
	}

	private def createInstanceOfAI(): Unit = {
		if (this.aiClass == null) this.aiInstance = null
		else {
			// TODO
		}
	}

	// ~~~~~~~~~~ Logic ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	override def onSpawn(world: World, pos: BlockPos, face: EnumFacing): Unit = {}

	override def onUpdateServer(): Unit = {

	}

	override def onBreak(world: World, pos: BlockPos, face: EnumFacing): Unit = {}

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
			if (tagAI.hasKey("displayName")) this.aiDisplayName = tagAI.getString("displayName")
			if (tagAI.hasKey("class"))
				try {
					this.aiClass = Class.forName(tagAI.getString("class"))
							.asInstanceOf[Class[_ <: EntityAIEmpty]]
					this.createInstanceOfAI()
				}
				catch { case e: Exception => }
		}

	}

}
