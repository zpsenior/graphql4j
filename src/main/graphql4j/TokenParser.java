package graphql4j;

import java.util.ArrayList;
import java.util.List;

import graphql4j.exception.TokenException;

public abstract class TokenParser {

	public static boolean isChar(int c){
		if(c >= 'A' && c <= 'Z'){
			return true;
		}
		if(c >= 'a' && c <= 'z'){
			return true;
		}
		return false;
	}
	
	public static boolean isDigitNotZero(int c){
		if(c >= '1' && c <= '9'){
			return true;
		}
		return false;
	}
	
	public static boolean isDigit(int c){
		if(c >= '0' && c <= '9'){
			return true;
		}
		return false;
	}
	
	private String ql;
	private int cpos = -1;
	private int line = 1;
	private int row = 0;
	
	private List<Token> tokens = new ArrayList<Token>();
	
	protected void addToken(int tokenType, String content){
		int len = content.length();
		Token t = new Token(tokenType, content, line, row - len, cpos - len);
		tokens.add(t);
	}
	
	protected TokenException buildException(String msg, int c){
		return new TokenException(msg, (char)c, line, row, cpos);
	}
	
	protected void replaceToken(List<Token> tokens){
		this.tokens = tokens;
	}
	
	protected List<Token> getTokens(){
		return tokens;
	}
	
	protected int getTokenCount(){
		return tokens.size();
	}

	protected Token getToken(int pos){
		return tokens.get(pos);
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < tokens.size(); i++){
			Token t = tokens.get(i);
			t.toString(sb);
			sb.append(" ").append(t.getType());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	protected TokenParser(String ql){
		this.ql = ql;
	}
	
	private int oldrow = -1;
	
	protected int readChar(){
		if(cpos + 1 < ql.length()){
			cpos++;
			int c = ql.charAt(cpos);
			if(c == '\n'){
				line++;
				oldrow = row;
				row = 1;
			}
			row++;
			return c;
		}
		return -1;
	}
	
	protected void retreat(){
		if(cpos - 1 >= 0){
			cpos --;
			if(ql.charAt(cpos) == '\n'){
				line--;
				row = oldrow;
			}else{
				row--;
			}
		}
	}
}
