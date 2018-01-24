package graphql4j;

public abstract class JObject {
	public abstract void toString(StringBuffer sb);
	
	public final String toString(){
		StringBuffer sb = new StringBuffer();
		toString(sb);
		return sb.toString();
	}
}
