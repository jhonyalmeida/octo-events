package tech.jaya.jhony.octoevents.issue

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class IssueDto(override var id: Long,
                    override var number: Long,
                    override var title: String,
                    override var state: String,
                    override var url: String) : IssueModel