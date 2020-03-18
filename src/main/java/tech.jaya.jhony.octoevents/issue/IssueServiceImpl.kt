package tech.jaya.jhony.octoevents.issue

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

class IssueServiceImpl : IssueService {

    override fun findOrCreate(issue: IssueModel) : Issue {
        val existentIssue = Issue.findById(issue.id)
        return existentIssue ?: Issue.new(issue.id) {
            number = issue.number
            title = issue.title
            state = issue.state
            url = issue.url
        }
    }

}

object Issues : IdTable<Long>(name = "issue") {
    override val id = long("id").entityId()
    val number = long("number").index()
    val title = varchar("title", length = 255)
    val state = varchar("state", length = 100)
    val url = varchar("url", length = 255)
    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id, name = "pk_issue_id") }
}

class Issue(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Issue>(Issues)

    var number by Issues.number
    var title by Issues.title
    var state by Issues.state
    var url by Issues.url
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class IssueDto(override var id: Long,
                    override var number: Long,
                    override var title: String,
                    override var state: String,
                    override var url: String) : IssueModel
