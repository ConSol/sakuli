# Sakuli Java DSL

Sakuli provides a Java DSL for writing test cases in pure Java. The DSL is designed as fluent API and provides the exact same
capabilities as the Javascript API.

## Usage

The Sakuli Java DSL comes to you as Maven module JAR file. You can add the module to your Sakuli project as Maven dependency. Currently the Java tests have to be written with the [TestNG] unit framework, so also provide the [TestNG]  Maven dependency in your project POM:

```.xml
    <dependencies>
        <dependency>
            <groupId>org.sakuli</groupId>
            <artifactId>java-dsl</artifactId>
            <version>${sakuli.version}</version>
            <scope>test</scope>
        </dependency>
        <!-- needed for AbstractSakuliTest class -->
        <dependency>
            <groupId>${project.groupId}</groupId>
            <artifactId>java-dsl</artifactId>
            <version>${sakuli.version}</version>
            <type>test-jar</type>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testng</groupId>
            <artifactId>testng</artifactId>
            <version>${testng.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
```    
        
In the last step, we have to provide also some local resources for our Sakuli test setup. Therefore we use the [maven-dependency-plugin] to unpack all needed Sakuli resources to our local `project.build.outputDirectory`:
 
```.xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <executions>
                    <execution>
                        <id>unpack</id>
                        <phase>generate-resources</phase>
                        <goals>
                            <goal>unpack</goal>
                        </goals>
                        <configuration>
                            <artifactItems>
                                <artifactItem>
                                    <groupId>org.sakuli</groupId>
                                    <artifactId>java-dsl</artifactId>
                                    <version>${sakuli.version}</version>
                                    <type>jar</type>
                                    <overWrite>true</overWrite>
                                </artifactItem>
                            </artifactItems>
                            <outputDirectory>${project.build.outputDirectory}</outputDirectory>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```
        
Now we are ready to write Java tests. See the following sample test which uses TestNG unit testing library in combination with Sakuli Java DSL:

```.java
        public class FirstExampleTest extends AbstractSakuliTest {
        
            Environment env;
        
            @BeforeClass
            @Override
            public void initTC() throws Exception {
                super.initTC();
                env = new Environment();
            }
        
            @Override
            protected TestCaseInitParameter getTestCaseInitParameter() throws Exception {
                return new TestCaseInitParameter("test1");
            }
        
            @Test
            public void testCitrus() throws Exception {
                browser.open();
                browser.navigateTo("http://www.citrusframework.org/");
        
                ElementStub heading1 = browser.paragraph("Citrus Integration Testing");
                heading1.highlight();
                assertTrue(heading1.isVisible());
        
                ElementStub download = browser.link("/Download v.*/");
                download.highlight();
                assertTrue(download.isVisible());
                download.click();
        
                ElementStub downloadLink = browser.cell("2.6.1");
                downloadLink.highlight();
                assertTrue(downloadLink.isVisible());
            }
        }
```
        
All people that are familiar with TestNG unit testing will notice that a Sakuli Java test is nothing but a normal TestNG 
unit test. Sakuli just adds the end-to-end testing capabilities. The test class extends an abstract class coming from Sakuli. 
This `AbstractSakuliTest` provides convenient access to the Sakuli Java DSL API.

These are methods such as `initTC()` and `getTestCaseInitParameter()` that are customizable in test classes. Just 
override the methods and add custom logic. In addition to that the abstract super class in Sakuli provides access to the
`browser` field that represents the Sahi web browser capabilities. This browser object is used in the test cases to trigger
Sahi related actions such as opening a website and highlighting links or buttons.

In the example above we open a website **http://www.citrusframework.org/** and assert the content on that page.

Now lets add some testing logic that works with content other than HTML dom related content. We add a test step that puts 
focus to the web browser task bar. In detail we click into the browser search input field, fill in a word and perform the search.

```.java
        new Region().find("search").click().type("Citrus");
        env.sleep(1);
        new Region().takeScreenshot("test.png");
```        
        
The `region` object provides access to Sakuli screen related actions such as finding a component on the screen. We can click that region
and fill in some characters (e.g. in the search input field). After that we sleep some time to give the search operation some
time to perform its actions. As a last step we take a screenshot of the result page and the test is finished.

This little example demonstrates the basic usage of the Sakuli Java API. We write normal Java unit tests with TestNG and
add Sakuli specific actions on HTML dom content in a browser or any native application operations by screen related access.

Next we will setup a complete sample project for Sakuli Java.

# Sakuli Java Example 

The next section describes how to get started with the Sakuli Java DSL by example. The Java example is a fully runnable Java
sample test case. So at the end of this chapter you should be able to start writing Sakuli test in pure Java. 

An example how to use Java DSL and setup Maven you will find at:
**[github.com/ConSol/sakuli-examples]**


# Installation

## Preparation

1. Install Java Development Kit version 8.   
2. Install Maven (Version 3.2.5 or higher).
3. Download `java-example` directory from [github.com/ConSol/sakuli-examples].

## Project setup and compilation

