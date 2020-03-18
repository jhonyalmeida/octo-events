package tech.jaya.jhony.octoevents.issue

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

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