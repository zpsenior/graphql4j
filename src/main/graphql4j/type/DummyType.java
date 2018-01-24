package graphql4j.type;

public class DummyType extends Type {
	
	private String name;
	
	public DummyType(String name){
		super("dummy");
		this.name = name;
	}

	@Override
	public void toSDL(StringBuffer sb) {
		sb.append(name);
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean compatible(Type t) {
		if(t instanceof DummyType){
			return name.equals(((DummyType)t).name);
		}
		return false;
	}

}
