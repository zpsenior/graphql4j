package graphql4j.type;

public interface TypeFinder {
	public Type getTypeByClass(Class<?> cls, boolean input)throws Exception;
}
