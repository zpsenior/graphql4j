package graphql4j.operation;

import graphql4j.Token;
import graphql4j.TokenParser;

import java.util.ArrayList;
import java.util.List;

public class GraphQLParser extends TokenParser {
	
	private final static char[] punctuators = new char[]{
		'!', '(', ')', ':', '=', '[', ']', '{', '|', '}', '.'
	};
	
	private final static String[] mpunctuators = new String[]{
		"..."
	};
	
	private final static char[] delims = new char[]{
		' ', '\t', '\r', '\n', '\b', '\f', ','
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
	
	public GraphQLParser(String json)throws Exception{
		super(json);
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
	
	private void readDirective(){
		sb.setLength(0);
		while(true){
			int c = readChar();
			if(isChar(c) || isDigit(c)|| c == '_'){
				sb.append((char)c);
				continue;
			}
			retreat();
			String str = sb.toString();
			addToken(Token.TOKEN_TYPE_DIRECTIVE, str);
			return;
		}
	}
	
	private void readVariable(){
		sb.setLength(0);
		while(true){
			int c = readChar();
			if(isChar(c) || isDigit(c)|| c == '_'){
				sb.append((char)c);
				continue;
			}
			retreat();
			String str = sb.toString();
			addToken(Token.TOKEN_TYPE_VARIABLE, str);
			return;
		}
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
	
	private void readStr(int delim){
		int c;
		sb.setLength(0);
		while((c = readChar()) != delim){
			sb.append((char)c);
		}
		addToken(Token.TOKEN_TYPE_STRING, sb.toString());
	}
	
	private void readComment(){
		int c;
		sb.setLength(0);
		while((c = readChar()) != '\n'){
			sb.append((char)c);
		}
		addToken(Token.TOKEN_TYPE_COMMENT, sb.toString());
	}
	
	private void parse()throws Exception{
		while(true){
			int c = readChar();
			//System.out.println((char)c);
			if(c == -1){
				break;
			}
			if(isDelim(c)){
				readDelimit(c);
				continue;
			}else if(isPunctuator(c)){
				readPunctuator(c);
				continue;
			}else if(c == '$'){
				readVariable();
				continue;
			}else if(c == '@'){
				readDirective();
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
			}else if(c == '#'){
				readComment();
				continue;
			}else if(c == '"' || c == '\''){
				readStr(c);
				continue;
			}
			throw buildException("error.char", c);
		}
		List<Token> tmpTokens = new ArrayList<Token>();
		for(int i = 0; i < getTokenCount(); i++){
			Token t = getToken(i);
			if(t.getType() == Token.TOKEN_TYPE_PUNCTUATOR){
				Token mk = checkMPunctuator(i);
				if(mk != null){
					tmpTokens.add(mk);
					i += mk.getName().length() - 1;
					continue;
				}
			}
			tmpTokens.add(t);
		}
		replaceToken(tmpTokens);
	}
	
	private Token checkMPunctuator(int pos){
		Token t = getToken(pos);
		String s = t.getName();
		for(String mp : mpunctuators){
			if(mp.startsWith(s)){
				if(compareMPunctuator(pos, mp)){
					t = new Token(Token.TOKEN_TYPE_PUNCTUATOR, mp, t);
					return t;
				}
			}
		}
		return null;
	}
	
	private boolean compareMPunctuator(int start, String mp) {
		int len = mp.length();
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < len; i++){
			int pos = start + i;
			if(pos >= getTokenCount()){
				return false;
			}
			Token t = getToken(pos);
			if(t.getType() != Token.TOKEN_TYPE_PUNCTUATOR){
				return false;
			}
			sb.append(t.getName());
		}
		return mp.equals(sb.toString());
	}
}
