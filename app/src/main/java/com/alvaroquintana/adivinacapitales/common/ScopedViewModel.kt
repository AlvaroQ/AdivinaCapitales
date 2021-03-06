package com.alvaroquintana.adivinacapitales.common

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import com.alvaroquintana.adivinacapitales.common.Scope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

abstract class ScopedViewModel(uiDispatcher: CoroutineDispatcher = Dispatchers.Main): ViewModel(), Scope by Scope.Impl(
    uiDispatcher
) {
    init {
        this.initScope()
    }

    @CallSuper
    override fun onCleared() {
        destroyScope()
        super.onCleared()
    }
}