@ echo off
call \java\jdk11fxcp.bat
rem java -version
echo %0
set dt_exec_drive=%~d0
set dt_exec_path=%~p0
set dt_exec_path2=%dt_exec_drive%%dt_exec_path%
echo ajohakemisto: %dt_exec_path2%
set dtcp=%dt_exec_path2%;%dt_exec_path2%dtbook2asciimath.jar;.
rem SET PATH=%ESPEAK_HOME%;%PATH%
rem echo %ESPEAK_HOME%
echo java -cp %dt_exec_path2%JavaFxmlFileI18Convert.jar;%CLASSPATH% com.metait.javafxmlfileI18convert.JavaFxmlFileConver18Application
java -cp %dt_exec_path2%JavaFxmlFileI18Convert.jar;%CLASSPATH% com.metait.javafxmlfileI18convert.JavaFxmlFileConver18Application
rem com.metait.javafxplayer.testapp.HelloWorld
rem pause