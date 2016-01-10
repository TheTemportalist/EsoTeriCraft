package temportalist.esotericraft.api;

/**
 * Created by TheTemportalist on 1/6/2016.
 */
public interface IEsotericPlayer {

	void impart(EsotericraftModule module);

	boolean canImpart(EsotericraftModule module);

	boolean hasKnowledgeOf(EsotericraftModule module);

}
