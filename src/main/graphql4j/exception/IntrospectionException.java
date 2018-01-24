package graphql4j.exception;

public class IntrospectionException extends Exception {

	private static final long serialVersionUID = -3306003149865644635L;
	
	public IntrospectionException(String msg, Object... args){
		super(msg);
	}

}
