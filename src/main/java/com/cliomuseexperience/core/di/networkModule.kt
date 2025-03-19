package com.cliomuseexperience.core.di

import android.util.Log
import com.cliomuseexperience.core.api.ApiService
import com.cliomuseexperience.experiencecliomuse.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.app.Application
import com.cliomuseexperience.experiencecliomuse.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {



    @Provides
    @Singleton
    @Named("socketTimeoutSDK")
    fun provideSocketTimeout(context: Application): Int {
        return context.resources.getInteger(R.integer.socketTimeout)
    }


    @Provides
    @Singleton
    @Named("connectionTimeoutSDK")
    fun provideConnectionTimeout(context: Application): Int {
        return context.resources.getInteger(R.integer.connectionTimeout)
    }


    @Named("sdk")
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        // Crea un interceptor de logging personalizado que verifica si el mensaje es un JSON
        val interceptor = HttpLoggingInterceptor { message ->
            if (message.startsWith("{") && message.endsWith("}") || message.startsWith("[") && message.endsWith(
                    "]"
                )
            ) {
                try {
                    val prettyPrintJson = JSONObject(message).toString(4)
                    Log.d("RetrofitLoggingInterceptor", prettyPrintJson)
                } catch (e: Exception) {
                    Log.d("RetrofitLoggingInterceptor", message)
                }
            } else {
                Log.d("RetrofitLoggingInterceptor", message)
            }
        }.apply {
            // Configura el nivel de logging para capturar el cuerpo de las respuestas
            level = HttpLoggingInterceptor.Level.BODY
        }

        return interceptor
    }


    @Named("sdk")
    @Provides
    @Singleton
    internal fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        @Named("socketTimeout") socketTimeout: Int,
        @Named("connectionTimeout") connectionTimeout: Int
    ): OkHttpClient {
        val clientBuilder = OkHttpClient().newBuilder()
        clientBuilder.readTimeout(socketTimeout.toLong(), TimeUnit.SECONDS)
        clientBuilder.connectTimeout(connectionTimeout.toLong(), TimeUnit.SECONDS)
        clientBuilder.addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val requestOriginal = chain.request()
                val request = requestOriginal.newBuilder()
                    .method(requestOriginal.method, requestOriginal.body)
                    .build()
                chain.proceed(request)
            }
        //TODO Add pinning and cert verification.
        return clientBuilder.build()
    }

    @Named("sdk")
    @Provides
    @Singleton
    internal fun provideRetrofitClient(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.HOST)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideApiServiceApp(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}