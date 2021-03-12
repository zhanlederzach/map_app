package kz.example.placestovisit.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kz.example.placestovisit.ui.MainActivity

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity
}