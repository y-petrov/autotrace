# autotrace
The project assists the Java apps troubleshooting via automatic adding entry/exit logging to the methods of the project classes. Technically speaking, the project provides Java8 compiler plugin that automatically adds a trace logging to your code during compilation.

The example below shows how to use it and what value it brings.

Let's say we have test Java class [test/test/subpkg/yp/Test01.java](./src/test/resources/test/test/subpkg/yp/Test01.java).
We compile it and then run:
```console
user@host:~/git/autotrace/src/test/resources> javac test/test/subpkg/yp/Test01.java 
user@host:~/git/autotrace/src/test/resources> java test.test.subpkg.yp.Test01
hardcoded - static init 1
hardcoded - instance static init 1 - nested block
hardcoded - instance init block 1
hardcoded - constructor without param
hardcoded - in main
user@host:~/git/autotrace/src/test/resources>
```

Good, it works as designed. Now we want to see the execution tracing. To do this we need ro recompile the source with autotracer plugin:
```console
user@host:~/git/autotrace/src/test/resources> javac -cp ../../../target/auto-tracer-0.1.jar -Xplugin:AutoTracerPlugin test/test/subpkg/yp/Test01.java
test/test/subpkg/yp/Test01.java:70: warning: No Autotrace for empty block
        {
        ^
1 warning
user@host:~/git/autotrace/src/test/resources>
```
Autotster printed one warning - it's OK, the empty blocks `{ }`, both method and class init, are not augmented with entry/exit logging.

Let's run it:
```console
user@host:~/git/autotrace/src/test/resources> java test.test.subpkg.yp.Test01
hardcoded - static init 1
hardcoded - instance static init 1 - nested block
hardcoded - instance init block 1
hardcoded - constructor without param
hardcoded - in main
user@host:~/git/autotrace/src/test/resources>
```
Oops... No traces... :(
It's OK, we're almost there. We need to make last step - we need to tell to Java logging framework that we want see the output of `.entering()` and `.exiting()` methods of the logger named `test.test.subpkg.yp.Test01`. There is the number of ways to do this, we'll go with config file approach. We create the config file `my-test-logging.properties` in the current directory. Here is its content:
```properties
# Let's use only console output
handlers= java.util.logging.ConsoleHandler

# Default logging level
.level= INFO

# Setup console output
java.util.logging.ConsoleHandler.level = FINEST
java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter

# Now enable entry/exit for one of the parents of the logger of our test class
test.test.subpkg.level = FINEST
```

Now we run our code again. This time we tell JVM were logging config must be taken from:
```console
user@host:~/git/autotrace/src/test/resources> java -Djava.util.logging.config.file=./my-test-logging.properties test.test.subpkg.yp.Test01
```
...and here is the output:
```console
Apr 16, 2018 10:51:34 AM test.test.subpkg.yp.Test01 <static init>
FINER: ENTRY
hardcoded - static init 1
hardcoded - instance static init 1 - nested block
Apr 16, 2018 10:51:34 AM test.test.subpkg.yp.Test01 <static init>
FINER: RETURN
Apr 16, 2018 10:51:34 AM test.test.subpkg.yp.Test01 main
FINER: ENTRY
Apr 16, 2018 10:51:34 AM test.test.subpkg.yp.Test01 <instance init>
FINER: ENTRY
hardcoded - instance init block 1
Apr 16, 2018 10:51:34 AM test.test.subpkg.yp.Test01 <instance init>
FINER: RETURN
Apr 16, 2018 10:51:34 AM test.test.subpkg.yp.Test01 <instance init>
FINER: ENTRY
Apr 16, 2018 10:51:34 AM test.test.subpkg.yp.Test01 <instance init>
FINER: RETURN
Apr 16, 2018 10:51:34 AM test.test.subpkg.yp.Test01 <init>
FINER: ENTRY
hardcoded - constructor without param
Apr 16, 2018 10:51:34 AM test.test.subpkg.yp.Test01 <init>
FINER: RETURN
hardcoded - in main
Apr 16, 2018 10:51:34 AM test.test.subpkg.yp.Test01 m1
FINER: ENTRY
Apr 16, 2018 10:51:34 AM test.test.subpkg.yp.Test01 m1
FINER: RETURN
Apr 16, 2018 10:51:34 AM test.test.subpkg.yp.Test01 main
FINER: RETURN
user@host:~/git/autotrace/src/test/resources>
```

In general - that's it. In my past when I had to find why JavaEE app does something wrong, the enabling tracing helped ne a lot. I hope this plugin will help you too.
