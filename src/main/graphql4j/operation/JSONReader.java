package graphql4j.operation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graphql4j.Token;
import graphql4j.TokenReader;
import graphql4j.exception.TokenException;

public class JSONReader extends TokenReader {
	
	public JSONReader(JSONParser parser)throws Exception{
		super(parser);
	}

	@Override
	public List<Token> filter(List<Token> tokens) {
		List<Token> tmpTokens = new ArrayList<Token>();
		for(int i = 0; i < tokens.size(); i++){
			Token t = tokens.get(i);
			int type = t.getType();
			if(type == Token.TOKEN_TYPE_DELIMIT){
				continue;
			}
			tmpTokens.add(t);
		}
		return tmpTokens;
	}
	
	private final boolean lookNull()throws Exception{
		Token t = readToken();
		forward(-1);
		if(Token.TOKEN_TYPE_NULL == t.getType()){
			return true;
		}
		return false;
	}
	
	private final boolean lookBoolean()throws Exception{
		Token t = readToken();
		forward(-1);
		if(Token.TOKEN_TYPE_BOOLEAN == t.getType()){
			return true;
		}
		return false;
	}
	
	private final Boolean readBoolean()throws Exception{
		Token t = readToken();
		if(Token.TOKEN_TYPE_BOOLEAN == t.getType()){
			return new Boolean(t.getName());
		}
		throw new TokenException("unexpect.token", t);
	}
	
	private Map<String, Object> buildObject()throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		String name = readName();
		checkPunctuator(":");
		Object obj = readObject();
		map.put(name, obj);
		while(checkPunctuator(",")){
			name = readName();
			checkPunctuator(":");
			obj = readObject();
			map.put(name, obj);
		}
		checkPunctuator("}");
		return map;
	}
	
	private List<Object> buildArray()throws Exception{
		List<Object> list = new ArrayList<Object>();
		Object obj = readObject();
		list.add(obj);
		while(checkPunctuator(",")){
			obj = readObject();
			list.add(obj);
		}
		checkPunctuator("]");
		return list;
	}
	
	public Object readObject() throws Exception{
		if(checkPunctuator("{")){
			return buildObject();
		}else if(checkPunctuator("[")){
			return buildArray();
		}else if(lookStr()){
			return readStr();
		}else if(lookBoolean()){
			return readBoolean();
		}else if(lookNull()){
			return null;
		}else if(lookNumber()){
			String str = readNumber();
			str = str.toLowerCase();
			if(str.indexOf('e') > 0){
				return new BigDecimal(str);
			}else if(str.indexOf('.') > 0){
				return new Double(str);
			}else{
				Long v = new Long(str);
				if(Math.abs(v) < Long.MAX_VALUE){
					return v.intValue();
				}
				return v;
			}
			
		}
		throw new TokenException("unexpect.token", readToken());
	}
}
