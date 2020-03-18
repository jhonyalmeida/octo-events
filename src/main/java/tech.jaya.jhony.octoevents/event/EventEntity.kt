package tech.jaya.jhony.octoevents.event

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import tech.jaya.jhony.octoevents.issue.Issue
import tech.jaya.jhony.octoevents.issue.Issues
import tech.jaya.jhony.octoevents.repository.Repositories
import tech.jaya.jhony.octoevents.repository.Repository
import tech.jaya.jhony.octoevents.user.User
import tech.jaya.jhony.octoevents.user.Users

object Events : LongIdTable(name = "event") {
    val action = varchar("action", length = 200)
    val createdAt = datetime("created_at")
    val issue = reference("issue_id", refColumn = Issues.id)
    val repository = reference("repository_id", refColumn = Repositories.id)
    val user = reference("user_id", refColumn = Users.id)
}

class Event(id: EntityID<Long>) : LongEntity(id) {

    companion object : LongEntityClass<Event>(Events) {
        fun findByIssueNumber(issueNumber: Long): List<EventDto> {
            return transaction {
                val ownerUser = Users.alias("owner")
                val senderUser = Users.alias("sender")
                val query =  Events.innerJoin(Issues)
                    .innerJoin(Repositories)
                    .join(senderUser, JoinType.INNER, senderUser[Users.id], Repositories.ownerUser)
                    .join(ownerUser, JoinType.INNER, ownerUser[Users.id], Events.user)
                    .select { Issues.number eq issueNumber }
                    .orderBy(Events.createdAt, SortOrder.DESC)

                wrapRows(query).map { event -> event.toModel() }.toList()
            }
        }
    }

    var action by Events.action
    var createdAt by Events.createdAt
    var issue by Issue referencedOn Events.issue
    var repository by Repository referencedOn Events.repository
    var user by User referencedOn Events.user

    fun toModel() : EventDto {
        val issue = issue.toModel()
        val repository = repository.toModel()
        val sender = user.toModel()
        return EventDto(action, issue, repository, sender).createdAt(createdAt)
    }
}