package tech.jaya.jhony.octoevents.repository

interface RepositoryService<in T: RepositoryModel<*>> {
    fun findOrCreate(repository: T) : Repository
}