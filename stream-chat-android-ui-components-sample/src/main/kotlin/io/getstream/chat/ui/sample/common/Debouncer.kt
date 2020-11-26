package io.getstream.chat.ui.sample.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Utility class for debouncing high frequency events.
 *
 * [submit]ting a new piece of work to run within the debounce window
 * will cancel the previously submitted pending work.
 */
class Debouncer(private val debounceMs: Long) {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    fun submit(work: () -> Unit) {
        job?.cancel()
        job = scope.launch {
            delay(debounceMs)
            work()
        }
    }

    /**
     * Cleans up any pending work.
     *
     * Note that a shut down Debouncer will never execute work again.
     */
    fun shutdown() {
        scope.cancel()
    }
}
