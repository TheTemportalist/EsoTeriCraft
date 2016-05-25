package temportalist.esotericraft.api.galvanize.ai;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by TheTemportalist on 5/22/2016.
 *
 * @author TheTemportalist
 */
public interface EntityAIHelperObj {

	Class<? extends IGalvanizeTask> getClassAI();

	ActionResult<ItemStack> onItemRightClick(
			ItemStack itemStack, World world, EntityPlayer player, EnumHand hand
	);

	EnumActionResult onItemUse(
			ItemStack itemStack, EntityPlayer player, World world, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ
	);

}
