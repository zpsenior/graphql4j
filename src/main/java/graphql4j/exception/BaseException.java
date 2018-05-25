package graphql4j.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseException extends Exception {

	private static final long serialVersionUID = -1848756210897561572L;
	
	private static Logger log = LogManager.getLogger(BaseException.class);
	
	BaseException(String msg){
		super(msg);
	}
	
	protected void logError(String msg, Object... args) {
		StringBuffer sb = new StringBuffer();
		sb.append(msg);
		for(Object arg : args) {
			sb.append(" ");
			sb.append(arg);
		}
		log.error(sb.toString());
	}

}
