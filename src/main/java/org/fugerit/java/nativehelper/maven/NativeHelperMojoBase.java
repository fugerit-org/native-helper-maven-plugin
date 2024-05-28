package org.fugerit.java.nativehelper.maven;

import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.fugerit.java.core.cfg.ConfigException;
import org.fugerit.java.core.lang.helpers.StringUtils;

import java.io.File;

public abstract class NativeHelperMojoBase extends AbstractMojo {

    public static final String PARAM_REFLECT_CONFIG_JSON_OUTPUT_PATH = "reflectConfigJsonOutputPath";

    public static final String PARAM_CREATE_PARENT_DIRECTORY = "createParentDirectory";

    public static final String PARAM_WARN_ON_ERROR = "warnOnError";

    @Parameter(property = PARAM_REFLECT_CONFIG_JSON_OUTPUT_PATH, required = false )
    @Getter @Setter
    private String reflectConfigJsonOutputPath;

    @Parameter(property = PARAM_CREATE_PARENT_DIRECTORY, required = false )
    @Getter @Setter
    private boolean createParentDirectory;

    @Parameter(property = PARAM_WARN_ON_ERROR, required = false )
    @Getter
    @Setter
    private boolean warnOnError;

    protected static final String USING_PARAMETERS = "using parameter %s : %s";

    protected abstract void executeWorker() throws ConfigException;

    public void execute() throws MojoExecutionException {
        getLog().info( String.format( USING_PARAMETERS, PARAM_REFLECT_CONFIG_JSON_OUTPUT_PATH, this.getReflectConfigJsonOutputPath() ) );
        getLog().info( String.format( USING_PARAMETERS, PARAM_CREATE_PARENT_DIRECTORY, this.isCreateParentDirectory() ) );
        getLog().info( String.format( USING_PARAMETERS, PARAM_WARN_ON_ERROR, this.isWarnOnError() ) );
        try {
            if (StringUtils.isNotEmpty( this.getReflectConfigJsonOutputPath() ) ) {
                File outputFile = new File(this.getReflectConfigJsonOutputPath());
                if ( this.isCreateParentDirectory() ) {
                    outputFile.getParentFile().mkdirs();
                }
            }
            this.executeWorker();
        } catch (Exception | NoClassDefFoundError | ExceptionInInitializerError e) {
            if ( this.isWarnOnError() ) {
                getLog().error(  String.format( "Error generating configuration : %s", e.getMessage() ), e );
                getLog().info( String.format( "Plugin context : %s", this.getPluginContext() ) );
            } else {
                getLog().error(  String.format( "Error generating configuration : %s", e.getMessage() ), e );
                throw new MojoExecutionException( String.format( "Error generating code : %s", e.toString() ), e );
            }
        }
    }

}
