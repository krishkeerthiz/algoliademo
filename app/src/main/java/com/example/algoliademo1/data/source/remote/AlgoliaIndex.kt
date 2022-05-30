package com.example.algoliademo1.data.source.remote

import com.algolia.search.client.ClientSearch
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import io.ktor.client.features.logging.*


class AlgoliaIndex {

    companion object {

        private const val ApplicationID = "9N1YDJJ8DK"
        //private const val APIKEY = "dcd5088a151c2e8db47aec60ea0eb6ec"

        private const val AdminKey = "5f0eac9f0d0da0cc008779c9393a44c7"
        private var client: ClientSearch? = null

        fun getClient(): ClientSearch {
            return client ?: ClientSearch(
                ApplicationID(ApplicationID),
                APIKey(AdminKey),
                LogLevel.ALL
            )
        }


    }
}