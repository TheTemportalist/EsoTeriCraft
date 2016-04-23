package temportalist.esotericraft.api;

/**
 *
 * Created by TheTemportalist on 4/23/2016.
 * @author TheTemportalist
 */
public interface IEsotericPlayer {

	void impart(EsotericModule module);

	boolean canImpart(EsotericModule module);

	boolean hasKnowledgeOf(EsotericModule module);

	void switchSpell(boolean increment);

	void setSpell(int index, Spell spell);

}
