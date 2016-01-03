package com.temportalist.esotericraft.common.block

import java.util

import net.minecraft.block.properties.{PropertyInteger, PropertyDirection}
import net.minecraft.util.EnumFacing

/**
  * Created by TheTemportalist on 1/3/2016.
  */
object StateProperties {

	val FACING_HORIZONTAL = PropertyDirection.create("facing_horizontal", EnumFacing.Plane.HORIZONTAL)
	val FACING_AXIS_X = PropertyDirection.create("facing_axis_x",
		util.Arrays.asList(EnumFacing.EAST, EnumFacing.WEST))
	val FACING_AXIS_Y = PropertyDirection.create("facing_axis_y",
		util.Arrays.asList(EnumFacing.UP, EnumFacing.DOWN))
	val FACING_AXIS_Z = PropertyDirection.create("facing_axis_z",
		util.Arrays.asList(EnumFacing.SOUTH, EnumFacing.NORTH))

	def createPropInt(name: String, min: Int, max: Int): PropertyInteger = {
		PropertyInteger.create(name, min, max)
	}

}
