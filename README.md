# petshop_test

* структура таблиц лежит в src/main/resources/db-schema/
* настройки соединения с базой данных в src/main/resources/petshop_test/db-config.xml
* xml мапперы для работы с mybatis в src/main/resources/petshop_test/mappers
* статичные ресурсы переехали из папки resources в папку webapp
* index.html из папки с ресурсами превратился в webapp/jsp/index.jsp

Немного изменилась команда для запуска:
```
mvn clean package tomcat7:run
```

Увидеть результат после запуска можно по адресу
http://localhost:8080/petshop-test/
