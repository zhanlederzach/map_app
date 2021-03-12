package com.main.ui_core.extensions

import timber.log.Timber

fun logger(tag: String, value: Any) {
    when (value) {
        is String -> Timber.tag(tag).d(value)
        is Int -> Timber.tag(tag).d(value.toString())
        is Double -> Timber.tag(tag).d(value.toString())
        is Boolean -> Timber.tag(tag).d(value.toString())
    }
}