package tech.jaya.jhony.octoevents.event

import tech.jaya.jhony.octoevents.issue.IssueModel
import tech.jaya.jhony.octoevents.repository.RepositoryModel
import tech.jaya.jhony.octoevents.user.UserModel
import java.time.LocalDateTime

interface EventModel<I: IssueModel, R: RepositoryModel<U>, U: UserModel> {
    var action: String
    var createdAt: LocalDateTime
    var issue: I
    var repository: R
    var sender: U
}