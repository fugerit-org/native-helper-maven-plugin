package org.fugerit.java.nativehelper.maven;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.jaxb2.shared.FileSystemUtilities;
import org.fugerit.java.core.io.FileIO;
import org.fugerit.java.nativehelper.tool.AccessingAllClassesInPackage;

@Mojo( 
		name = "generate",
		threadSafe = true,
		defaultPhase = LifecyclePhase.COMPILE,
		requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class MojoGenerate extends AbstractCodegenMojo {
	
	public static final String PARAM_NATIVE_HELPER_CONFIG = "nativeHelperConfig";
	
    @Parameter(property = PARAM_NATIVE_HELPER_CONFIG, required = true )
    private String nativeHelperConfig;
    
	public static final String PARAM_OUTPUT_DIR = "outputDirectory";
	
    @Parameter(property = PARAM_OUTPUT_DIR, required = true )
    private String outputDirectory;
    
    /**
     * The injected Maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;
     
    public void execute() throws MojoExecutionException {
    	getLog().info( "using parameter "+PARAM_NATIVE_HELPER_CONFIG+" : "+this.nativeHelperConfig );
    	getLog().info( "using parameter "+PARAM_OUTPUT_DIR+" : "+this.outputDirectory );
        File file = new File( this.nativeHelperConfig );
        try {
        	String packageName = "org.fugerit.java.doc.base.model";
        	getLog().info( "daogen config path : "+file.getCanonicalPath() );
        	AccessingAllClassesInPackage helper = new AccessingAllClassesInPackage();
        	Set<Class<?>> classNames = helper.findAllClassesUsingClassLoader(packageName);
        	getLog().info( "List : "+classNames );
        	File outputFolder = new File( this.outputDirectory );
        	getLog().info( "create output dir : "+outputFolder.getCanonicalPath()+" : "+outputFolder.mkdirs() );
        	getLog().info( "exists output dir : "+outputFolder.exists()+" , parent exists : "+outputFolder.getParentFile().exists() );
        	File nativeImageFolder = new File( outputFolder, "META-INF/native-image" );
        	nativeImageFolder.mkdirs();
        	FileIO.writeString( "{}" , new File( nativeImageFolder, "reflect-config.json" ) );
        	FileIO.writeString( "{}" , new File( nativeImageFolder, "resource-config.json" ) );
        	//this.addGeneratedSourcesToProjectSourceRoot();
        	final Resource outputDirectoryResource = new Resource();
        	this.getLog().info( "resourse path : "+this.getOutputDirectory().getCanonicalPath() );
        	outputDirectoryResource.setDirectory( this.getOutputDirectory().getCanonicalPath() );
        	outputDirectoryResource.setIncludes(Collections.singletonList("**/*.json"));
        	this.addResource( outputDirectoryResource );
        } catch (Exception e) {
        	e.printStackTrace();
        	throw new MojoExecutionException( "Error generating code : "+e, e );
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void addGeneratedSourcesToProjectSourceRoot() {
        getProject().addCompileSourceRoot(FileSystemUtilities.getCanonicalPath(getOutputDirectory()));
    }
    
	@Override
	protected void addResource(Resource resource) {
		getProject().addResource(resource);
	}

	@Override
	protected boolean shouldExecutionBeSkipped() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isReGenerationRequired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean performExecution() throws MojoExecutionException, MojoFailureException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected List<URL> getSources() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected File getOutputDirectory() {
		return new File( this.outputDirectory );
	}

	@Override
	protected List<String> getClasspath() throws MojoExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getStaleFileName() {
		// TODO Auto-generated method stub
		return null;
	}

}