1. Import `java-example` to IDE (IntelliJ or Eclipse...) as Maven project:
  **Example for IntelliJ:**
    *   Choose  `Project from Existing Sources...` in File menu.
    *   Choose `pom.xml` and click `next` button till the project is imported.
2. Try to **compile** the new Sakuli Maven project. If an **ERROR** is reported please check your `pom.xml` first. The following section has to be present in your Maven POM:   
       
        <repository>
            <id>labs-consol-snapshots</id>
            <name>ConSol* Labs Repository</name>
            <url>http://labs.consol.de/maven/snapshots-repository</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
            <releases>
                <enabled>false</enabled>
            </releases>
        </repository>
        
The ConSol labs Maven repository should be placed to the **repositories** section in your POM. After this is done please execute the Maven **compile** phase. 
  
## Test execute

Once compilation has been **SUCCESS** try to execute **test** phase as a next step.
  
### Configuration

For customized browser detection create your own `browser_types.xml` file. This file should be located in `main/resources/sahi/userdata/config` package in `src` folder. 
The content of this file looks like follows:

        <browserTypes>
            <browserType>
                <name>firefox</name>
                <displayName>Firefox</displayName>
                <icon>firefox.png</icon>
                <path>$ProgramFiles (x86)\Mozilla Firefox\firefox.exe</path>
                <options>-profile "$userDir/browser/ff/profiles/sahi$threadNo" -no-remote</options>
                <processName>firefox.exe</processName>
                <capacity>5</capacity>
            </browserType>
        
            <browserType>
                <name>firefox4</name>
                <displayName>Firefox 4</displayName>
                <icon>firefox.png</icon>
                <path>$ProgramFiles (x86)\Mozilla Firefox 4.0 Beta 7\firefox.exe</path>
                <options>-profile "$userDir/browser/ff/profiles/sahi$threadNo" -no-remote</options>
                <processName>firefox.exe</processName>
                <capacity>5</capacity>
            </browserType>
        
            <browserType>
                <name>ie</name>
                <displayName>IE</displayName>
                <icon>ie.png</icon>
                <path>$ProgramFiles\Internet Explorer\iexplore.exe</path>
                <options>-noframemerging</options>
                <processName>iexplore.exe</processName>
                <useSystemProxy>true</useSystemProxy>
                <capacity>5</capacity>
            </browserType>
        
            <!-- <browserType>
                <name>ie</name>
                <displayName>IE 8</displayName>
                <icon>ie.png</icon>
                <path>$ProgramFiles (x86)\Internet Explorer\iexplore.exe</path>
                <options>-noframemerging</options>
                <processName>iexplore.exe</processName>
                <useSystemProxy>true</useSystemProxy>
                <capacity>5</capacity>
            </browserType> -->
            <!--
            <browserType>
                <name>ie</name>
                <displayName>IE 7</displayName>
                <icon>ie.png</icon>
                <path>$ProgramFiles (x86)\Internet Explorer\iexplore.exe</path>
                <options></options>
                <processName>iexplore.exe</processName>
                <useSystemProxy>true</useSystemProxy>
                <capacity>5</capacity>
            </browserType>
             -->
        
            <!--
            <browserType>
                <name>ie</name>
                <displayName>IE 6</displayName>
                <icon>ie.png</icon>
                <path>$ProgramFiles (x86)\Internet Explorer\iexplore.exe</path>
                <options></options>
                <processName>iexplore.exe</processName>
                <useSystemProxy>true</useSystemProxy>
                <capacity>5</capacity>
            </browserType>
             -->
        
            <browserType>
                <name>chrome</name>
                <displayName>Chrome</displayName>
                <icon>chrome.png</icon>
                <path>C:\Program Files (x86)\Google\Chrome\Application\chrome.exe</path>
                <options>--incognito --user-data-dir=$userDir\browser\chrome\profiles\sahi$threadNo --proxy-server=localhost:9999 --disable-popup-blocking</options>
                <processName>chrome.exe</processName>
                <capacity>5</capacity>
            </browserType>
        
            <browserType>
                <name>safari</name>
                <displayName>Safari</displayName>
                <icon>safari.png</icon>
                <path>$ProgramFiles (x86)\Safari\Safari.exe</path>
                <options> </options>
                <processName>safari.exe</processName>
                <useSystemProxy>true</useSystemProxy>
                <capacity>1</capacity>
            </browserType>
        
            <browserType>
                <name>opera</name>
                <displayName>Opera</displayName>
                <icon>opera.png</icon>
                <path>$ProgramFiles (x86)\Opera\opera.exe</path>
                <options> </options>
                <processName>opera.exe</processName>
                <useSystemProxy>true</useSystemProxy>
                <capacity>1</capacity>
            </browserType>
        
        </browserTypes>
        
Note: **If needed change the <path> for your own locations of each browser!**

Now you can execute **test** phase and enjoy the successful execution of the test. 

[github.com/ConSol/sakuli-examples]:https://github.com/ConSol/sakuli-examples/tree/master/java-example