package kz.example.placestovisit.di.modules

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import kz.example.placestovisit.api.PlacesToVisitApi
import kz.example.placestovisit.repository.PlacesToVisitRepository
import kz.example.placestovisit.repository.PlacesToVisitRepositoryImpl
import javax.inject.Singleton

@Module
class RepositoryModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideUserRepository(
        placesToVisitApi: PlacesToVisitApi,
        gson: Gson
    ): PlacesToVisitRepository = PlacesToVisitRepositoryImpl(
        placesToVisitApi = placesToVisitApi,
        gson = gson
    )

}
