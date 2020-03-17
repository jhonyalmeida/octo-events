package tech.jaya.jhony.octoevents.issue

interface IssueModel {
    var id: Long
    var number: Long
    var title: String
    var state: String
    var url: String
}