package graphql4j.exception;

public class BindException extends Exception {

	private static final long serialVersionUID = -8841068141149075251L;
	
	public BindException(String msg, Object... args){
		super(msg);
	}
}
