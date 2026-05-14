package com.davidread.habittracker.common.config

import com.davidread.habittracker.BuildConfig

enum class BuildVariant {
    DEBUG,
    LOCAL_HOST_DEBUG,
    MOCK_IN_APP_DEBUG,
    RELEASE,
    UNKNOWN;

    companion object {
        @Suppress("KotlinConstantConditions")
        fun current(): BuildVariant {
            return when (BuildConfig.BUILD_TYPE) {
                "debug" -> DEBUG
                "localHostDebug" -> LOCAL_HOST_DEBUG
                "mockInAppDebug" -> MOCK_IN_APP_DEBUG
                "release" -> RELEASE
                else -> UNKNOWN
            }
        }
    }
}
