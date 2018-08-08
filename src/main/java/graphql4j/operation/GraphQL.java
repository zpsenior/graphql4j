package graphql4j.operation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import graphql4j.engine.ParamValueBinder;
import graphql4j.exception.BindException;
import graphql4j.exception.IntrospectionException;
import graphql4j.type.Argument;
import graphql4j.type.EnumType;
import graphql4j.type.GraphQLSchema;
import graphql4j.type.InterfaceType;
import graphql4j.type.ArrayType;
import graphql4j.type.ObjectField;
import graphql4j.type.ObjectType;
import graphql4j.type.Type;
import graphql4j.type.UnionType;

public class GraphQL {

	public final static int TYPE_QUERY = 1;
	public final static int TYPE_MUTATION = 2;
	public final static int TYPE_SUBSCRIPTION = 3;

	public class Operation {
		private int operationType;
		private String operation;
		private QueryArgument[] arguments;
		private Entity[] entities;

		Operation(int operationType, String operation,
				Collection<QueryArgument> arguments, Collection<Entity> entities) {
			this.operationType = operationType;
			this.operation = operation;
			if (arguments != null && arguments.size() > 0) {
				this.arguments = arguments.toArray(new QueryArgument[arguments
						.size()]);
			}
			this.entities = entities.toArray(new Entity[entities.size()]);
		}

		public void clearValues() throws Exception {
			if(arguments == null) {
				return;
			}
			for (QueryArgument argument : arguments) {
				argument.clearValue();
			}
		}

		public String[] getArgumentNames() {
			if(arguments == null) {
				return null;
			}
			String[] names = new String[arguments.length];
			for (int i = 0; i < arguments.length; i++) {
				QueryArgument argument = arguments[i];
				names[i] = argument.getName();
			}
			return names;
		}

		public QueryArgument getArgument(String name) {
			if(arguments == null) {
				return null;
			}
			for (int i = 0; i < arguments.length; i++) {
				QueryArgument argument = arguments[i];
				if (argument.getName().equals(name)) {
					return argument;
				}
				;
			}
			return null;
		}
		
		public void bindValues(ParamValueBinder binder) throws Exception {
			if(arguments == null) {
				return;
			}
			for (QueryArgument argument : arguments) {
				String varName = argument.getName();
				Object value = binder.find(varName);
				argument.bindValue(value);
			}
		}

		public void bindValue(String name, Object value) throws Exception {
			if(arguments != null) {
				for (QueryArgument argument : arguments) {
					String varName = argument.getName();
					if (varName.equals(name)) {
						argument.bindValue(value);
						return;
					}
				}
			}
			throw new IntrospectionException("can.not.find.variable.value",
					name);
		}

		public int getOperationType() {
			return operationType;
		}

		public String getOperation() {
			return operation;
		}

		public Entity[] getEntities() {
			return entities;
		}

		public QueryArgument[] getArguments() {
			return arguments;
		}

		public void toString(StringBuffer sb) {
			switch (operationType) {
			case TYPE_QUERY:
				sb.append("query");
				break;
			case TYPE_MUTATION:
				sb.append("mutation");
				break;
			case TYPE_SUBSCRIPTION:
				sb.append("subscription");
				break;
			default:
				break;
			}
			if (operation != null) {
				sb.append(operation);
			}
			if (arguments != null) {
				sb.append("(");
				boolean first = true;
				for (QueryArgument arg : arguments) {
					if (!first) {
						sb.append(", ");
					}
					sb.append("$");
					arg.toString(sb);
					first = false;
				}
				sb.append(")");
			}
			if (entities != null) {
				sb.append("{");
				sb.append("\n");
				for (Entity en : entities) {
					en.toString(sb);
					sb.append("\n");
				}
				sb.append("}");
			}
		}

		public Fragment[] getFragments() {
			return fragments.values().toArray(new Fragment[fragments.size()]);
		}

		public Fragment getFragment(String name) {
			return fragments.get(name);
		}
	}

	private Map<String, Operation> operations = new HashMap<String, Operation>();
	private Map<String, Fragment> fragments = new HashMap<String, Fragment>();

	public GraphQL() {
	}

	public int getFragmentCount() {
		return fragments.size();
	}

	protected void addFragments(String name, Fragment fr)throws Exception{
		if(fragments.containsKey(name)){
			throw new BindException("duplicate.fragment.name", name);
		}
		fragments.put(name, fr);
	}

	protected void addOperation(int operationType, String name, Collection<QueryArgument> arguments, Collection<Entity> entities)throws Exception {
		if (operations.containsKey(name)) {
			throw new IntrospectionException("duplicate.operation.name", name);
		}
		Operation operation = new Operation(operationType, name, arguments,
				entities);
		operations.put(name, operation);
	}

