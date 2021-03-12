package kz.example.placestovisit.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import kz.example.placestovisit.App
import kz.example.placestovisit.di.modules.*
import javax.inject.Singleton

@Singleton
@Component(
    modules =[
        AndroidInjectionModule::class,
        ActivityModule::class,
        NetworkModule::class,
        RepositoryModule::class,
        StorageModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {

    companion object {
        fun create(application: Application): AppComponent {
            return DaggerAppComponent.builder()
                .application(application)
                .storageModule(StorageModule(application))
                .repositoryModule(RepositoryModule(application))
                .networkModule(NetworkModule(application))
                .build()
        }
    }

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun storageModule(storageModule: StorageModule): Builder

        fun repositoryModule(repositoryModule: RepositoryModule): Builder

        fun networkModule(networkModule: NetworkModule): Builder

        fun build(): AppComponent
    }

}
