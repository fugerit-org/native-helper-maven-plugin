package org.fugerit.java.nativehelper.tool;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

import org.fugerit.java.core.cfg.ConfigRuntimeException;
import org.fugerit.java.core.lang.helpers.ClassHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccessingAllClassesInPackage {

    public Set<Class<?>> findAllClassesUsingClassLoader(String packageName) throws Exception {
        InputStream stream = ClassHelper.getDefaultClassLoader()
          .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
          .filter(line -> line.endsWith(".class"))
          .map(line -> getClass(line, packageName))
          .collect(Collectors.toSet());
    }
 
    private Class<?> getClass(String className, String packageName) {
    	Class<?> res = null;
        try {
        	log.debug( "packageName : {}, className : {}", packageName, className );
            res = Class.forName(packageName+"."+className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
        	throw new ConfigRuntimeException( "Failed to load class : "+e, e );
        }
        return res;
    }
}