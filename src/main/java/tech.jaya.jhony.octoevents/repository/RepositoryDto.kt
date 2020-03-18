package tech.jaya.jhony.octoevents.repository

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import tech.jaya.jhony.octoevents.user.UserDto

@JsonIgnoreProperties(ignoreUnknown = true)
data class RepositoryDto(override var id: Long,
                         @JsonProperty("full_name") override var fullName: String,
                         override var owner: UserDto
) : RepositoryModel<UserDto>