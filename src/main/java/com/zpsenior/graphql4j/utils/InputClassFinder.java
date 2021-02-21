package com.zpsenior.graphql4j.utils;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.zpsenior.graphql4j.annotation.Input;
import com.zpsenior.graphql4j.exception.ExecuteException;
import com.zpsenior.graphql4j.input.InputFinder;

public class InputClassFinder implements InputFinder {

	private Map<String, Class<?>> classes = new HashMap<>();
	
	private ClassLoader cl = Thread.currentThread().getContextClassLoader();
	
	public InputClassFinder(String[] packageNames) throws Exception {
		for(String packageName : packageNames) {
			init(packageName);
		}
	}

	private void init(String packageName) throws Exception {
		if(packageName == null || "".equals(packageName)) {
			return;
		}
		String packageDirName = packageName.replace('.', '/');
		Enumeration<URL> dirs = cl.getResources(packageDirName);
		while (dirs.hasMoreElements()) {
			URL url = dirs.nextElement();
			String protocol = url.getProtocol();
			if ("file".equals(protocol)) {
				String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
				findInFile(packageName, filePath);
			} else {
				JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
				packageName = findInJar(packageName, packageDirName, jar);
			}
		}
	}

	private String findInJar(String packageName, String packageDirName, JarFile jar)throws Exception {
		 
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();  
		    String name = entry.getName();
		    if (name.charAt(0) == '/') {
		        name = name.substring(1);  
		    }
		    if (name.startsWith(packageDirName)) {
		    	int idx = name.lastIndexOf('/');  
		        if (idx != -1) {  
		            packageName = name.substring(0, idx).replace('/', '.');  
		        }  
		        if (idx != -1) {  
		            if (name.endsWith(".class") && !entry.isDirectory()) {  
		                String className = name.substring(  
		                        packageName.length() + 1, name  
		                                .length() - 6);  
		                addClass(packageName + "." + className); 
		            }  
		        }
		    }
		}
		return packageName;
	}

	@Override
	public Class<?> findClass(String name) throws Exception {
		if (classes.containsKey(name)) {
			return classes.get(name);
		}
		throw new ExecuteException("can not find class by name:" + name);
	}

	public void findInFile(String packageName, String packagePath)throws Exception {

		File dir = new File(packagePath);

		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}

		File[] dirfiles = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return (file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		for (File file : dirfiles) {
			if (file.isDirectory()) {
				findInFile(packageName + "." + file.getName(), file.getAbsolutePath());
			} else {
				String className = file.getName().substring(0, file.getName().length() - 6);
				addClass(packageName + "." + className);
			}
		}
	}

	private void addClass(String className) throws Exception {
		Class<?> cls = cl.loadClass(className);
		Input input = cls.getAnnotation(Input.class);
		if(input != null) {
			String name = input.value();
			classes.put(name, cls);
		}
	}

}
