package com.premiere.di

import com.premiere.api.PremiereApi
import com.premiere.api.createPremiereApi
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

private const val API_URL = "https://rma.finlab.rs/"

val networkingModule = module {
    single<Json> {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
        }
    }

    single {
        HttpClient {
            install(ContentNegotiation) {
                json(get<Json>())
            }
        }
    }

    single<Ktorfit> {
        Ktorfit.Builder()
            .baseUrl(API_URL)
            .httpClient(get<HttpClient>())
            .build()
    }

    single<PremiereApi> {
        get<Ktorfit>().createPremiereApi()
    }
}