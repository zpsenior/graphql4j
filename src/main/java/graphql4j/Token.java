package graphql4j;

public class Token{
	
	public final static int TOKEN_TYPE_PUNCTUATOR = 10;
	
	public final static int TOKEN_TYPE_NAME = 1;
	
	public final static int TOKEN_TYPE_COMMENT = 2;
	
	public final static int TOKEN_TYPE_DELIMIT = 3;
	
	public final static int TOKEN_TYPE_STRING = 4;
	
	public final static int TOKEN_TYPE_BOOLEAN = 5;
	
	public final static int TOKEN_TYPE_NULL = 6;
	
	public final static int TOKEN_TYPE_NUMBER = 7;
	
	public final static int TOKEN_TYPE_VARIABLE = 8;
	
	public final static int TOKEN_TYPE_DIRECTIVE = 9;
	
	public final static int TOKEN_TYPE_END = 999;
	
	public final static Token END = new Token(TOKEN_TYPE_END);
	
	private String name;
	private int type;
	private int line;
	private int row;
	private int pos;
	
	private Token(int type){
		this.type = type;
		this.name = "";
	}
	
	public Token(int type, String name, Token t){
		this.type = type;
		this.name = name;
		this.line = t.line;
		this.row = t.row;
		this.pos = t.pos;
	}
	
	Token(int type, String name, int line, int row, int pos){
		this.type = type;
		this.name = name;
		this.line = line;
		this.row = row;
		this.pos = pos;
	}
	public String getName() {
		return name;
	}
	public int getType() {
		return type;
	}
	public int getPos() {
		return pos;
	}
	public int getLine() {
		return line;
	}
	public int getRow() {
		return row;
	}
	public String toString(){
		StringBuffer sb = new StringBuffer();
		toString(sb);
		return sb.toString();
	}
	
	public void toString(StringBuffer sb){
		switch(type){
			case TOKEN_TYPE_VARIABLE:
				sb.append("$");
				break;
			case TOKEN_TYPE_DIRECTIVE:
				sb.append("@");
				break;
			case TOKEN_TYPE_STRING:
				sb.append("\"");
				break;
			case TOKEN_TYPE_COMMENT:
				sb.append("#");
				break;
			default:
				break;
		}
		sb.append(name);
		switch(type){
			case TOKEN_TYPE_STRING:
				sb.append("\"");
				break;
			case TOKEN_TYPE_COMMENT:
				sb.append("\n");
				break;
			default:
				break;
		}
	}
}
