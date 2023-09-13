package net.artux.pda.model.news

import java.time.Instant

class ArticleModel {
    var id: String? = null
    var title: String? = null
    var image: String? = null
    var url: String? = null
    var tags: List<String>? = null
    var description: String? = null
    var published: Instant? = null
    var likes: Int = 0
    var comments: Int = 0
}