package tech.jaya.jhony.octoevents.event

import org.jetbrains.exposed.sql.transactions.transaction
import tech.jaya.jhony.octoevents.issue.IssueService
import tech.jaya.jhony.octoevents.repository.RepositoryModel
import tech.jaya.jhony.octoevents.repository.RepositoryService
import tech.jaya.jhony.octoevents.user.UserService

class EventServiceImpl(private val issueService: IssueService,
                       private val repositoryService: RepositoryService<RepositoryModel<*>>,
                       private val userService: UserService
) : EventService<EventDto> {

    override fun findByIssueNumber(issueNumber: Long) : Iterable<EventDto> {
        return Event.findByIssueNumber(issueNumber)
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