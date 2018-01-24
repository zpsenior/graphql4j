package graphql4j.type;

import graphql4j.exception.TransformException;

public class ScalarType extends Type implements Input {

	public final static String TYPE_BOOLEAN = "Boolean";
	public final static String TYPE_STRING = "String";
	public final static String TYPE_BYTE = "Byte";
	public final static String TYPE_SHORT = "Short";
	public final static String TYPE_INT = "Int";
	public final static String TYPE_FLOAT = "Float";
	public final static String TYPE_DOUBLE = "Double";
	public final static String TYPE_LONG = "Long";
	public final static String TYPE_CHAR = "Char";
	public final static String TYPE_DATE = "Date";
	public final static String TYPE_BIGINT = "BigInt";
	public final static String TYPE_BIGDECIMAL = "BigDecimal";

	public final static ScalarType Boolean = new ScalarType(TYPE_BOOLEAN);
	public final static ScalarType String  = new ScalarType(TYPE_STRING);
	public final static ScalarType Byte    = new ScalarType(TYPE_BYTE);
	public final static ScalarType Short   = new ScalarType(TYPE_SHORT);
	public final static ScalarType Int     = new ScalarType(TYPE_INT);
	public final static ScalarType Float   = new ScalarType(TYPE_FLOAT);
	public final static ScalarType Long    = new ScalarType(TYPE_LONG);
	public final static ScalarType Double   = new ScalarType(TYPE_DOUBLE);
	public final static ScalarType Char     = new ScalarType(TYPE_CHAR);
	public final static ScalarType Date     = new ScalarType(TYPE_DATE);
	public final static ScalarType BigInt     = new ScalarType(TYPE_BIGINT);
	public final static ScalarType BigDecimal  = new ScalarType(TYPE_BIGDECIMAL);
	
	
	private String name;


	public String getName() {
		return name;
	}
	
	
	private ScalarType(String name){
		super(name);
		this.name = name;
	}

	@Override
	public void toSDL(StringBuffer sb) {
		sb.append(name);
	}
	
	@Override
	public boolean compatible(Type t) {
		if(t instanceof ScalarType){
			return name.equals(((ScalarType)t).name);
		}
		return false;
	}


	@Override
	public Object parseValue(Object value) throws Exception {
		if(value == null){
			return null;
		}
		if(TYPE_BOOLEAN.equals(name)){
			return parseBoolean(value);
		}else if(TYPE_INT.equals(name)){
			return parseInt(value);
		}else if(TYPE_LONG.equals(name)){
			return parseLong(value);
		}else if(TYPE_FLOAT.equals(name)){
			return parseFloat(value);
		}else if(TYPE_DOUBLE.equals(name)){
			return parseDouble(value);
		}else if(TYPE_STRING.equals(name)){
			return value.toString();
		}else if(TYPE_SHORT.equals(name)){
			return parseShort(value);
		}else if(TYPE_BYTE.equals(name)){
			return parseByte(value);
		}else if(TYPE_DATE.equals(name)){
			return parseDate(value);
		}else if(TYPE_BIGINT.equals(name)){
			return parseBigInt(value);
		}else if(TYPE_BIGDECIMAL.equals(name)){
			return parseBigDecimal(value);
		}
		throw new TransformException("error.type");
	}
	
	public static Type getTypeByClass(Class<?> cls) {
		if(cls.isArray()){
			cls = cls.getComponentType();
			Type type = getTypeByClass(cls);
			if(type != null){
				return new ArrayType(type, "", false);
			}
			return null;
		}
		String clsName = cls.getName();
		if("java.lang.String".equals(clsName)){
			return ScalarType.String;
		}else if("java.lang.Boolean".equals(clsName)||"boolean".equals(clsName)){
			return ScalarType.Boolean;
		}else if("java.lang.Integer".equals(clsName)||"int".equals(clsName)){
			return ScalarType.Int;
		}else if("java.lang.Float".equals(clsName)||"float".equals(clsName)){
			return ScalarType.Float;
		}else if("java.lang.Long".equals(clsName)||"long".equals(clsName)){
			return ScalarType.Long;
		}else if("java.lang.Double".equals(clsName)||"double".equals(clsName)){
			return ScalarType.Double;
		}else if("java.lang.Short".equals(clsName)||"short".equals(clsName)){
			return ScalarType.Short;
		}else if("java.lang.Character".equals(clsName)||"char".equals(clsName)){
			return ScalarType.Char;
		}else if("java.lang.Byte".equals(clsName)||"byte".equals(clsName)){
			return ScalarType.Byte;
		}else if("java.util.Date".equals(clsName)){
			return ScalarType.Date;
		}else if("java.math.BigInteger".equals(clsName)){
			return ScalarType.BigInt;
		}else if("java.math.BigDecimal".equals(clsName)){
			return ScalarType.BigDecimal;
		}
		return null;
	}

