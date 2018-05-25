package graphql4j.exception;

public class BindException extends BaseException {

	private static final long serialVersionUID = -8841068141149075251L;
	
	public BindException(String msg, Object... args){
		super(msg);
		logError(msg, args);
	}
}
