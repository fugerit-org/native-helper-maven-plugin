package test.org.fugerit.java.nativehelper.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.fugerit.java.nativehelper.maven.MojoGenerate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

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

}
