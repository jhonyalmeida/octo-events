package tech.jaya.jhony.octoevents

import io.javalin.Javalin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.KoinComponent
import org.koin.core.inject
import tech.jaya.jhony.octoevents.event.EventDto
import tech.jaya.jhony.octoevents.event.EventService
import tech.jaya.jhony.octoevents.event.Events
import tech.jaya.jhony.octoevents.issue.Issues
import tech.jaya.jhony.octoevents.repository.Repositories
import tech.jaya.jhony.octoevents.user.Users
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Properties

class ApplicationServerComponent : KoinComponent {

    private val eventService by inject<EventService<EventDto>>()
    private var app: Javalin = Javalin.create()
    private var port = 0

    private fun init() {
        val props = loadProperties()
        port = props.getProperty("application.port").toInt()
        configureRoutes()
        configureDatabase(props)
    }

    fun start() {
        if (port == 0) {
            init()
        }
        app.start(port)
    }

    fun stop() {
        app.stop()
    }

    private fun loadProperties() : Properties {
        val filePath = Paths.get(javaClass.classLoader.getResource("application.properties").toURI())
        return Files.newInputStream(filePath).use { `in` ->
            val props = Properties()
            props.load(`in`)
            props
        }
    }

    private fun configureRoutes() {
        app.routes {
            app.get("/issues/:number/events") { ctx ->
                val issueNumber = ctx.pathParam("number").toLong()
                ctx.json(eventService.findByIssueNumber(issueNumber))
            }

            app.post("/events") { ctx ->
                val event = ctx.body<EventDto>()
                eventService.create(event)
            }
        }
    }

    private fun configureDatabase(props: Properties) {
        Database.connect(
            props.getProperty("database.url"),
            driver = props.getProperty("database.driver"),
            user = props.getProperty("database.user"),
            password = props.getProperty("database.password"))

        if (props.getProperty("database.create-on-startup") == "true") {
            transaction {
                SchemaUtils.create(Users, Issues, Repositories, Events)
            }
        }
    }
}