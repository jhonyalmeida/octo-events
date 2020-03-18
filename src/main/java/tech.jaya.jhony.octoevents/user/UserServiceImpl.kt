package tech.jaya.jhony.octoevents.user

class UserServiceImpl : UserService {

    override fun findOrCreate(user: UserModel) : User {
        val existentUser = User.findById(user.id)
        return existentUser ?: User.new(user.id) { login = user.login }
    }

}