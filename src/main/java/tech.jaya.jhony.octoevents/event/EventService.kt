package tech.jaya.jhony.octoevents.event

interface EventService<T: EventModel<*, *, *>> {
    fun findByIssueNumber(issueNumber: Long) : Iterable<T>
    fun create(event : T)
}