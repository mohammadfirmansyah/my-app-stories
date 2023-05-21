package com.dicoding.myappstories.db.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.dicoding.myappstories.db.auth.AuthService
import com.dicoding.myappstories.db.remote.RemoteKeysDao
import com.dicoding.myappstories.db.stories.StoriesAppDatabase
import com.dicoding.myappstories.db.stories.StoriesDao
import com.dicoding.myappstories.db.stories.StoriesService
import com.dicoding.myappstories.db.stories.StoriesWidgetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

class AuthPreferenceDataSource @Inject constructor(private val dataStore: DataStore<Preferences>)

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "application")

@Module
@InstallIn(SingletonComponent::class)
class Modules {

    @Provides
    @Singleton
    fun provideAuthPreferences(dataStore: DataStore<Preferences>): AuthPreferenceDataSource =
        AuthPreferenceDataSource(dataStore)

    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore

    @Provides
    fun provideStoriesDao(storyDatabase: StoriesAppDatabase): StoriesDao =
        storyDatabase.getstoriesDao()

    @Provides
    fun provideStoriesDaoWidget(storyDatabase: StoriesAppDatabase): StoriesWidgetDao =
        storyDatabase.getStoriesDaoWidget()

    @Provides
    fun provideRemoteKeysStoryDao(storyDatabase: StoriesAppDatabase): RemoteKeysDao =
        storyDatabase.remoteKeysDao()

    @Provides
    @Singleton
    fun provideStoriesDatabase(@ApplicationContext context: Context): StoriesAppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            StoriesAppDatabase::class.java,
            "stories_database"
        ).build()
    }

    @Provides
    fun providesOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(150, TimeUnit.SECONDS)
            .readTimeout(150, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://story-api.dicoding.dev/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    @Provides
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    fun provideStoryService(retrofit: Retrofit): StoriesService =
        retrofit.create(StoriesService::class.java)

}