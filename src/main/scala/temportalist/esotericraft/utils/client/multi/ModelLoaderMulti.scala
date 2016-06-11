package temportalist.esotericraft.utils.client.multi

import java.util
import javax.vecmath.{Matrix3f, Matrix4f, Vector3f, Vector4f}

import com.google.common.base.{Function, Optional}
import com.google.common.collect.{ImmutableList, ImmutableMap, Lists}
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType
import net.minecraft.client.renderer.block.model._
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.{VertexFormat, VertexFormatElement}
import net.minecraft.client.resources.IResourceManager
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraft.world.World
import net.minecraftforge.client.model.pipeline.{UnpackedBakedQuad, VertexTransformer}
import net.minecraftforge.client.model.{ICustomModelLoader, IModel, IPerspectiveAwareModel, ItemLayerModel}
import net.minecraftforge.common.model.{IModelPart, IModelState, TRSRTransformation}
import org.apache.commons.lang3.tuple.Pair
import temportalist.esotericraft.main.common.EsoTeriCraft
import temportalist.esotericraft.utils.common.Utils
import temportalist.esotericraft.utils.common.init.ItemMulti

import scala.collection.JavaConversions

/**
  *
  * Created by TheTemportalist on 6/10/2016.
  *
  * @author TheTemportalist
  */
object ModelLoaderMulti extends ICustomModelLoader {

	private val location = new ModelResourceLocation(Utils.getModId, "models/fake")

	def getLocation: ModelResourceLocation = this.location

	override def accepts(modelLocation: ResourceLocation): Boolean = modelLocation ==
			this.getLocation

	override def onResourceManagerReload(resourceManager: IResourceManager): Unit = {}

	override def loadModel(modelLocation: ResourceLocation): IModel = Model

	def getTransformsForTool(state: IModelState): ImmutableMap[TransformType, TRSRTransformation] = {
		val builder = ImmutableMap.builder[TransformType, TRSRTransformation]
		// Generic transformations for whatever state was passed
		builder.putAll(IPerspectiveAwareModel.MapWrapper.getTransforms(state))

		// override with transformations for tool items
		// found at net.minecraftforge.client.model.ForgeBlockStateV1 ln 517-538

		// default-item
		//val thirdperson = this.get(0, 3, 1, 0, 0, 0, 0.55f)
		//val firstperson = this.get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f)
		builder.put(TransformType.GROUND, this.get(0, 2, 0, 0, 0, 0, 0.5f))
		builder.put(TransformType.HEAD, this.get(0, 13, 7, 0, 180, 0, 1))
		/*
		builder.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson)
		builder.put(TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdperson))
		builder.put(TransformType.FIRST_PERSON_RIGHT_HAND, firstperson)
		builder.put(TransformType.FIRST_PERSON_LEFT_HAND, leftify(firstperson))
		*/

		// default-tool
		builder.put(TransformType.THIRD_PERSON_RIGHT_HAND, get(0, 4, 0.5f, 0, -90, 55, 0.85f))
		builder.put(TransformType.THIRD_PERSON_LEFT_HAND, get(0, 4, 0.5f, 0, 90, -55, 0.85f))
		builder.put(TransformType.FIRST_PERSON_RIGHT_HAND, get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f))
		builder.put(TransformType.FIRST_PERSON_LEFT_HAND, get(1.13f, 3.2f, 1.13f, 0, 90, -25, 0.68f))

