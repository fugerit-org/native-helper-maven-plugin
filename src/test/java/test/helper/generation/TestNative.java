package test.helper.generation;


import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestNative {

		
	@Test
	public void testReflectConfig() {
		try {
			AccessingAllClassesInPackage helper = new AccessingAllClassesInPackage();
			Set<Class<?>> set = helper.findAllClassesUsingClassLoader( "test.helper.generation.model" );
			List<String> classNames = set.stream().map( s -> s.getName() ).sorted().collect( Collectors.toList() );
			log.info( "classNames {}", classNames );
			StringBuilder sb = new StringBuilder();
			for ( String current : classNames ) {
				log.info( "current -> {}", current );
				sb.append( "{\n" );
				sb.append( "  \"name\": \""+current+"\",\n" );
				sb.append( "    \"methods\": [\n" );
				sb.append( "      { \"name\": \"<init>\", \"parameterTypes\": [] }\n" );
				Class<?> currentC = Class.forName( current );
				Method[] ml = currentC.getMethods();
				for ( int k =0; k<ml.length; k++ ) {
					Method currentM = ml[k];
					if ( ( currentM.getName().startsWith( "get") || currentM.getName().startsWith( "is") ) && currentM.getParameterTypes().length == 0 ) {
						log.info( "current method {} -> {}", current, currentM );
						sb.append( "      ,{ \"name\": \""+currentM.getName()+"\", \"parameterTypes\": [] }\n" );
					}
				}
				sb.append( "    ]\n" );
				sb.append( "},\n" );
			}
			log.info( "buffer : \n{}", sb.toString() );
		} catch (Exception e) {
			fail( "Error : "+e ); 
		}
		
	}
	
}
