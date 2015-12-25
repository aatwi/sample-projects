# Java-Ruby-JMeter-Integration
INTRODUCTION
------------------------------------
This tutorial  is intended to show how to integrate ruby-jmeter within your java project.<br/>
I have divided this tutorial into 3 sections. The first one shows how to execute ruby scripts directly from Java, the second section <br/>
shows how to ruby ruby-jmeter scripts from Java. Whereas the last section is dedicated from some refactoring of our test class.<br/>

Executing Ruby Scripts
-------------------------------------

1. **Maven Dependency**

     One of the possible options to execute ruby from java is by using JRuby. <br/>
     Since we are writing a maven project then we need to add JRuby dependency in our pom. <br/>
     To do that we simply add the following dependency to the dependencies section of our pom.xml file.<br/>  
    
        <dependency>
            <groupId>org.jruby</groupId>
            <artifactId>jruby-complete</artifactId>
            <version>9.0.3.0</version>
        </dependency>

2. **RubyExecutor**

    Now we can reference JRuby in our code, so let us start by adding a new java file "RubyExecutor" to our project.<br/> 
    In this class we will add a static method that takes as parameter the full path of the ruby script. <br/>
    Below is the code of the method: <br/>
    
        
        public class RubyExecutor {
            public static void run(String rubyFile) throws FileNotFoundException, ScriptException {
                final ScriptEngine scriptEngine = new JRubyEngineFactory().getScriptEngine();
                final FileReader rubyFileReader = new FileReader(rubyFilePath);
                scriptEngine.eval(rubyFileReader);
                rubyFileReader.close();
            }
        }
    <br/>
    The first line initializes an instance of JRubyEngine.<br/>
    The third line simply calls the eval method that is responsible of executing the ruby code in the JRubyEngine.<br/><br/>   
       
3. **Testing our code** <br/>
    The best way to make sure our code is working as we want is to write JUnit tests. 
    For simplicity, we will make our ruby code generate a new file at the end of the execution. This generated file has the same file name but with "jmx" as an extension.<br/>
    Based on that we should make our test call the method RubyExecutor.run() and assert that a new file has been generated.<br/> <br/>
    First we should start by writing a simple ruby code in the test-resources folder. <br/>
    *Steps:* <br/>
        3.1. Add a new folder "src/test/resources/RubyExecutor"     
        3.2. Add a new ruby file "simpleCode.rb" under that folder <br/>
        3.3. Add the following code to the ruby file. This code will simply create a new empty file with the name "simpleCode.jmx" <br/>
        
        `File.new('target/test-classes/RubyExecutor/simpleCode.jmx', "w+")`
         
    <br/>
    Then we write our test <br/>
    
        @Test
        public void
        it_should_generate_a_file_from_ruby_code() throws IOException, ScriptException {
            final String rubyFile = this.getClass().getResource("/RubyExecutor/simpleCode.rb").getFile();
            RubyExecutor.run(rubyFile);
            final File expectedFile = new File(rubyFile.replace("rb", "jmx"));
            Assertions.assertThat(expectedFile).exists();
        }
   
    The test asks the RubyExecutor to execute a ruby file named "simpleCode.rb" (the one we created in the previous section) and asserts that the file "simpleCode.jmx" has been generated.<br/>
    The test should be green if we run it, and we should notice that the file "simpleCode.jmx" has been created under "target/test-classes/RubyExecutor/"<br/><br/>
    You can use maven to run tests. To do so run the below command from the directory "jruby-jmeter-integration" <br/>
        
        mvn clean install
    
    Below is what you would expect if the tests were successful: <br/>
    
        [INFO] ------------------------------------------------------------------------
        [INFO] BUILD SUCCESS
        [INFO] ------------------------------------------------------------------------
        [INFO] Total time: 01:17 min
    
    *Note: I use the the library org.fest.assertions.Assertions, thus make sure you have added it to your dependencies in maven.*

Executing Ruby-JMeter Scripts
-------------------------------------

Now that we are capable of executing ruby scripts from our project, we will work on executing ruby-jmeter scripts. 

