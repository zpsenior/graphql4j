package graphql4j.type;

import graphql4j.JObject;

public abstract class Type extends JObject {
	
	private String bindClass;
	
	Type(String bindClass){
		this.bindClass = bindClass;
	}
	
	public abstract void toSDL(StringBuffer sb);
	
	public abstract String getName();
	
	public abstract boolean compatible(Type t);
	
	public boolean equals(Object o){
		if(o == null || !(o instanceof Type)){
			return false;
		}
		String name = ((Type)o).getName();
		return getName().equals(name);
	}
	
	public final void toString(StringBuffer sb){
		toSDL(sb);
	}

	public final String getBindClass() {
		return bindClass;
	}
	
	
}
