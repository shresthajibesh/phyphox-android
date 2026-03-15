package de.rwth_aachen.phyphox.utils

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.math.min

/**
 * Extension function that allows to map a collection of items in parallel.
 * takes in mapper function and optionally number of concurrent runs
 * */

suspend fun <O, I> Iterable<O>.parallelMap(
    concurrency: Int =  min(Runtime.getRuntime().availableProcessors(), 8),
    transform: suspend (O) -> I
): List<I> = coroutineScope {

    val semaphore = Semaphore(concurrency)

    map { item ->
        async {
            semaphore.withPermit { transform(item) }
        }
    }.awaitAll()
}
