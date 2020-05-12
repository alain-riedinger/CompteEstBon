@ECHO OFF

REM [Distributing your Application as an Executable JAR file](https://introcs.cs.princeton.edu/java/85application/jar/jar.html)

ECHO Build the application ?
PAUSE
jar cvfe CompteEstBon.jar CompteEstBon.App .\CompteEstBon\*.class

PAUSE
