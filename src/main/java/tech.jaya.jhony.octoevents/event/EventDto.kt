package tech.jaya.jhony.octoevents.event

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import tech.jaya.jhony.octoevents.issue.IssueDto
import tech.jaya.jhony.octoevents.repository.RepositoryDto
import tech.jaya.jhony.octoevents.user.UserDto
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class EventDto(override var action: String,
                    override var issue: IssueDto,
                    override var repository: RepositoryDto,
                    override var sender: UserDto
) : EventModel<IssueDto, RepositoryDto, UserDto> {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    override var createdAt : LocalDateTime = LocalDateTime.now()

    fun createdAt(createdAt: LocalDateTime): EventDto {
        this.createdAt = createdAt
        return this
    }
}