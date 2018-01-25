package graphql4j.operation;

import graphql4j.Token;
import graphql4j.TokenParser;

public class JSONParser extends TokenParser {
	
	private final static char[] delims = new char[]{
		' ', '\t', '\r', '\n', '\b', '\f'
	};
	
	private final static char[] punctuators = new char[]{
		',', ':', '[', ']', '{', '}'
	};
	
	private StringBuffer sb = new StringBuffer();
	
	private boolean isDelim(int c){
		for(char ch : delims){
			if(c == ch){
				return true;
			}
		}
		return false;
	}
	
	private boolean isPunctuator(int c){
		for(char ch : punctuators){
			if(c == ch){
				return true;
			}
		}
		return false;
	}
	
	public JSONParser(String ql)throws Exception{
		super(ql);
		parse();
		//System.out.println(this);
	}

	private void readDelimit(int start){
		sb.setLength(0);
		sb.append((char)start);
		while(true){
			int c = readChar();
			if(isDelim(c)){
				sb.append((char)c);
				continue;
			}
			break;
		}
		retreat();
		addToken(Token.TOKEN_TYPE_DELIMIT, sb.toString());
	}

	private void readPunctuator(int punctuator){
		addToken(Token.TOKEN_TYPE_PUNCTUATOR, String.valueOf((char)punctuator));
	}
	
	private void readStr(int delim){
		int c;
		sb.setLength(0);
		while((c = readChar()) != delim){
			sb.append((char)c);
		}
		addToken(Token.TOKEN_TYPE_STRING, sb.toString());
	}
	
	private void readName(int start){
		sb.setLength(0);
		sb.append((char)start);
		while(true){
			int c = readChar();
			if(isChar(c) || isDigit(c)|| c == '_'){
				sb.append((char)c);
				continue;
			}
			retreat();
			String str = sb.toString();
			if("true".equals(str)||"false".equals(str)){
				addToken(Token.TOKEN_TYPE_BOOLEAN, str);
			}else if("null".equals(str)){
				addToken(Token.TOKEN_TYPE_NULL, str);
			}else{
				addToken(Token.TOKEN_TYPE_NAME, str);
			}
			return;
		}
	}
	
	private void readZeroNumber()throws Exception{
		sb.setLength(0);
		sb.append('0');
		int c = readChar();
		retreat();
		if(isDelim(c)||isPunctuator(c)){
			String str = sb.toString();
			addToken(Token.TOKEN_TYPE_NUMBER, str);
			return;
		}
		throw buildException("unexpect.number", c);
	}
	
	private void readNumber(int start)throws Exception{
		int c;
		sb.setLength(0);
		sb.append((char)start);
		while(true){
			c = readChar();
			if(isDigit(c)){
				sb.append((char)c);
				continue;
			}
			break;
		}
		if(c == '.'){
			sb.append((char)c);
			while(true){
				c = readChar();
				if(isDigit(c)){
					sb.append((char)c);
					continue;
				}
				break;
			}
		}
		if(c == 'E' || c == 'e'){
			sb.append((char)c);
			c = readChar();
			if(c != '+' && c != '-' ){
				throw buildException("unexpect.char", c);
			}
			sb.append((char)c);
			while(true){
				c = readChar();
				if(isDigit(c)){
					sb.append((char)c);
					continue;
				}
				break;
			}
		}
		retreat();
		String str = sb.toString();
		addToken(Token.TOKEN_TYPE_NUMBER, str);
		return;
	}
	
	private void parse()throws Exception{
		while(true){
			int c = readChar();
			if(c == -1){
				break;
			}
			if(isDelim(c)){
				readDelimit(c);
				continue;
			}else if(isPunctuator(c)){
				readPunctuator(c);
				continue;
			}else if(isChar(c)){
				readName(c);
				continue;
			}else if(isDigitNotZero(c)||c == '+' || c == '-'){
				readNumber(c);
				continue;
			}else if(c == '0'){
				readZeroNumber();
				continue;
			}else if(c == '"' || c == '\''){
				readStr(c);
				continue;
			}
			throw buildException("error.char", c);
		}
	}
}
