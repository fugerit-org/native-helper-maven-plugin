package org.fugerit.java.nativehelper.maven;

import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.fugerit.java.core.cfg.ConfigException;
import org.fugerit.java.nhg.GenerateReflectConfig;
import org.fugerit.java.nhg.MergeConfigUtil;
import org.fugerit.java.nhg.config.NativeHelperFacade;
import org.fugerit.java.nhg.reflect.config.Entry;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

@Mojo( 
		name = "merge",
		threadSafe = true,
		defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
		requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class MojoMerge extends NativeHelperMojoBase {
	
	public static final String PARAM_REFLECT_CONFIG_JSON_FILES = "reflectConfigJsonFiles";

    public static final String PARAM_REFLECT_CONFIG_JSON_OUTPUT_PATH = "reflectConfigJsonOutputPath";

    @Parameter(property = PARAM_REFLECT_CONFIG_JSON_FILES, required = true )
    @Getter @Setter
    private List<String> reflectConfigJsonFiles;

    @Parameter(property = PARAM_REFLECT_CONFIG_JSON_OUTPUT_PATH, required = true )
    @Getter @Setter
    private String reflectConfigJsonOutputPath;

    @Override
    public void executeWorker() throws ConfigException {
        getLog().info( String.format( USING_PARAMETERS, PARAM_REFLECT_CONFIG_JSON_FILES, this.getReflectConfigJsonFiles() ) );
        ConfigException.apply( () -> {
            List<Entry> entries = new ArrayList<>();
            for ( String currentFileName : this.reflectConfigJsonFiles ) {
                File reflectConfigJsonFile = new File( currentFileName );
                if ( reflectConfigJsonFile.isFile() ) {
                    MergeConfigUtil.add( entries, reflectConfigJsonFile );
                } else {
                    throw new ConfigException( String.format( "It is not a file : %s (path:%s)", currentFileName, reflectConfigJsonFile.getCanonicalPath() ) );
                }
            }
            GenerateReflectConfig generateReflectConfig = new GenerateReflectConfig();
            try (FileWriter writer = new FileWriter( this.reflectConfigJsonOutputPath ) ) {
                this.getLog().info( String.format( "generating reflect config file : %s with %s entries", this.getReflectConfigJsonOutputPath(), entries.size() ) );
                generateReflectConfig.generate( writer, entries );
            }
        } );
    }

}