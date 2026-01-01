package de.rwth_aachen.phyphox.features.settings.domain.model

enum class AppUiMode(val identifier: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromString(identifier: String): AppUiMode? {
            return entries.find { it.identifier == identifier }
        }
    }
}
