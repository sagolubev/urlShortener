package com.sigius.models

import kotlinx.serialization.Serializable

@Serializable
data class UrlItem(val id: String, val longUrl: String, val shortUrl: String)

fun getUrlFromStorage(item_to_find: String): UrlItem? {
    for (i in urlStorage) {
        if (item_to_find == i.id) {
            return i
        }
    }
    return null
}

val urlStorage = mutableListOf<UrlItem>()

// interface UrlStorage {
//
//    fun upsertUrl(hash: String, longUrl: String): CompletableFuture<Unit>
//    fun findUrl(hash: String): CompletableFuture<UrlItem?>
// }
//
// class InMemoryStorage : UrlStorage {
//
//    private val list: MutableList<UrlItem> = mutableListOf()
//
//    override fun upsertUrl(hash: String, longUrl: String): CompletableFuture<Unit> {
//        TODO("Not yet implemented")
//    }
//
//    override fun findUrl(hash: String): CompletableFuture<UrlItem?> {
//        TODO("Not yet implemented")
//    }
// }
//
// class CassandraStorage: UrlStorage {
//
//    val casClient = ...
//
//    init {
//
//
//    }
//
//    override fun upsertUrl(hash: String, longUrl: String): CompletableFuture<Unit> {
//        TODO("Not yet implemented")
//    }
//
//    override fun findUrl(hash: String): CompletableFuture<UrlItem?> {
//
//        TODO("Not yet implemented")
//    }
// }
