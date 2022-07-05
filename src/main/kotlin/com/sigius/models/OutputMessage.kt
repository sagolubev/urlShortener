package com.sigius.models

import kotlinx.serialization.Serializable

@Serializable
data class OutputRootMessage(val appName: String)

@Serializable
data class OutputErrorMessage(val errorMessage: String)

@Serializable
data class OutputNoContentMessage(val noContentMessage: String)

@Serializable
data class OutputMessage(val longUrl: String, val shortUrl: String)
