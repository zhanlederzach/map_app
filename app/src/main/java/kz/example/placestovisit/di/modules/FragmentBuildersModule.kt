package kz.example.placestovisit.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kz.example.placestovisit.ui.main_page.MainPageFragment

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeMainPageFragment(): MainPageFragment

}
