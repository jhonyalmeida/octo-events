package tech.jaya.jhony.octoevents.user

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserDto(override var id: Long, override var login: String) : UserModel