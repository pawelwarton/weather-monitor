## Skrypt tworzący structurę DB

Skrypt do initicjalizacji bazy danych znajduje się w [V1__init.sql](src/main/resources/db/migration/V1__init.sql).
Schema bazy zostanie stworzona automatycznie przy starcie aplikacji poprzez Flyway.

## Katalog raportów

Katalog do raportów konfiguruje się poprzez property
`app.report.output-dir` w [application.properties](./src/main/resources/application.properties).

## Testy
Do uruchomienia testow **potrzebny jest Docker**.

## Uruchamianie aplikacji

Najproście jest wywołać `#main` w
[TestWeatherMonitorApplication](src/test/java/com/ing/weather_monitor/integration/TestWeatherMonitorApplication.java).\
Podniesie on bazę danych (potrzebny Docker) i zainicjalizuje ją schemą
z [V1__init.sql](src/main/resources/db/migration/V1__init.sql).

Automatyczną inicjalizację bazy można wyłączyć ustawiając `spring.flyway.enabled=false`
w [application.properties](./src/main/resources/application.properties).

Aby podłączyć się do istniejącej instancji PostgreSQL należy skonfigurować property `spring.datasource.*`
i uruchomić aplikację normalnie tj. z klasy
[WeatherMonitorApplication](src/main/java/com/ing/weather_monitor/WeatherMonitorApplication.java).

## Komentarze

### #1
Użyłem PostgreSQL, ale wiem, że istnieją
[bazy danych szeregów czasowych](https://pl.wikipedia.org/wiki/Baza_danych_szereg%C3%B3w_czasowych).

### #2
W przykładzie `"ffa525f2-d996-3z1d-3a7c-316866d3fa2ae"` nie jest poprawnym UUID,
bo zawiera literę "z" i ma o jeden znak za dużo, więc użyłem typu `String` zamiast `java.util.UUID`.\
[PostgreSQL ma dedykowany typ dla UUID](https://www.postgresql.org/docs/current/datatype-uuid.html),
co zajmuje mniej miejsca na dysku niż representacja tekstowa, ale oczywiście odrzuci niepoprawne UUID.

### #3
W przykładzie do `WORST_COUNTRIES_CO_yyyyMM.CSV` jest wpis z liczbą miast równą 1.\
Nie spełnia to reguły conajmniej 2 miast, więc przykład jest błędny.