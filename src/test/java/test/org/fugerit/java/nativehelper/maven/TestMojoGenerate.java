package test.org.fugerit.java.nativehelper.maven;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.fugerit.java.nativehelper.maven.MojoGenerate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class TestMojoGenerate {

    @Test
    void test1() throws MojoExecutionException, IOException {
        MojoGenerate mojo = new MojoGenerate();
        File file = new File( "target/main-test/native-image/reflect-config.json" );
        file.delete();
        mojo.setNativeHelperConfigPath( "src/test/resources/tool/config/native-helper-config.yaml" );
        mojo.execute();
        Assertions.assertTrue( file.exists() );
    }

    @Test
    void test2() throws MojoExecutionException, IOException {
        MojoGenerate mojo = new MojoGenerate();
        File file = new File( "target/override-test/native-image/reflect-config.json" );
        file.delete();
        // maven project mock
        mojo.setProject( new MavenProject() {
            @Override
            public Build getBuild() {
                return new Build() {
                    @Override
                    public String getOutputDirectory() {
                        return "target";
                    }
                };
            }

            @Override
            public List<String> getCompileClasspathElements() throws DependencyResolutionRequiredException {
                return Arrays.asList( new File( this.getBuild().getOutputDirectory(),"classes" ).getAbsolutePath() );
            }
        } );
        mojo.setNativeHelperConfigPath( "src/test/resources/tool/config/native-helper-config.yaml" );
        mojo.setReflectConfigJsonOutputPath( file.getCanonicalPath() );
        mojo.execute();
        Assertions.assertTrue( file.exists() );
    }

    @Test
    void testFail1() throws MojoExecutionException, IOException {
        MojoGenerate mojo = new MojoGenerate();
        mojo.setNativeHelperConfigPath( "src/test/resources/tool/config/native-helper-config-fail.yaml" );
        Assertions.assertThrows( MojoExecutionException.class, () -> mojo.execute() );
    }

    @Test
    void testFail2() throws MojoExecutionException, IOException {
        MojoGenerate mojo = new MojoGenerate();
        mojo.setNativeHelperConfigPath( "src/test/resources/tool/config/native-helper-config-not-exists.yaml" );
        Assertions.assertThrows( MojoExecutionException.class, () -> mojo.execute() );
    }

    @Test
    void testFail3() throws MojoExecutionException, IOException {
        MojoGenerate mojo = new MojoGenerate();
        mojo.setNativeHelperConfigPath( "src/test/resources/tool/config/native-helper-config-fail.yaml" );
        mojo.setWarnOnError( true );
        mojo.execute();
        Assertions.assertNotNull( mojo );
    }

}
