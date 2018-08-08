package graphql4j.test.market;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class PO {
	
	protected static final String bucket = "test";
	protected static final String endPoint = "img-cn-shenzhen.aliyuncs.com";
	
	private static String img_prefix = null;
	
	protected String getFullpath(String filepath){
		String path = filepath;
		if(img_prefix != null){
			path = img_prefix + filepath;
		}else{
			path = "http://" + bucket + "." + endPoint + "/" + filepath;
		}
		return path;
	}
	
	static{
		img_prefix = System.getenv("TEST_IMG_PREFIX");
	}
	
	private final static int SECOND_OF_MINUTE = 60;
	
	private final static int SECOND_OF_HOUR = SECOND_OF_MINUTE * 60;
	
	private final static int SECOND_OF_DAY = SECOND_OF_HOUR * 24;
	
	protected String getTimeTag(Date time){
		if(time == null){
			return "";
		}
		long l = (System.currentTimeMillis() - time.getTime())/1000;
		if(l < SECOND_OF_MINUTE){
			return "1分钟内";
		}else if(l < SECOND_OF_HOUR){
			l = l / SECOND_OF_MINUTE;
			return l + "分钟前";
		}else if(l < SECOND_OF_DAY){
			l = l / SECOND_OF_HOUR;
			return l + "小时前";
		}
		l = l / SECOND_OF_DAY;
		return l + "天前";
	}
	
	protected String getCurrency(int amount){
		BigDecimal dec = new BigDecimal(amount);
		dec = dec.divide(BigDecimal.TEN).divide(BigDecimal.TEN);
		return dec.toString();
	}
	
	private final static char SP = '"';
	
	private Map<String, Method> props = null;
	
	private void init(){
		
		props = new HashMap<String, Method>();
		
		Class<?> c = this.getClass();
		getClassMethod(c);
		
	}

	private void getClassMethod(Class<?> c) {
		Method[] methods = c.getDeclaredMethods();
		
		for(Method m : methods){
			int modifers = m.getModifiers();
			if(!Modifier.isPublic(modifers)){
				continue;
			}
			if(m.getParameterTypes().length != 0){
				continue;
			}
			String methodName = m.getName();
			if(!methodName.startsWith("get")){
				continue;
			}
			String name = methodName.substring(3).toLowerCase();
			
			props.put(name, m);
		}
		Class<?> sc = c.getSuperclass();
		String scName = sc.getName();
		if(scName.equals(PO.class.getName())){
			return;
		}
		if(scName.equals(Object.class.getName())){
			return;
		}
		getClassMethod(sc);
	}
	
	public Map<String, Object> toMap(){
		if(props == null){
			init();
		}
		Object[] params = new Object[]{};
		Map<String, Object> map = new HashMap<String, Object>();
		for(String name : props.keySet()){
			Method m = props.get(name);
			//String value = null;
			Object obj = null;
			
			try{
				obj = m.invoke(this, params);
			}catch(Exception e){
				e.printStackTrace();
			}
			map.put(name, obj);
		}
		return map;
	}
	
	public String toString(){
		if(props == null){
			init();
		}
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");
		
		Object[] params = new Object[]{};
		
		boolean first = true;
		for(String name : props.keySet()){

			Method m = props.get(name);
			String value = null;
			Object obj = null;
			
			try{
				obj = m.invoke(this, params);
			}catch(Exception e){
				e.printStackTrace();
			}
			if(obj != null){
				value = obj.toString();
			}
			
			if(!first){
				sb.append(", ");
			}
			sb.append(SP).append(name).append(SP).append(" : ").append(SP).append(value).append(SP);
			
			first = false;
		}
		
		sb.append("}");
		
		return sb.toString();
	}
	/*
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("{");
		
		Class<?> c = this.getClass();
		
		Method[] methods = c.getDeclaredMethods();
		
		boolean first = true;
		
		for(Method m : methods){
			int modifers = m.getModifiers();
			if(!Modifier.isPublic(modifers)){
				continue;
			}
			if(m.getParameterTypes().length != 0){
				continue;
			}
			String methodName = m.getName();
			if(!methodName.startsWith("get")){
				continue;
			}
			String name = methodName.substring(3).toLowerCase();

			String value = null;
			Object obj = null;
			
			try{
				obj = m.invoke(this, new Object[]{});
			}catch(Exception e){
				e.printStackTrace();
				log.error(e);
			}
			if(obj != null){
				value = obj.toString();
			}
			
			if(!first){
				sb.append(", ");
			}
			sb.append(SP).append(name).append(SP).append(" : ").append(SP).append(value).append(SP);
			
			first = false;
		}
		
		sb.append("}");
		
		return sb.toString();
	} */
	
	
}