1. **Ruby-JMeter**<br/>
    Ruby-JMeter is a tool that allows you to define your jmeter test-plan with a simple ruby code. <br/>
    Most of the controllers are defined by ruby methods, thus it is a matter of calling the right method with the right parameters. Upon execution, tool will generate a jmx file from your ruby code. <br/> 
    I'm not going to dig into details on how it works, for more details you can have a look at some examples available on project's workspace on [GitHub](https://github.com/flood-io/ruby-jmeter)   
    <br/>
    Now it is time to write a simple test-plan with ruby-jmeter.<br/>
    *Steps:* <br/>
        1.1. Add a new ruby file "src/test/resources/RubyExecutor/simpleJmx.rb" <br/>
        1.2. Add the following code to the ruby file: <br/>
    
        require 'rubygems'
        require 'ruby-jmeter'   
        test do
          threads count: 1, rampup: 0, loops: 1, scheduler: false do
            beanshell_sampler query:'log.info("********Hello World********");'
          end
        end.jmx(file: 'target/test-classes/RubyExecutor/simpleJmx.jmx')
    <br/>
    *Code Details:* <br/>
    The above code will generate the following test-plan:<br/>
    1.2.1. Create a test-plan<br/>
    1.2.2. Adds a new "Thread Group" with a single thread and single loop count<br/>
    1.2.3. Adds a BeanShellSampler as a child of the thread group to print "********Hello World********"  
    <br/> 
    Running the above ruby code will generate the following JMeter test plan:<br/>     
    ![SimpleJmx](/src/test/resources/SimpleJmx.png)<br/>
    <br/>
    <br/>
    
2. **Ruby-JMeter within our maven project**<br/>
    Now we know exactly what our code should do; what is it's input and output. Thus it would be great if we write a simple JUnit test to test our code. <br/>
    Our test should execute the ruby script we wrote in the previous section, and is expected to pass if a respective JMX file was generated at the end of the execution.<br/>
    Based on this, we can add a new test to RubyExecutorTest.java 
    
        @Test
        public void
        it_should_generate_a_jmx_file_from_ruby_code() throws IOException, ScriptException {
            final String rubyFile = this.getClass().getResource("/RubyExecutor/simpleJmx.rb").getFile();
            RubyExecutor.run(rubyFile);
            final File expectedFile = new File(rubyFile.replace("rb", "jmx"));
            Assertions.assertThat(expectedFile).exists();
        }
    
    From this test we expect our code to generate the file "target/test-classes/RubyExecutor/simpleJmx.jmx". <br/>
    Let us give this a try:
     
        mvn clean install
        
    At this stage, our project will fail with the following exception: 

        LoadError: no such file to load -- ruby-jmeter
            require at org/jruby/RubyKernel.java:939
            require at uri:classloader:/META-INF/jruby.home/lib/ruby/stdlib/rubygems/core_ext/kernel_require.rb:54
                <top> at <script>:2
            Tests run: 2, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 11.657 sec <<< FAILURE!
            it_should_generate_a_jmx_file_from_ruby_code(RubyExecutorTest)  Time elapsed: 11.446 sec  <<< ERROR!
            javax.script.ScriptException: org.jruby.embed.EvalFailedException: (LoadError) no such file to load -- ruby-jmeter
                at org.jruby.embed.jsr223.JRubyEngine.wrapException(JRubyEngine.java:104)
                at org.jruby.embed.jsr223.JRubyEngine.eval(JRubyEngine.java:121)
                at org.jruby.embed.jsr223.JRubyEngine.eval(JRubyEngine.java:146)

    The exception is telling us that it couldn't execute ruby-jmeter. Maven wasn't able to locate the ruby-jmeter libraries as we haven't referenced it yet in any of our code. <br/>
    To fix this problem we will have to add more dependencies to our pom file! <br/>
    
3. **Ruby-Gems Dependency**<br/>
    RubyGems is a repository hosting ruby community's gems. Once referenced, maven will access this repository to sync the required ruby libraries locally to your project.<br/>
    In our case we are using the ruby-jmeter gems, to add dependency on that we should modify our pom file as follows: <br/>
    3.1. Add repository: This will be used by maven to download the gems from the specified url  
    
        <repositories>
            <repository>
                <id>rubygems-releases</id>
                <url>http://rubygems-proxy.torquebox.org/releases</url>
            </repository>
        </repositories>
    
    3.2. Add dependency to ruby-jmeter
    
        <dependency>
            <groupId>rubygems</groupId>
            <artifactId>ruby-jmeter</artifactId>
            <version>2.13.8</version>
            <type>gem</type>
        </dependency>
    
    3.3. Add a build step: 
    
        <build>
            <plugins>
                <plugin>
                    <groupId>de.saumya.mojo</groupId>
                    <artifactId>gem-maven-plugin</artifactId>
                    <version>1.1.3</version>
                    <extensions>true</extensions>
                    <executions>
                        <execution>
                            <goals>
                                <goal>initialize</goal>
                            </goals>
                        </execution>
                    </executions>
                    <configuration>
                        <includeRubygemsInResources>true</includeRubygemsInResources>
                    </configuration>
                </plugin>
            </plugins>
        </build>
       
    3.4. Rerun maven
    
        mvn clean install
    
    We should get a Build Success
    
        -------------------------------------------------------
         T E S T S
        -------------------------------------------------------
        Running RubyExecutorTest
        I, [2015-12-23T17:27:19.643000 #7564]  INFO -- : Test plan saved to: target/test-classes/RubyExecutor/simpleJmx.jmx
        Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 7.278 sec
        Results :
        Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
        [INFO]
        [INFO] --- maven-jar-plugin:2.4:jar (default-jar) @ java-ruby-jmeter-integration ---
        [INFO] Building jar: D:\Dev\java\workspace\GitHub\sample-projects\jruby-jmeter-integration\target\java-ruby-jmeter-integration-1.0-SNAPSHOT.jar
        [INFO]
        [INFO] --- maven-install-plugin:2.4:install (default-install) @ java-ruby-jmeter-integration ---
        [INFO] Installing D:\Dev\java\workspace\GitHub\sample-projects\jruby-jmeter-integration\target\java-ruby-jmeter-integration-1.0-SNAPSHOT.jar to D:\Dev\maven_repo\aatwi\github\java-ruby-jmeter-integration\1.0-SNAPSHOT\java-ruby-jmeter-integration-1.0-SNAPSHOT.jar
        [INFO] Installing D:\Dev\java\workspace\GitHub\sample-projects\jruby-jmeter-integration\pom.xml to D:\Dev\maven_repo\aatwi\github\java-ruby-jmeter-integration\1.0-SNAPSHOT\java-ruby-jmeter-integration-1.0-SNAPSHOT.pom
        [INFO] ------------------------------------------------------------------------
        [INFO] BUILD SUCCESS
        [INFO] ------------------------------------------------------------------------
           
Code Refactoring
-------------------------------------
The last part of this tutorial is to do some code refactoring to our test. Although it isn't really essential here, but it is a habit!!<br/>
If we look at our current code, we can notice that there is some code duplication in the 2 tests. <br/>
One thing we can do here is extracting the method "assertJmxFileExists" to remove this duplication.<br/> 
Our new method will take the responsibility of calling RubyExecutor.run and asserting the generation of the JMX file.  <br/><br/>
The code below shows the difference before and after refactoring.<br/>
    Before Refactoring:
    
    @Test
    public void
    it_should_generate_a_file_from_ruby_code() throws IOException, ScriptException {
        final String rubyFile = this.getClass().getResource("/RubyExecutor/simpleCode.rb").getFile();
        RubyExecutor.run(rubyFile);
        final File expectedFile = new File(rubyFile.replace("rb", "jmx"));
        Assertions.assertThat(expectedFile).exists();
    }  
    
    @Test
    public void
    it_should_generate_a_jmx_file_from_ruby_code() throws IOException, ScriptException {
        final String rubyFile = this.getClass().getResource("/RubyExecutor/simpleJmx.rb").getFile();
        RubyExecutor.run(rubyFile);
        final File expectedFile = new File(rubyFile.replace("rb", "jmx"));
        Assertions.assertThat(expectedFile).exists();
    }
<br/>
    After Refactoring:

    @Test
    public void
    it_should_generate_a_file_from_ruby_code() throws IOException, ScriptException {
        assertJmxFileExists("simpleCode.rb");
    }
    
    @Test
    public void
    it_should_generate_a_jmx_file_from_ruby_code() throws IOException, ScriptException {
        assertJmxFileExists("simpleJmx.rb");
    }
    
    private void assertJmxFileExists(final String rubyScript) throws IOException, ScriptException {
        final String rubyFile = this.getClass().getResource("/RubyExecutor/" + rubyScript).getFile();
        RubyExecutor.run(rubyFile);
        final File expectedJmxFile = new File(rubyFile.replace("rb", "jmx"));
        Assertions.assertThat(expectedJmxFile).exists();
    }
    
We can do more refactoring, but I think it is not worth at this point. The code is simple and can be easily read and understood. <br/>
Finally, rerun maven to make sure that nothing was broken with this refactoring.<br/>
 <br/>    
**References**<br/>
[ruby-jmeter](https://github.com/flood-io/ruby-jmeter) <br/>
[Apache Jmeter](http://jmeter.apache.org/)<br/>
[RubyGems](https://rubygems.org/)<br/>