package org.fugerit.java.nativehelper.maven;

import lombok.Getter;
import lombok.Setter;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.fugerit.java.core.cfg.ConfigException;
import org.fugerit.java.core.cfg.ConfigRuntimeException;
import org.fugerit.java.core.function.SafeFunction;
import org.fugerit.java.core.lang.helpers.StringUtils;
import org.fugerit.java.nhg.config.NativeHelperFacade;
import org.fugerit.java.nhg.config.model.NativeHelperConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mojo( 
		name = "generate",
		threadSafe = true,
		defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
		requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class MojoGenerate extends NativeHelperMojoBase {
	
	public static final String PARAM_NATIVE_HELPER_CONFIG_PATH = "nativeHelperConfigPath";

    @Parameter(property = PARAM_NATIVE_HELPER_CONFIG_PATH, required = true )
    @Getter @Setter
    private String nativeHelperConfigPath;


    @Parameter(defaultValue = "${project}")
    @Getter @Setter
    private MavenProject project;

    /*
     * Thanks to @viqueen for this code  :
     * https://medium.com/@viqueen/building-a-maven-plugin-to-explore-your-code-base-fe309ce56eb6
     */
    private void setupClassLoader() throws DependencyResolutionRequiredException, IOException {
        if ( this.getProject() != null ) {
            final Set<URI> urls = new LinkedHashSet<>();
            // get the project's compile classpath elements
            // and turn them into URLs
            for (String element : project.getCompileClasspathElements()) {
                urls.add(new File(element).toURI());
            }
            // create a URLClassLoader using the previously
            // resolved urls
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{
                    new File(project.getBuild().getOutputDirectory()).toURI().toURL()
            }, new URLClassLoader( urls.stream().map( u -> SafeFunction.get( u::toURL ) ).collect( Collectors.toList() ).toArray(new URL[]{})));
            // use the custom class loader
            Thread.currentThread().setContextClassLoader( urlClassLoader );
        }
    }

    @Override
    protected void executeWorker() throws ConfigException {
        getLog().info( "using parameter "+PARAM_NATIVE_HELPER_CONFIG_PATH+" : "+this.getNativeHelperConfigPath() );
        ConfigException.apply( () -> {
            this.setupClassLoader();
            File nativeHelperConfigFile = new File( this.getNativeHelperConfigPath() );
            if ( nativeHelperConfigFile.exists() ) {
                NativeHelperConfig config = NativeHelperFacade.loadConfig( this.getNativeHelperConfigPath() );
                String reflectConfigJsonPath = StringUtils.valueWithDefault( this.getReflectConfigJsonOutputPath(), config.getReflectConfigJsonOutputPath() );
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
        } );
    }

}