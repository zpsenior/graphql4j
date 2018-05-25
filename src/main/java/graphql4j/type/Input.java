package graphql4j.type;

public interface Input {
	
	public Object parseValue(Object value)throws Exception;

}
