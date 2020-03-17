package tech.jaya.jhony.octoevents

import io.javalin.plugin.json.JavalinJson
import kong.unirest.HttpResponse
import kong.unirest.Unirest
import org.assertj.core.api.Assertions.assertThat
import org.junit.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import tech.jaya.jhony.octoevents.event.EventDto
import tech.jaya.jhony.octoevents.issue.IssueDto
import tech.jaya.jhony.octoevents.repository.RepositoryDto
import tech.jaya.jhony.octoevents.user.UserDto
import java.time.LocalDateTime

class ApplicationServerComponentTest {

    private val events = createEvents()

    companion object {
        val app = ApplicationServerComponent()

        @JvmStatic
        @BeforeClass
        fun setUp() {
            startKoin { modules(mainModule) }
            app.start()
        }

        @JvmStatic
        @AfterClass
        fun tearDown() {
            app.stop()
            stopKoin()
        }
    }

    @Test
    fun `POST to create github events`() {
        events.forEach { event ->
            val response: HttpResponse<String> = Unirest.post("http://localhost:7001/events")
                .body(JavalinJson.toJson(event))
                .asString()

            assertThat(response.status).isEqualTo(200)
        }
    }

    @Test
    fun `GET to fetch github events by issue number returns list of events`() {
        val response: HttpResponse<String> = Unirest.get("http://localhost:7001/issues/1/events").asString()
        assertThat(response.status).isEqualTo(200)
        assertThat(response.body).isEqualTo(JavalinJson.toJson(events.filter { it.issue.number == 1L }))
    }
}

fun createEvents() : List<EventDto> {
    val user1 = UserDto(1, "user1")
    val user2 = UserDto(2, "user2")
    val repository = RepositoryDto(1, "octocat/Hello-World", user1)
    val issue1 = IssueDto(1, 1, "issue 1", "closed", "https://api.github.com/repos/octocat/Hello-World/issues/1")
    val issue2 = IssueDto(2, 2, "issue 2", "open", "https://api.github.com/repos/octocat/Hello-World/issues/2")
    return listOf(
        EventDto("created", issue1, repository, user2).createdAt(LocalDateTime.of(2020, 3, 16, 12, 30)),
        EventDto("closed", issue1, repository, user1).createdAt(LocalDateTime.of(2020, 3, 16, 23, 15)),
        EventDto("created", issue2, repository, user2).createdAt(LocalDateTime.of(2020, 3, 17, 10, 45))
    )
}