	public Operation getOperation(String name) {
		return operations.get(name);
	}

	public Operation getDefaultOperation() {
		return operations.get("");
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

		if (fragments != null) {
			for (String name : fragments.keySet()) {
				Fragment fr = fragments.get(name);
				if (fr.isInline()) {
					continue;
				}
				fr.toString(sb);
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	protected void introspection(GraphQLSchema schema) throws Exception {
		ObjectType qt = schema.getQueryType();
		ObjectType mt = schema.getMutationType();
		for(String name : operations.keySet()){
			Operation operation = operations.get(name);
			ObjectType ot = null;
			if(operation.getOperationType() == TYPE_QUERY) {
				ot = qt;
			}else if(operation.getOperationType() == TYPE_MUTATION) {
				ot = mt;
			}
			for (Entity entity : operation.getEntities()) {
				checkEntity(schema, ot, entity);
			}
		}
		if (fragments != null) {
			checkFragments(schema);
		}
	}

	private void checkEntity(GraphQLSchema schema, Type type, Entity entity)
			throws Exception {

		while (type instanceof ArrayType) {
			type = ((ArrayType) type).getBaseType();
		}

		String name = entity.getName();
		if (entity.isFragment()) {
			Fragment fr = fragments.get(name);
			Type tp = fr.getMappingType();
			if (!type.compatible(tp) && !tp.compatible(type)) {
				throw new IntrospectionException("type.is.not.compatible",
						type.getName(), tp.getName());
			}
			return;
		}
		ObjectField field = null;

		if (type instanceof ObjectType) {
			field = ((ObjectType) type).getField(name);
		} else if (type instanceof InterfaceType) {
			field = ((InterfaceType) type).getField(name);
		} else {
			throw new IntrospectionException("unsupport.type", type.getName(), entity.getName());
		}
		if (field == null) {
			throw new IntrospectionException("not.find.field.name.in.type",
					name, type.getName());
		}
		checkParamArgumentType(entity, field);
		if (!entity.hasChildren()) {
			return;
		}
		for (Entity child : entity.getChildren()) {
			checkEntity(schema, field.getType(), child);
		}
	}

	public void checkChildren(Entity entity, Type type) throws Exception {
		if (!entity.hasChildren()) {
			return;
		}
		if (type instanceof ArrayType) {
			type = ((ArrayType) type).getBaseType();

		} else if (type instanceof ObjectType) {
			ObjectType ot = (ObjectType) type;
			checkChildren(entity, ot);

		} else if (type instanceof InterfaceType) {
			InterfaceType it = (InterfaceType) type;
			checkChildren(entity, it);

		} else if (type instanceof UnionType) {
			UnionType ut = (UnionType) type;
			checkChildren(entity, ut);

		} else if (type instanceof EnumType) {
			throw new IntrospectionException("enum.type.had.no.children",
					type.getName());

		} else {
			throw new IntrospectionException("unsupport.type", type.getName(), entity.getName());
		}
	}

	private void checkFragments(GraphQLSchema schema) throws Exception {
		for (String frName : fragments.keySet()) {
			Fragment fr = fragments.get(frName);
			Type tp = fr.getMappingType();
			Entity[] entities = fr.getEntities();
			for (Entity entity : entities) {
				checkEntity(schema, tp, entity);
			}
		}
	}

	private void checkParamArgumentType(Entity entity, ObjectField field)
			throws Exception {
		Argument[] arguments = field.getArguments();
		Param[] params = entity.getParams();
		if (arguments == null && params == null) {
			return;
		}
		// throw new IntrospectionException("diff.param.count",
		// arguments.length, params.length);
		if (params != null) {
			for (Param param : params) {
				String name = param.getName();
				if (field.getArgument(name) == null) {
					throw new IntrospectionException(
							"not.find.argument.in.field ", name,
							field.getName());
				}
			}
		}
		if (arguments != null) {
			for (int i = 0; i < arguments.length; i++) {
				Argument arg = arguments[i];
				String name = arg.getName();
				Param param = entity.getParam(name);
				if (param != null) {
					ParamValue pv = param.getParamValue();
					if (pv instanceof ParamVariable) {
						ParamVariable var = (ParamVariable) pv;
						Type t = var.getArgument().getType();
						if (!t.equals(arg.getType())) {
							throw new IntrospectionException(
									"type.not.compatible", name);
						}
					}
					continue;
				}
				if (arg.isNotNull()) {
					throw new IntrospectionException("argument.is.not.null",
							name, field.getName());
				}
			}
		}

	}
}