		builder.build()
	}

	private def get(tx: Float, ty: Float, tz: Float,
			ax: Float, ay: Float, az: Float, s: Float): TRSRTransformation = {
		TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
			new Vector3f(tx / 16, ty / 16, tz / 16),
			TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)),
			new Vector3f(s, s, s),
			null
		))
	}

	/*
	private val flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null)

	private def leftify(transform: TRSRTransformation): TRSRTransformation = {
		TRSRTransformation.blockCenterToCorner(
			flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX)
		)
	}
	*/

	object Model extends IModel {

		val baseTextureLoc = new ResourceLocation(EsoTeriCraft.getModId, "items/spindle")
		var baseModel: ImmutableList[BakedQuad] = _
		var transforms: ImmutableMap[TransformType, TRSRTransformation] = null

		override def getDependencies: util.Collection[ResourceLocation] = {
			val list = Lists.newArrayList[ResourceLocation]()
			list.add(this.baseTextureLoc)
			list
		}

		override def getTextures: util.Collection[ResourceLocation] = {
			new util.ArrayList[ResourceLocation]()
		}

		override def getDefaultState: IModelState = TRSRTransformation.identity()

		override def bake(state: IModelState, format: VertexFormat,
				bakedTextureGetter: Function[ResourceLocation, TextureAtlasSprite]): IBakedModel = {
			val sprite = bakedTextureGetter.apply(this.baseTextureLoc)
			val transform = state.apply(Optional.absent[IModelPart]())
			this.baseModel = ItemLayerModel.getQuadsForSprite(0, sprite, format, transform)
			this.transforms = getTransformsForTool(state)
			Baked
		}

	}

	class BlankBakedModel extends IBakedModel {

		override def isAmbientOcclusion: Boolean = true

		override def isBuiltInRenderer: Boolean = false

		override def isGui3d: Boolean = false

		override def getParticleTexture: TextureAtlasSprite = null

		override def getItemCameraTransforms: ItemCameraTransforms = ItemCameraTransforms.DEFAULT

		override def getQuads(state: IBlockState, side: EnumFacing,
				rand: Long): util.List[BakedQuad] = {
			new util.ArrayList[BakedQuad]()
		}

		override def getOverrides: ItemOverrideList = BlankItemOverrideListImpl

	}

	object Baked extends BlankBakedModel {

		override def getOverrides: ItemOverrideList = ItemModel

	}

	class BlankItemOverrideList extends ItemOverrideList(Lists.newArrayList())

	object BlankItemOverrideListImpl extends BlankItemOverrideList

	object ItemModel extends BlankItemOverrideList {

		override def handleItemState(originalModel: IBakedModel, stack: ItemStack,
				world: World, entity: EntityLivingBase): IBakedModel = {
			// WARNING: New model being generated every render
			val activeStack = ItemMulti.getActiveStack(stack)
			val activeModel: IBakedModel =
				if (activeStack != null && !activeStack.getItem.isInstanceOf[ItemBlock]) {
					val original = Minecraft.getMinecraft.getRenderItem.getItemModelMesher.
							getItemModel(activeStack)
					original.getOverrides.handleItemState(original, activeStack, world, entity)
				}
				else null
			new BakedItemModel(activeModel)
		}

	}

	private val scale = 0.5f
	private val transformation: TRSRTransformation = new TRSRTransformation(
		new Vector3f(0, scale, 0.25F),
		null,
		new Vector3f(scale, scale, scale),
		null
	)

	class BakedItemModel(private val activeModel: IBakedModel)
			extends BlankBakedModel with IPerspectiveAwareModel {

		override def getQuads(state: IBlockState, side: EnumFacing,
				rand: Long): util.List[BakedQuad] = {

			if (this.activeModel == null) return Model.baseModel

			// WARNING: New list being generated every render
			val quads = new util.ArrayList[BakedQuad]()

			quads.addAll(Model.baseModel)

			if (this.activeModel != null) {
				val activeQuads = this.activeModel.getQuads(null, null, rand)
				if (activeQuads != null) {
					// http://gamedev.stackexchange.com/questions/28249/calculate-new-vertex-position-given-a-transform-matrix/28251#28251
					for (quad <- JavaConversions.asScalaBuffer(activeQuads)) {
						// https://github.com/SlimeKnights/Mantle/blob/273cca6465c615671b48d44af4a8a95c24174bd2/src/main/java/slimeknights/mantle/client/model/TRSRBakedModel.java#L86-L88
						val transformer = new Transformer(ModelLoaderMulti.transformation, quad.getFormat)
						quad.pipe(transformer)
						quads.add(transformer.build)
					}
				}
			}

			quads
		}

		override def handlePerspective(cameraType: TransformType): Pair[_ <: IBakedModel, Matrix4f] = {
			val renderBackground = Seq(TransformType.GUI, TransformType.GROUND).contains(cameraType)
			val model = if (renderBackground || this.activeModel == null) this else this.activeModel
			IPerspectiveAwareModel.MapWrapper.handlePerspective(model, Model.transforms, cameraType)
		}

	}

	// https://github.com/SlimeKnights/Mantle/blob/273cca6465c615671b48d44af4a8a95c24174bd2/src/main/java/slimeknights/mantle/client/model/TRSRBakedModel.java#L150
	class Transformer(
			private val transformation: TRSRTransformation,
			private val format: VertexFormat
	) extends VertexTransformer(
		new UnpackedBakedQuad.Builder(format)
	) {

		private val transformationMatrix = this.transformation.getMatrix
		private val normalTransformation = new Matrix3f()
		this.transformationMatrix.getRotationScale(this.normalTransformation)
		this.normalTransformation.invert()
		this.normalTransformation.transpose()

		override def put(element: Int, data: Float*): Unit = {
			val usage = this.parent.getVertexFormat.getElement(element).getUsage

			if (usage == VertexFormatElement.EnumUsage.POSITION && data.length >= 3) {
				val vec = new Vector4f(data(0), data(1), data(2), 1F)
				this.transformationMatrix.transform(vec)
				val data2 = new Array[Float](4)
				vec.get(data2)
				super.put(element, data2:_*)
			}
			else if (usage == VertexFormatElement.EnumUsage.NORMAL && data.length >= 3) {
				val vec = new Vector3f(data.toArray)
				this.normalTransformation.transform(vec)
				vec.normalize()
				val data2 = new Array[Float](4)
				vec.get(data2)
				super.put(element, data2:_*)
			}
			else super.put(element, data:_*)
		}

		def build: UnpackedBakedQuad = {
			this.parent.asInstanceOf[UnpackedBakedQuad.Builder].build()
		}

	}

}
