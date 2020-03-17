package tech.jaya.jhony.octoevents.repository

import tech.jaya.jhony.octoevents.user.UserModel

interface RepositoryModel<U: UserModel> {
    var id: Long
    var fullName: String
    var owner: U
}