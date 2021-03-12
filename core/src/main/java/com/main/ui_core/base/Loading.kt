package com.main.ui_core.base

sealed class Loading {
    object Show : Loading()
    object Hide : Loading()
}