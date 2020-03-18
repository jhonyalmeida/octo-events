package tech.jaya.jhony.octoevents.repository

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import tech.jaya.jhony.octoevents.user.User
import tech.jaya.jhony.octoevents.user.Users

object Repositories : IdTable<Long>(name = "repository") {
    override val id = long("id").entityId()
    val fullName = varchar("full_name", length = 255)
    val ownerUser = reference("owner_user_id", refColumn = Users.id)
    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id, name = "pk_repository_id") }
}

class Repository(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Repository>(Repositories)

    var fullName by Repositories.fullName
    var ownerUser by User referencedOn Repositories.ownerUser

    fun toModel() : RepositoryDto {
        return RepositoryDto(id.value, fullName, ownerUser.toModel())
    }
}