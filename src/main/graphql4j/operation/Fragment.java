package graphql4j.operation;

import graphql4j.JObject;
import graphql4j.exception.TransformException;
import graphql4j.type.InterfaceType;
import graphql4j.type.ObjectType;
import graphql4j.type.Type;
import graphql4j.type.UnionType;

import java.util.Collection;

public class Fragment extends JObject implements Comparable<Fragment> {
	
	public final static String INLINE_PREFIX = "__inline_fr__";

	private String name;
	private Type mappingType;
	private Entity[] entities;
	
	public Fragment(String name, Type type, Collection<Entity> entities)throws Exception {
		if (!(type instanceof ObjectType) && !(type instanceof InterfaceType)
				&& !(type instanceof UnionType)) {
			throw new TransformException(
					"fragment.type.must.be.object.or.interface.or.union", type.getName());
		}
		this.name = name;
		this.mappingType = type;
		this.entities = entities.toArray(new Entity[entities.size()]);
	}

	public Type getMappingType() {
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
		sb.append(" on ").append(mappingType.getName());
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
