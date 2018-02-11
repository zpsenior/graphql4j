package graphql4j.operation;

import java.util.ArrayList;
import java.util.List;

import graphql4j.Token;
import graphql4j.TokenReader;
import graphql4j.exception.TokenException;

public class GraphQLReader extends TokenReader{
	
	public GraphQLReader(GraphQLParser parser)throws Exception{
		super(parser);
	}
	
	public List<Token> filter(List<Token> tokens){
		List<Token> tmpTokens = new ArrayList<Token>();
		for(int i = 0; i < tokens.size(); i++){
			Token t = tokens.get(i);
			int type = t.getType();
			if(type == Token.TOKEN_TYPE_COMMENT || type == Token.TOKEN_TYPE_DELIMIT){
				continue;
			}
			tmpTokens.add(t);
		}
		return tmpTokens;
	}
	
	public boolean checkDirective(String str)throws Exception{
		Token t = readToken();
		if(str.equals(t.getName()) && Token.TOKEN_TYPE_DIRECTIVE == t.getType()){
			return true;
		}
		forward(-1);
		return false;
	}
	
	public boolean lookDirective(String str)throws Exception{
		Token t = readToken();
		forward(-1);
		if(str.equals(t.getName()) && Token.TOKEN_TYPE_DIRECTIVE == t.getType()){
			return true;
		}
		return false;
	}
	
	public boolean lookDirective()throws Exception{
		Token t = readToken();
		forward(-1);
		if(Token.TOKEN_TYPE_DIRECTIVE == t.getType()){
			return true;
		}
		return false;
	}
	
	public final void readDirective(String str)throws Exception{
		Token t = readToken();
		if(str.equals(t.getName()) && Token.TOKEN_TYPE_DIRECTIVE== t.getType()){
			return ;
		}
		throw new TokenException("unexpect.token", t);
	}
	
	public final String readDirective()throws Exception{
		Token t = readToken();
		if(Token.TOKEN_TYPE_DIRECTIVE== t.getType()){
			return t.getName();
		}
		throw new TokenException("unexpect.token", t);
	}
	
	public boolean lookVar()throws Exception{
		Token t = readToken();
		forward(-1);
		if(Token.TOKEN_TYPE_VARIABLE == t.getType()){
			return true;
		}
		return false;
	}
	
	public boolean checkVar()throws Exception{
		Token t = readToken();
		if(Token.TOKEN_TYPE_VARIABLE == t.getType()){
			return true;
		}
		forward(-1);
		return false;
	}
	
	public String readVar()throws Exception{
		Token t = readToken();
		if(Token.TOKEN_TYPE_VARIABLE == t.getType()){
			return t.getName();
		}
		throw new TokenException("unexpect.token", t);
	}
}
