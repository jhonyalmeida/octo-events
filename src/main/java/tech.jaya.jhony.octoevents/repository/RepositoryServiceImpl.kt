package tech.jaya.jhony.octoevents.repository

import tech.jaya.jhony.octoevents.user.UserService

class RepositoryServiceImpl(private val userService: UserService) : RepositoryService<RepositoryDto> {

    override fun findOrCreate(repository: RepositoryDto) : Repository {
        val existentRepository = Repository.findById(repository.id)
        return existentRepository ?: Repository.new(repository.id) {
            fullName = repository.fullName
            ownerUser = userService.findOrCreate(repository.owner)
        }
    }

}