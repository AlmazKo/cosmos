cp ../build/libs/annotations-20.1.0.jar .
jdeps --generate-module-info . annotations-20.1.0.jar
javac --patch-module org.jetbrains.annotations=annotations-20.1.0.jar org.jetbrains.annotations/module-info.java
jar -u -f annotations-20.1.0.jar -C org.jetbrains.annotations module-info.class

jar --update --file foo.jar --main-class com.foo.Main --module-version 1.0 -C foo/module-info.class

jar xf

jar --update --file annotations-20.1.0.jar --main-class com.foo.Main --module-version 1.0 -C
org.jetbrains.annotations/module-info.class
