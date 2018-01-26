package graphql4j.exception;

public class ExecuteException extends Exception {
	
	private static final long serialVersionUID = 8875579600169831216L;

	public ExecuteException(String msg, Object... args){
		super(msg);
	}
}
