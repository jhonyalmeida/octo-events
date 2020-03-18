package tech.jaya.jhony.octoevents.user

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

object Users : IdTable<Long>(name = "user") {
    override val id = long("id").entityId()
    val login = varchar("login", length = 200)
    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id, name = "pk_user_id") }
}

class User(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<User>(Users)

    var login by Users.login

    fun toModel() : UserDto {
        return UserDto(id.value, login)
    }
}