package tech.jaya.jhony.octoevents.issue

interface IssueService {
    fun findOrCreate(issue: IssueModel) : Issue
}