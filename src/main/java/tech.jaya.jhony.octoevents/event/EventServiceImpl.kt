package tech.jaya.jhony.octoevents.event

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import tech.jaya.jhony.octoevents.issue.Issue
import tech.jaya.jhony.octoevents.issue.IssueDto
import tech.jaya.jhony.octoevents.issue.IssueService
import tech.jaya.jhony.octoevents.issue.Issues
import tech.jaya.jhony.octoevents.repository.*
import tech.jaya.jhony.octoevents.user.User
import tech.jaya.jhony.octoevents.user.UserDto
import tech.jaya.jhony.octoevents.user.UserService
import tech.jaya.jhony.octoevents.user.Users
import java.time.LocalDateTime

class EventServiceImpl(private val issueService: IssueService,
                       private val repositoryService: RepositoryService<RepositoryModel<*>>,
                       private val userService: UserService
) : EventService<EventDto> {

    override fun findByIssueNumber(issueNumber: Long) : Iterable<EventDto> {
        return transaction {
            val events = Event.wrapRows(
                Events.innerJoin(Issues).select { Issues.number.eq(issueNumber) }.orderBy(Events.createdAt)
            )
            events.map { event ->
                val issue = IssueDto(
                    event.issue.id.value,
                    event.issue.number,
                    event.issue.title,
                    event.issue.state,
                    event.issue.url
                )
                val owner = UserDto(
                    event.repository.ownerUser.id.value,
                    event.repository.ownerUser.login
                )
                val repository = RepositoryDto(
                    event.repository.id.value,
                    event.repository.fullName,
                    owner
                )
                val sender = UserDto(event.user.id.value, event.user.login)
                EventDto(event.action, issue, repository, sender).createdAt(event.createdAt)
            }
        }
    }

    override fun create(event : EventDto) {
        transaction {
            val createdIssue = issueService.findOrCreate(event.issue)
            val createdRepository = repositoryService.findOrCreate(event.repository)
            val createdSenderUser = userService.findOrCreate(event.sender)
            Event.new {
                action = event.action
                createdAt = event.createdAt
                issue = createdIssue
                repository = createdRepository
                user = createdSenderUser
            }
        }
    }

}

object Events : LongIdTable(name = "event") {
    val action = varchar("action", length = 200)
    val createdAt = datetime("created_at")
    val issue = reference("issue_id", Issues)
    val repository = reference("repository_id", Repositories)
    val user = reference("user_id", Users)
}

class Event(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Event>(Events)

    var action by Events.action
    var createdAt by Events.createdAt
    var issue by Issue referencedOn Events.issue
    var repository by Repository referencedOn Events.repository
    var user by User referencedOn Events.user
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class EventDto(override var action: String,
                    override var issue: IssueDto,
                    override var repository: RepositoryDto,
                    override var sender: UserDto
) : EventModel<IssueDto, RepositoryDto, UserDto> {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    override var createdAt : LocalDateTime = LocalDateTime.now()

    fun createdAt(createdAt: LocalDateTime): EventDto {
        this.createdAt = createdAt
        return this
    }
}