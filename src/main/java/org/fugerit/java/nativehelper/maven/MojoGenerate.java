package org.fugerit.java.nativehelper.maven;

import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.fugerit.java.core.cfg.ConfigRuntimeException;
import org.fugerit.java.core.lang.helpers.StringUtils;
import org.fugerit.java.nhg.config.NativeHelperFacade;
import org.fugerit.java.nhg.config.model.NativeHelperConfig;

import java.io.File;
import java.io.FileWriter;

@Mojo( 
		name = "generate",
		threadSafe = true,
		defaultPhase = LifecyclePhase.COMPILE,
		requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class MojoGenerate extends AbstractMojo {
	
	public static final String PARAM_NATIVE_HELPER_CONFIG_PATH = "nativeHelperConfigPath";
	
    @Parameter(property = PARAM_NATIVE_HELPER_CONFIG_PATH, required = true )
    @Getter @Setter
    private String nativeHelperConfigPath;
    
	public static final String PARAM_REFLECT_CONFIG_JSON_OUTPUT_PATH = "reflectConfigJsonOutputPath";
	
    @Parameter(property = PARAM_REFLECT_CONFIG_JSON_OUTPUT_PATH, required = true )
    @Getter @Setter
    private String reflectConfigJsonOutputPath;

    public void execute() throws MojoExecutionException {
    	getLog().info( "using parameter "+PARAM_NATIVE_HELPER_CONFIG_PATH+" : "+this.nativeHelperConfigPath );
    	getLog().info( "using parameter "+PARAM_REFLECT_CONFIG_JSON_OUTPUT_PATH+" : "+this.reflectConfigJsonOutputPath );
        try {
            File nativeHelperConfigFile = new File( this.nativeHelperConfigPath );
            if ( nativeHelperConfigFile.exists() ) {
                NativeHelperConfig config = NativeHelperFacade.loadConfig( this.nativeHelperConfigPath );
                String reflectConfigJsonPath = StringUtils.valueWithDefault( this.reflectConfigJsonOutputPath, config.getReflectConfigJsonOutputPath() );
                if (StringUtils.isNotEmpty( reflectConfigJsonPath ) ) {
                    File reflectConfigJsonFile = new File( reflectConfigJsonPath );
                    if ( config.isCreateParentDirectory() ) {
                        getLog().info( String.format( "Create parent directory : %s -> %s", reflectConfigJsonFile.getParentFile(), reflectConfigJsonFile.getParentFile().mkdirs() ) );
                    }
                    try (FileWriter writer = new FileWriter( reflectConfigJsonFile ) ) {
                        NativeHelperFacade.generate( config, writer );
                    }
                } else {
                    throw new ConfigRuntimeException( String.format( "param %s not set", PARAM_REFLECT_CONFIG_JSON_OUTPUT_PATH ) );
                }
            } else {
                throw new ConfigRuntimeException( String.format( "%s does not exist : %s", PARAM_NATIVE_HELPER_CONFIG_PATH, nativeHelperConfigFile.getCanonicalPath() ) );
            }
        } catch (Exception e) {
        	getLog().error( "Error generating configuration : "+e, e );
        	throw new MojoExecutionException( "Error generating code : "+e, e );
        }
    }

}