Octo Events
==================

Octo Events is a Kotlin application that listens to Github Events via webhooks and expose by an api for later use. 

Some of the libraries used were: 

* Javalin for the web server
* Exposed for querying the database
* Koin for dependency injection
* JUnit, unirest and assertj for the functional tests

## Running

* Configure properties file _application.properties_ with database connection parameters. Example using a _Postgres_ database:

```
database.url=jdbc:postgresql://localhost:5432/octo-events
database.driver=org.postgresql.Driver
database.user=postgres
database.password=postgres
database.create-on-startup=true
application.port=7000
```

The `create-on-startup` property will automatically create the application database when set to _true_. After first execution, it can be set to _false_.

* Configure a web hook in your repository at <https://github.com> and point the url to the application `POST /events` endpoint. Further help in this task can be found at <https://developer.github.com/webhooks/configuring/>.

* Optionally, configure the webhook secret in the property `application.secret` to validate requests

* Run application and generate events in your repository, like creating and closing an issue. The events should have been registered and can be listed by issue number at `GET /issues/{issueNumber}/events` endpoint.

* You can also generate a self contained jar with `mvn package`, wich can be executed like `java -jar target/octo-events-1.0.0-SNAPSHOT-jar-with-dependencies.jar`

## Testing

There are functional tests implemented for each endpoint, currently using the H2 database for persistence. They were implemented using as example the code shown in Javalin testing page: <https://javalin.io/tutorials/testing>