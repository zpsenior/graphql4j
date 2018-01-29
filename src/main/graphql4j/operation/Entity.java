package graphql4j.operation;

import graphql4j.JObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import graphql4j.exception.ExecuteException;
import graphql4j.type.Argument;
import graphql4j.type.ObjectField;


public class Entity extends JObject implements Comparable<Entity>{
	private String name;
	private String alias;
	private Param[] params = new Param[0];
	private Entity[] children = new Entity[0];
	private Fragment inlineFragment = null;
	private boolean fragment = false;
	
	public Entity(String name, Fragment fr){
		this.name = name;
		this.alias = name;
		this.fragment = true;
		this.inlineFragment = fr;
	}
	
	public Entity(String name, String alias, Set<Param> params, Collection<Entity> entities){
		this.name = name;
		if(alias != null){
			this.alias = alias;
		}else{
			this.alias = name;
		}
		if(params != null && params.size() > 0){
			this.params = params.toArray(new Param[params.size()]);
		}
		if(entities != null && entities.size() > 0){
			this.children = entities.toArray(new Entity[entities.size()]);
		}
	}
	
	@Override
	public int compareTo(Entity o) {
		return name.compareTo(o.name);
	}

	public String getName() {
		return name;
	}

	public String getAlias() {
		return alias;
	}

	public Entity[] getChildren() {
		return children;
	}

	public Boolean isFragment() {
		return this.fragment;
	}

	public Param getParam(String name) {
		for(Param param : params){
			if(param.getName().equals(name)){
				return param;
			}
		}
		return null;
	}

	public Param[] getParams() {
		return params;
	}

	public void toString(StringBuffer sb){
		if(fragment){
			sb.append("...");
			if(inlineFragment != null){
				sb.append(" on ");
				sb.append("{");
				sb.append("\n");
				for(Entity entity : inlineFragment.getEntities()){
					entity.toString(sb);
					sb.append("\n");
				}
				sb.append("}");
			}else{
				sb.append(name);
			}
			return;
		}
		if(!alias.equals(name)){
			sb.append(alias).append(":");
		}
		sb.append(name);
		if(params != null && params.length > 0){
			sb.append("(");
			boolean first = true;
			for(Param param : params){
				if(!first){
					sb.append(", ");
				}
				param.toString(sb);
				first = false;
			}
			sb.append(")");
		}
		if(children != null && children.length > 0){
			sb.append("{");
			sb.append("\n");
			for(Entity entity : children){
				entity.toString(sb);
				sb.append("\n");
			}
			sb.append("}");
		}
	}

	public boolean hasAlias() {
		return !name.equals(alias);
	}

	public boolean hasChildren() {
		return children != null && children.length > 0;
	}

	public Map<String, Object> getParamValues(ObjectField field)throws Exception{
		Map<String, Object> values = new HashMap<String, Object>();
		Object value;
		for(Param param : params){
			ParamValue pv = param.getParamValue();
			String name = param.getName();
			Argument arg = field.getArgument(name);
			if(arg == null){
				throw new ExecuteException("can.not.find.argument", name);
			}
			value = pv.getValue(arg.getType());
			values.put(name, value);
		}
		return values;
	}
}
