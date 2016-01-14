package temportalist.esotericraft.api;

/**
 * Created by TheTemportalist on 1/6/2016.
 */
public interface IEsotericPlayer {

	void impart(EsotericModule module);

	boolean canImpart(EsotericModule module);

	boolean hasKnowledgeOf(EsotericModule module);

	void switchSpell(boolean increment);

	void setSpell(int index, Spell spell);

}
