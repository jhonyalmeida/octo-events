package tech.jaya.jhony.octoevents.repository

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import tech.jaya.jhony.octoevents.user.User
import tech.jaya.jhony.octoevents.user.UserDto
import tech.jaya.jhony.octoevents.user.UserService
import tech.jaya.jhony.octoevents.user.Users

class RepositoryServiceImpl(private val userService: UserService) :
    RepositoryService<RepositoryDto> {

    override fun findOrCreate(repository: RepositoryDto) : Repository {
        val existentRepository = Repository.findById(repository.id)
        return existentRepository ?: Repository.new(repository.id) {
            fullName = repository.fullName
            ownerUser = userService.findOrCreate(repository.owner)
        }
    }

}

object Repositories : IdTable<Long>(name = "repository") {
    override val id = long("id").entityId()
    val fullName = varchar("full_name", length = 255)
    val ownerUser = reference("owner_user_id", Users)
    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(Users.id, name = "pk_repository_id") }
}

class Repository(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Repository>(Repositories)

    var fullName by Repositories.fullName
    var ownerUser by User referencedOn Repositories.ownerUser
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class RepositoryDto(override var id: Long,
                         @JsonProperty("full_name") override var fullName: String,
                         override var owner: UserDto
) : RepositoryModel<UserDto>