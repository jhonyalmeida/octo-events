package tech.jaya.jhony.octoevents.user

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

class UserServiceImpl : UserService {

    override fun findOrCreate(user: UserModel) : User {
        val existentUser = User.findById(user.id)
        return existentUser ?: User.new(user.id) { login = user.login }
    }

}

object Users : IdTable<Long>(name = "user") {
    override val id = long("id").entityId()
    val login = varchar("login", length = 200)
    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id, name = "pk_user_id") }
}

class User(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<User>(Users)

    var login by Users.login
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserDto(override var id: Long, override var login: String) : UserModel