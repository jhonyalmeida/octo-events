package tech.jaya.jhony.octoevents.issue

class IssueServiceImpl : IssueService {

    override fun findOrCreate(issue: IssueModel) : Issue {
        val existentIssue = Issue.findById(issue.id)
        return existentIssue ?: Issue.new(issue.id) {
            number = issue.number
            title = issue.title
            state = issue.state
            url = issue.url
        }
    }

}