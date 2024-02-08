package org.nekrasov.utils

import io.ktor.server.application.*
import org.nekrasov.exceptions.IncompatibleQueryParameterTypeException
import org.nekrasov.exceptions.MissingQueryParameterException

inline fun <reified T: Number> ApplicationCall.getQueryParameter(name: String): T {
    val value = parameters[name] ?: throw MissingQueryParameterException("Parameter $name not specified in query")
    return when (T::class) {
        Long::class -> value.toLongOrNull() as? T ?: throw IncompatibleQueryParameterTypeException("Parameter $name requires the Long type")
        Int::class -> value.toIntOrNull() as? T ?: throw IncompatibleQueryParameterTypeException("Parameter $name requires the Int type")
        else -> throw IllegalArgumentException("Unsupported parameter type: ${T::class}")
    }
}