	public static Type parseScalarType(String name) {
		if("Boolean".equals(name)){
			return ScalarType.Boolean;
		}
		if("Int".equals(name)){
			return ScalarType.Int;
		}
		if("Char".equals(name)){
			return ScalarType.Char;
		}
		if("Short".equals(name)){
			return ScalarType.Short;
		}
		if("Byte".equals(name)){
			return ScalarType.Byte;
		}
		if("Long".equals(name)){
			return ScalarType.Long;
		}
		if("Float".equals(name)){
			return ScalarType.Float;
		}
		if("Double".equals(name)){
			return ScalarType.Double;
		}
		if("String".equals(name)){
			return ScalarType.String;
		}
		if("Date".equals(name)){
			return ScalarType.Date;
		}
		return null;
	}

	private Object parseDouble(Object value)throws Exception {
		if(value instanceof java.lang.Double){
			return value;
		}
		if(value instanceof java.lang.Number){
			return ((Number)value).doubleValue();
		}
		if(value instanceof java.lang.String){
			return java.lang.Double.parseDouble((java.lang.String)value);
		}
		throw new TransformException("error.double.type", value);
	}


	private Object parseFloat(Object value)throws Exception {
		if(value instanceof java.lang.Float){
			return value;
		}
		if(value instanceof java.lang.Integer || value instanceof java.lang.Short || value instanceof java.lang.Byte){
			return ((Number)value).floatValue();
		}
		if(value instanceof java.lang.String){
			return java.lang.Float.parseFloat((java.lang.String)value);
		}
		throw new TransformException("error.float.type", value);
	}


	private Object parseLong(Object value)throws Exception {
		if(value instanceof java.lang.Long){
			return value;
		}
		if(value instanceof java.lang.Integer || value instanceof java.lang.Short || value instanceof java.lang.Byte){
			return ((Number)value).longValue();
		}
		if(value instanceof java.lang.String){
			return java.lang.Long.parseLong((java.lang.String)value);
		}
		throw new TransformException("error.long.type", value);
	}


	private Object parseInt(Object value)throws Exception{
		if(value instanceof java.lang.Integer){
			return value;
		}
		if(value instanceof java.lang.Short || value instanceof java.lang.Byte){
			return ((Number)value).intValue();
		}
		if(value instanceof java.lang.String){
			return java.lang.Integer.parseInt((java.lang.String)value);
		}
		throw new TransformException("error.int.type", value);
	}


	private Object parseShort(Object value)throws Exception{
		if(value instanceof java.lang.Short){
			return value;
		}
		if(value instanceof java.lang.Byte){
			return ((Number)value).shortValue();
		}
		if(value instanceof java.lang.String){
			return java.lang.Short.parseShort((java.lang.String)value);
		}
		throw new TransformException("error.short.type", value);
	}


	private Object parseByte(Object value)throws Exception{
		if(value instanceof java.lang.Byte){
			return value;
		}
		if(value instanceof java.lang.String){
			return java.lang.Byte.parseByte((java.lang.String)value);
		}
		throw new TransformException("error.byte.type", value);
	}


	private Object parseBoolean(Object value)throws Exception{
		if(value instanceof java.lang.Boolean){
			return value;
		}
		if(value instanceof java.lang.String){
			return java.lang.Boolean.parseBoolean((java.lang.String)value);
		}
		throw new TransformException("error.boolean.type", value);
	}

	private Object parseDate(Object value)throws Exception{
		if(value instanceof java.util.Date){
			return value;
		}
		if(value instanceof java.lang.Long){
			return new java.util.Date((java.lang.Long)value);
		}
		if(value instanceof java.lang.String){
			return new java.util.Date(java.lang.Long.parseLong((java.lang.String)value));
		}
		throw new TransformException("error.date.type", value);
	}

	private Object parseBigInt(Object value)throws Exception{
		if(value instanceof java.math.BigInteger){
			return value;
		}
		if(value instanceof java.lang.String){
			return new java.math.BigInteger((java.lang.String)value);
		}
		throw new TransformException("error.big.int.type", value);
	}

	private Object parseBigDecimal(Object value)throws Exception{
		if(value instanceof java.math.BigDecimal){
			return value;
		}
		if(value instanceof java.lang.String){
			return new java.math.BigDecimal((java.lang.String)value);
		}
		throw new TransformException("error.big.decimal.type", value);
	}
}
