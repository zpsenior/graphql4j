package graphql4j.type;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import graphql4j.operation.JSONParser;
import graphql4j.operation.JSONReader;
import graphql4j.annotation.GraphQLObject;
import graphql4j.exception.TransformException;


public class ArrayType extends Type implements Input {

	private Type type;
	private boolean childNotNull;
	
	public ArrayType(Type type, String bindClass, boolean childNotNull){
		super(bindClass);
		this.type = type;
	}

	public Type getBaseType() {
		return type;
	}

	public boolean isChildNotNull() {
		return childNotNull;
	}
	
	@Override
	public boolean compatible(Type t) {
		if(t instanceof ArrayType){
			return type.compatible(((ArrayType)t).type);
		}
		return false;
	}

	@Override
	public void toSDL(StringBuffer sb) {
		sb.append("[");
		type.toString(sb);
		sb.append("]");
	}

	@Override
	public String getName() {
		StringBuffer sb = new StringBuffer();
		sb.append("[").append(type.getName());
		if(childNotNull){
			sb.append("!");
		}
		sb.append("]");
		return sb.toString();
	}
	
	public boolean equals(Object o){
		if(o == null || !(o instanceof ArrayType)){
			return false;
		}
		return getBaseType().equals(((ArrayType)o).getBaseType());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object parseValue(Object value) throws Exception {
		if(!(type instanceof Input)){
			throw new TransformException("base.type.is.not.input.interface");
		}
		if(value == null){
			return null;
		}
		if(value instanceof String){
			String str = (String)value;
			str = str.trim();
			if(str.startsWith("[") && str.endsWith("]")) {
				JSONReader reader = new JSONReader(new JSONParser(str));
				value = reader.readObject();
				if(!(value instanceof List)){
					throw new TransformException("value.is.not.list.class");
				}
			}else {
				String[] strArray = str.split(",");
				value = Arrays.asList(strArray);
			}
		}
		Collection source = (Collection)value;
		Collection target;
		Class<?> cls;
		String bindClass = getBindClass();
		if(!"".equals(bindClass)){
			cls = Class.forName(bindClass);
			if(cls.isInstance(value)){
				return cls.cast(value);
			}
			target = (Collection)cls.newInstance();
		}else{
			if(type instanceof ScalarType) {
				ScalarType st = (ScalarType)type;
				cls = st.getBaseClass();
			}else {
				cls = Class.forName(type.getBindClass());
			}
			target = createArrayList(cls);
		}
		Input in = (Input)type;
		for(Object val : source){
			Object obj = in.parseValue(val);
			target.add(obj);
		}
		if(!"".equals(bindClass)){
			return target;
		}
		return toArray(target);
	}
	
	private <T> Collection<T> createArrayList(Class<T> cls) {
		return new ArrayList<T>();
	}
	
	@SuppressWarnings("unchecked")
	private <T> T[] toArray(Collection<T> list) {
		T[] array = (T[])Array.newInstance(list.iterator().next().getClass(), list.size());
		int i = 0;
		for(T item : list) {
			array[i] = item;
			i++;
		}
		return array;
	}

	public static ArrayType buildNestedArrayType(Class<?> parentClass, Class<?> cls, java.lang.reflect.Type gtype, TypeFinder finder, boolean input)throws Exception{
		return buildNestedArrayType(parentClass, cls.getName(), gtype.toString(), finder, input);
	}
	
	public static ArrayType buildNestedArrayType(Class<?> parentClass, String className, String gtypeStr, TypeFinder finder, boolean input)throws Exception{
		Class<?> cls = Class.forName(className);
		if(!isArrayWrapperClass(cls)){
			throw new TransformException("unsurpport.class.as.list.type", className);
		}
		String prefix = className + "<";
		String baseClass = gtypeStr;
		if(baseClass.startsWith(prefix)){
			baseClass = baseClass.substring(prefix.length(), baseClass.length() - 1);
			int pos = baseClass.indexOf('<');
			if(pos > 0){
				String clsName = baseClass.substring(0, pos);
				Type t = buildNestedArrayType(parentClass, clsName, baseClass, finder, input);
				return new ArrayType(t, className, false);
			}else{
				Type tp;
				if(parentClass != null && parentClass.getName().equals(baseClass)) {
					GraphQLObject gqlObject = parentClass.getAnnotation(GraphQLObject.class);
					String name = gqlObject.value();
					if(name == null || "".equals(name)) {
						name = parentClass.getSimpleName();
					}
					tp = new DummyType(name);
				}else {
					if(finder != null){
						tp = finder.getTypeByClass(Class.forName(baseClass), input);
					}else{
						tp = new DummyType(baseClass);
					}
				}
				return new ArrayType(tp, className, false);
			}
		}else{
			return new ArrayType(new DummyType("java.lang.Object"), className, false);
		}
	}
	
	private static String[] arrayWrapperClass = new String[]{"java.util.List", "java.util.Collection"};
	
	public static boolean isArrayWrapperClass(Class<?> cls)throws Exception{
		Class<?>[] interfaces = cls.getInterfaces();
		for(Class<?> inf : interfaces){
			String className = inf.getName();
			for(String clsName : arrayWrapperClass){
				if(clsName.equals(className)){
					return true;
				}
			}
		}
		return false;
	}
}
