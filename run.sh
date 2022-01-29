# java -version
# set ESPEAK_HOME="."
# SET PATH=%ESPEAK_HOME%;%PATH%
# echo %ESPEAK_HOME%
export DISPLAY=:0
echo java -cp .:./JavaFxmlFileI18Convert.jar;$CLASSPATH com.metait.javafxmlfileI18convert.JavaFxmlFileConver18Application
java -cp .:./JavaFxmlFileI18Convert.jar:$CLASSPATH com.metait.javafxmlfileI18convert.JavaFxmlFileConver18Application
# com.metait.javafxplayer.testapp.HelloWorld
