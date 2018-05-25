package graphql4j;

import java.util.List;

import graphql4j.exception.TokenException;

public abstract class TokenReader {

	private List<Token> tokens;
	
	public TokenReader(TokenParser parser)throws Exception{
		this.tokens = filter(parser.getTokens());
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
	
	private int currentPos = -1;
	
	public void forward(int deep){
		int pos = currentPos + deep;
		if(pos >= tokens.size()|| pos < -1){
			return;
		}
		currentPos = pos;
	}
	
	public Token lookahead(int deep){
		int pos = currentPos + deep;
		if(pos >= tokens.size()|| pos < 0){
			return null;
		}
		return tokens.get(pos);
	}
	
	protected void reset(){
		currentPos = -1;
	}
	
	protected Token read(){
		Token t = lookahead(1);
		if(t != null){
			currentPos++;
		}
		return t;
	}
	
	public Token readToken()throws Exception{
		Token t = read();
		if(t == null){
			return Token.END;
		}
		return t;
	}
	
	public abstract List<Token> filter(List<Token> tokens);
	
	public final void readName(String str)throws Exception{
		Token t = readToken();
		if(str.equals(t.getName()) && Token.TOKEN_TYPE_NAME == t.getType()){
			return ;
		}
		throw new TokenException("unexpect.token", t);
	}
	
	public final String readName()throws Exception{
		Token t = readToken();
		if(Token.TOKEN_TYPE_NAME == t.getType()){
			return t.getName();
		}
		throw new TokenException("unexpect.token", t);
	}
	
	public final String readNumber()throws Exception{
		Token t = readToken();
		if(Token.TOKEN_TYPE_NUMBER == t.getType()){
			return t.getName();
		}
		throw new TokenException("unexpect.token", t);
	}
	
	public final String readStr()throws Exception{
		Token t = readToken();
		if(Token.TOKEN_TYPE_STRING == t.getType()){
			return t.getName();
		}
		throw new TokenException("unexpect.token", t);
	}
	
	public final void readPunctuator(String str)throws Exception{
		Token t = readToken();
		if(str.equals(t.getName()) && Token.TOKEN_TYPE_PUNCTUATOR == t.getType()){
			return ;
		}
		throw new TokenException("unexpect.token", t);
	}
	
	public final boolean lookName(String str)throws Exception{
		Token t = readToken();
		forward(-1);
		if(str.equals(t.getName()) && Token.TOKEN_TYPE_NAME == t.getType()){
			return true;
		}
		return false;
	}
	
	public final boolean lookName()throws Exception{
		Token t = readToken();
		forward(-1);
		if(Token.TOKEN_TYPE_NAME == t.getType()){
			return true;
		}
		return false;
	}
	
	public final boolean lookStr()throws Exception{
		Token t = readToken();
		forward(-1);
		if(Token.TOKEN_TYPE_STRING == t.getType()){
			return true;
		}
		return false;
	}
	
	public final boolean lookNumber()throws Exception{
		Token t = readToken();
		forward(-1);
		if(Token.TOKEN_TYPE_NUMBER == t.getType()){
			return true;
		}
		return false;
	}
	
	public final boolean lookPunctuator(String str)throws Exception{
		Token t = readToken();
		forward(-1);
		if(str.equals(t.getName()) && Token.TOKEN_TYPE_PUNCTUATOR == t.getType()){
			return true;
		}
		return false;
	}

	public final boolean checkName(String str)throws Exception{
		Token t = readToken();
		if(str.equals(t.getName()) && Token.TOKEN_TYPE_NAME == t.getType()){
			return true;
		}
		forward(-1);
		return false;
	}

	public final boolean checkPunctuator(String str)throws Exception{
		Token t = readToken();
		if(str.equals(t.getName()) && Token.TOKEN_TYPE_PUNCTUATOR == t.getType()){
			//log.debug(t.getPos() + ":" + t.getName());
			return true;
		}
		forward(-1);
		return false;
	}
}
