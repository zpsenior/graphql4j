package graphql4j.engine;

public interface ParamValueBinder {
	
	Object find(String name)throws Exception;
}
