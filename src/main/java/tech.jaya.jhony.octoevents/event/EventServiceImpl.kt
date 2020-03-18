package tech.jaya.jhony.octoevents.event

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import tech.jaya.jhony.octoevents.issue.IssueDto
import tech.jaya.jhony.octoevents.issue.IssueService
import tech.jaya.jhony.octoevents.issue.Issues
import tech.jaya.jhony.octoevents.repository.RepositoryDto
import tech.jaya.jhony.octoevents.repository.RepositoryModel
import tech.jaya.jhony.octoevents.repository.RepositoryService
import tech.jaya.jhony.octoevents.user.UserDto
import tech.jaya.jhony.octoevents.user.UserService

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