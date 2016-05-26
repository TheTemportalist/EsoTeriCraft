package temportalist.esotericraft.api.galvanize.ai;

/**
 * Created by TheTemportalist on 5/26/2016.
 *
 * @author TheTemportalist
 */
public enum EnumTaskType {

	/**
	 * Interacts with something in the world. i.e. grabbing items, chopping trees, fishing, etc.
	 */
	WORLD_INTERACTION,
	/**
	 * Moves when certain criteria in the entity are correct. i.e. holding a certain item
	 */
	STATUS_RELIANT,
	/**
	 * Basic movement (think 'Maintaining position'). i.e. swimming
	 */
	MOVEMENT_PASSIVE,
	/**
	 * Movement targeted at a certain spot (think 'I have a destination'). i.e. follow a player
	 */
	MOVEMENT_ACTIVE,
	/**
	 * Any task that doesnt fit into one of the categories above.
	 * Recommendation: use this until a PR or issue can be submitted at
	 * https://github.com/TheTemportalist/EsoTeriCraft/issues
	 */
	OTHER

}
