package graphql4j.operation;

import graphql4j.JObject;

import java.util.Collection;

public class Fragment extends JObject implements Comparable<Fragment> {
	
	public final static String INLINE_PREFIX = "__inline_fr__";

	private String name;
	private String mappingType;
	private Entity[] entities;
	
	public Fragment(String name, String mappingType, Collection<Entity> entities) {
		this.name = name;
		this.mappingType = mappingType;
		this.entities = entities.toArray(new Entity[entities.size()]);
	}

	public String getMappingType() {
		return mappingType;
	}

	public String getName() {
		return name;
	}

	public Entity[] getEntities() {
		return entities;
	}

	public boolean isInline() {
		return name.startsWith(INLINE_PREFIX);
	}

	@Override
	public void toString(StringBuffer sb) {
		sb.append("\n");
		sb.append("fragment").append(" ").append(getName());
		sb.append(" on ").append(mappingType);
		sb.append("{\n");
		for(Entity en : entities){
			en.toString(sb);
			sb.append("\n");
		}
		sb.append("}\n");
	}

	@Override
	public int compareTo(Fragment o) {
		return name.compareTo(o.name);
	}

}
