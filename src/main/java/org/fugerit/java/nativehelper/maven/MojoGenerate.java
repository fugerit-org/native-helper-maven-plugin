package org.fugerit.java.nativehelper.maven;

import lombok.Getter;
import lombok.Setter;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
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
		name = "nativeHelper",
		threadSafe = true,
		defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
		requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class MojoGenerate extends AbstractMojo {
	
	public static final String PARAM_NATIVE_HELPER_CONFIG_PATH = "nativeHelperConfigPath";

    public static final String PARAM_REFLECT_CONFIG_JSON_OUTPUT_PATH = "reflectConfigJsonOutputPath";

    public static final String PARAM_WARN_ON_ERROR = "warnOnError";

    @Parameter(property = PARAM_NATIVE_HELPER_CONFIG_PATH, required = true )
    @Getter @Setter
    private String nativeHelperConfigPath;

    @Parameter(property = PARAM_REFLECT_CONFIG_JSON_OUTPUT_PATH, required = false )
    @Getter @Setter
    private String reflectConfigJsonOutputPath;

    @Parameter(property = PARAM_WARN_ON_ERROR, required = false )
    @Getter @Setter
    private boolean warnOnError;

    @Parameter(defaultValue = "${project}")
    @Getter @Setter
    private MavenProject project;

    /*
     * Thanks to @viqueen for this code  :
     * https://medium.com/@viqueen/building-a-maven-plugin-to-explore-your-code-base-fe309ce56eb6
     */
    private void setupClassLoader() throws DependencyResolutionRequiredException, IOException {
        if ( this.project != null ) {
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

    public void execute() throws MojoExecutionException {
    	getLog().info( "using parameter "+PARAM_NATIVE_HELPER_CONFIG_PATH+" : "+this.nativeHelperConfigPath );
    	getLog().info( "using parameter "+PARAM_REFLECT_CONFIG_JSON_OUTPUT_PATH+" : "+this.reflectConfigJsonOutputPath );
        try {
            this.setupClassLoader();
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
        } catch (Exception | NoClassDefFoundError | ExceptionInInitializerError e) {
            if ( this.isWarnOnError() ) {
                getLog().error( "Error generating configuration : "+e, e );
                getLog().info( "Plugin context : "+this.getPluginContext() );
            } else {
                throw new MojoExecutionException( "Error generating code : "+e, e );
            }
        }
    }

}