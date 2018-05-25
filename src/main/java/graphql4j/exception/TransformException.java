package graphql4j.exception;

public class TransformException extends Exception {

	private static final long serialVersionUID = -2591574124518008568L;
	
	public TransformException(String msg, Object... args){
		super(msg);
	}

}
