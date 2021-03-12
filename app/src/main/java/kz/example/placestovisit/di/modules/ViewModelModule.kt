package kz.example.placestovisit.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kz.example.placestovisit.ui.main_page.MainPageViewModel
import kz.example.placestovisit.di.DaggerViewModelFactory
import kz.example.placestovisit.di.ViewModelKey

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(
        factory: DaggerViewModelFactory
    ): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainPageViewModel::class)
    internal abstract fun provideMainPageViewModel(viewModel: MainPageViewModel): ViewModel

}