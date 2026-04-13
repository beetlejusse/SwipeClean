package com.app.swipeclean.di
import android.content.Context
import androidx.room.Room
import com.app.swipeclean.data.local.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module

@InstallIn(SingletonComponent::class) // Lives for the entire app lifecycle
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "snapswipe.db")
            .fallbackToDestructiveMigration() // Dev only — replace with migrations in prod
            .build()
    @Provides @Singleton
    fun provideTrashDao(db: AppDatabase): TrashDao = db.trashDao()
    @Provides @Singleton
    fun provideSessionDao(db: AppDatabase): SessionDao = db.sessionDao()
}