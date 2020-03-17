package tech.jaya.jhony.octoevents

import org.koin.core.context.startKoin
import org.koin.dsl.module
import tech.jaya.jhony.octoevents.event.*
import tech.jaya.jhony.octoevents.issue.IssueService
import tech.jaya.jhony.octoevents.issue.IssueServiceImpl
import tech.jaya.jhony.octoevents.repository.RepositoryModel
import tech.jaya.jhony.octoevents.repository.RepositoryService
import tech.jaya.jhony.octoevents.repository.RepositoryServiceImpl
import tech.jaya.jhony.octoevents.user.UserService
import tech.jaya.jhony.octoevents.user.UserServiceImpl

val mainModule = module {
    single { IssueServiceImpl() as IssueService }
    single { UserServiceImpl() as UserService }
    single { RepositoryServiceImpl(get()) as RepositoryService<*> }
    single { EventServiceImpl(get(), get(), get()) as EventService<*> }
}

fun main(args: Array<String>) {
    startKoin {
        modules(mainModule)
    }
    ApplicationServerComponent().start()
}