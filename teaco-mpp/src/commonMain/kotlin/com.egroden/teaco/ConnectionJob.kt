package com.egroden.teaco

import kotlinx.coroutines.Job

class ConnectionJob(
    statusJob: Job,
    subscriptionJob: Job
) {
    private var statusJob: Job? = statusJob
    private var subscriptionJob: Job? = subscriptionJob

    fun cancel() {
        statusJob?.cancel()
        statusJob = null
        subscriptionJob?.cancel()
        subscriptionJob = null
    }
}