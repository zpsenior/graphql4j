package graphql4j.operation;

import graphql4j.Token;
import graphql4j.exception.TransformException;
import graphql4j.type.ScalarType;
import graphql4j.type.Type;


public class ParamConst extends ParamValue {
	
	private String value;
	private int tokenType;
	
	public ParamConst(Token t)throws Exception {
		this.tokenType = t.getType();
		this.value = t.getName();
	}

	public Object getValue(Type tp)throws Exception {
		if(!(tp instanceof ScalarType)){
			throw new TransformException("not.match.scalar.type");
		}
		ScalarType type = (ScalarType)tp;
		return type.parseValue(value);
	}
	
	public int getTokenType() {
		return tokenType;
	}

	@Override
	public void toString(StringBuffer sb) {
		if(tokenType == Token.TOKEN_TYPE_STRING){
			sb.append("'");
			sb.append(value);
			sb.append("'");
		}else{
			sb.append(value);
		}
	}
}
