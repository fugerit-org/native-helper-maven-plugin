# native-helper-maven-plugin

Maven plugin with helper methods for generating native configuration

[![Keep a Changelog v1.1.0 badge](https://img.shields.io/badge/changelog-Keep%20a%20Changelog%20v1.1.0-%23E05735)](https://github.com/fugerit-org/native-helper-maven-plugin/blob/main/CHANGELOG.md)
[![license](https://img.shields.io/badge/License-Apache%20License%202.0-teal.svg)](https://opensource.org/licenses/Apache-2.0)  
[![code of conduct](https://img.shields.io/badge/Conduct-Contributor%20Covenant%202.1-purple.svg)](https://github.com/fugerit-org/fj-universe/blob/main/CODE_OF_CONDUCT.md)
[![Maven Central](https://img.shields.io/maven-central/v/org.fugerit.java/native-helper-maven-plugin.svg)](https://central.sonatype.com/artifact/org.fugerit.java/native-helper-maven-plugin)  
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=fugerit-org_native-helper-maven-plugin&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=fugerit-org_native-helper-maven-plugin)  
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=fugerit-org_native-helper-maven-plugin&metric=coverage)](https://sonarcloud.io/summary/new_code?id=fugerit-org_native-helper-maven-plugin)

[![Java runtime version](https://img.shields.io/badge/run%20on-java%208+-%23113366.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://universe.fugerit.org/src/docs/versions/java11.html)
[![Java build version](https://img.shields.io/badge/build%20on-java%2011+-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://universe.fugerit.org/src/docs/versions/java11.html)
[![Apache Maven](https://img.shields.io/badge/Apache%20Maven-3.9.0+-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)](https://universe.fugerit.org/src/docs/versions/maven3_9.html)
[![Fugerit Github Project Conventions](https://img.shields.io/badge/Fugerit%20Org-Project%20Conventions-1A36C7?style=for-the-badge&logo=Onlinect%20Playground&logoColor=white)](https://universe.fugerit.org/src/docs/conventions/index.html)

This maven plugin allows generation of [graalvm](https://www.graalvm.org/) metadata 
using the [native-helper-graalvm](https://github.com/fugerit-org/native-helper-graalvm)

## 1. mojo 'generate'

Write a *native-helper-config.yaml* configuration file for the project.
([configuration reference here](https://github.com/fugerit-org/native-helper-graalvm))

```xml
<plugin>
    <groupId>org.fugerit.java</groupId>
    <artifactId>native-helper-maven-plugin</artifactId>
    <version>${native-helper-maven-plugin-version}</version>
    <executions>
        <execution>
            <id>generate-native-configuration</id>
            <phase>prepare-package</phase>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <nativeHelperConfigPath>src/main/config/native-helper-config.yaml</nativeHelperConfigPath>
                <reflectConfigJsonOutputPath>${project.build.directory}/generated-resources/reflect-config-demo.json</reflectConfigJsonOutputPath>
                <warnOnError>false</warnOnError>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### mojo 'generate' configuration reference

| name                        | default | required | type      | description                                                                   |
|-----------------------------|---------|----------|-----------|-------------------------------------------------------------------------------|
| nativeHelperConfigPath      | *none*  | *true*   | *string*  | Path to *native-helper-config.yaml* configuration file                        |
| reflectConfigJsonOutputPath | *none*  | *false*  | *string*  | generation path for *reflect-config.json* file                                |
| createParentDirectory       | *false* | *false*  | *boolean* | if set to *true* will create *reflectConfigJsonOutputPath* parent folder      |
| warnOnError                 | *false* | *false*  | *boolean* | if set to *true* exception will be logged instead of generating a build error |

## 2. mojo 'merge'

```xml
<plugin>
    <groupId>org.fugerit.java</groupId>
    <artifactId>native-helper-maven-plugin</artifactId>
    <version>${native-helper-maven-plugin-version}</version>
    <executions>
        <execution>
            <id>merge-native-configuration</id>
            <phase>prepare-package</phase>
            <goals>
                <goal>merge</goal>
            </goals>
            <configuration>
                <reflectConfigJsonFiles>
                    <reflectConfigJsonFile>${project.build.directory}/generated-resources/reflect-config-demo.json</reflectConfigJsonFile>
                    <reflectConfigJsonFile>${project.basedir}/src/main/config/reflect-config-nhg.json</reflectConfigJsonFile>
                </reflectConfigJsonFiles>
                <reflectConfigJsonOutputPath>${project.build.outputDirectory}/META-INF/native-image/reflect-config.json</reflectConfigJsonOutputPath>
                <createParentDirectory>true</createParentDirectory>
                <warnOnError>true</warnOnError>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### mojo 'merge' configuration reference

| name                                         | default | required | type      | description                                                                   |
|----------------------------------------------|---------|----------|-----------|-------------------------------------------------------------------------------|
| reflectConfigJsonFiles.reflectConfigJsonFile | *none*  | *true*   | *string*  | List of *reflect-config.json* files to merge                                  |
| reflectConfigJsonOutputPath                  | *none*  | *true*   | *string*  | generation path for *reflect-config.json* file                                |
| createParentDirectory                        | *false* | *false*  | *boolean* | if set to *true* will create *reflectConfigJsonOutputPath* parent folder      |
| warnOnError                                  | *false* | *false*  | *boolean* | if set to *true* exception will be logged instead of generating a build error |

## 3. Demo project

Here is a [simple demo project](https://github.com/caffetteria/native-metadata-demo)