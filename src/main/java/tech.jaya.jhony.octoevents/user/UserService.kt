package tech.jaya.jhony.octoevents.user

interface UserService {
    fun findOrCreate(user: UserModel) : User
}