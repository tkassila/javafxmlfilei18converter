# javafxmlfilei18converter
JavaFx application, which is converting a fxml file into i18 format and can edit lang properties files also.

Case 1
======

1) Open and convert normal .fxml file into i18 % variable file. Save it
2) After Convert button press, save also lang with name like 'app_name_en.properties'.
3) Change your app to load this modified .fxml file with current java locale. To see an example code,
   open help dialog of the application and add corresponding code to yout app.

Case 2
======

1) Open and convert this kind of modified .fxml file into i18 % variable file. Save it over earlier template .fxml file.
2) After Convert button press, open erarlier lang with name like 'app_name_en.properties' and add new % variables into it.
3) Start your app with new file contents.

Case 3
======

1) Open and edit language 1 .properties and language 2 .properties files. 
2) After edit, save lang files whith names like 'app_name_en.properties' and like 'app_name_fi.properties'
3) Start your app with new file contents.

This app is using language combo to load i18 language ui values.