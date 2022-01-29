!#/bin/bash
#java --version
# export JAVA_HOME=/home/tk/.sdkman/candidates/java/11.0.13.fx-zulu
¤ export PATH=$JAVA_HOME/bin:$PATH
dt_exec_path2=$(pwd)

# echo ajohakemisto: $dt_exec_path2
# ls -la $dt_exec_path2/JavaFxmlFileI18Convert.jar
echo java -cp $dt_exec_path2/JavaFxmlFileI18Convert.jar:$CLASSPATH:. com.metait.javafxmlfileI18convert.JavaFxmlFileConver18Application
java -cp $dt_exec_path2/JavaFxmlFileI18Convert.jar:$CLASSPATH:. com.metait.javafxmlfileI18convert.JavaFxmlFileConver18Application
