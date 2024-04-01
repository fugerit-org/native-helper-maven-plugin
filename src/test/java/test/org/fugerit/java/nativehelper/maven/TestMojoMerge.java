package test.org.fugerit.java.nativehelper.maven;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.fugerit.java.nativehelper.maven.MojoGenerate;
import org.fugerit.java.nativehelper.maven.MojoMerge;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class TestMojoMerge {

    private static final String TEST_PATH = "src/test/resources/reflect-config";

    private static final String TEST_REFLECT_CONFIG_1 = TEST_PATH+"/reflect-config-emp.json";

    private static final String TEST_REFLECT_CONFIG_2 = TEST_PATH+"/reflect-config-nhg.json";

    @Test
    void test1() throws MojoExecutionException, IOException {
        MojoMerge mojo = new MojoMerge();
        mojo.setReflectConfigJsonFiles( Arrays.asList( TEST_REFLECT_CONFIG_1, TEST_REFLECT_CONFIG_2 ) );
        File file = new File( "target/main-test/native-image/reflect-config.json" );
        file.delete();
        mojo.setReflectConfigJsonOutputPath( file.getCanonicalPath() );
        mojo.setCreateParentDirectory( true );
        mojo.execute();
        Assertions.assertTrue( file.exists() );
    }

    @Test
    void test2() throws MojoExecutionException, IOException {
        MojoMerge mojo = new MojoMerge();
        mojo.setReflectConfigJsonFiles( Arrays.asList( TEST_REFLECT_CONFIG_1, TEST_REFLECT_CONFIG_2 ) );
        File file = new File( "target/reflect-config.json" );
        file.delete();
        mojo.setReflectConfigJsonOutputPath( file.getCanonicalPath() );
        mojo.execute();
        Assertions.assertTrue( file.exists() );
    }

    @Test
    void testFail() throws MojoExecutionException, IOException {
        MojoMerge mojo = new MojoMerge();
        mojo.setReflectConfigJsonFiles( Arrays.asList( "/not-exists.json", TEST_REFLECT_CONFIG_2 ) );
        File file = new File( "target/not-exists-reflect-config.json" );
        file.delete();
        mojo.setReflectConfigJsonOutputPath( file.getCanonicalPath() );
        Assertions.assertThrows( MojoExecutionException.class, () -> mojo.execute() );
    }

}
