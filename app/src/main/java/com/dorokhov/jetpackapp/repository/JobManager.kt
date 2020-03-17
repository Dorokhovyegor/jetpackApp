package com.dorokhov.jetpackapp.repository

import kotlinx.coroutines.Job

open class JobManager(
    private val className: String
) {
    val TAG = this.javaClass.canonicalName

    private val jobs: HashMap<String, Job> = HashMap()

    fun addJob(methodName: String, job: Job) {
        cancelJob(methodName)
        jobs[methodName] = job
    }

    fun cancelJob(methodName: String) {
        getJob(methodName)?.cancel()
    }

    private fun getJob(methodName: String): Job? {
        if (jobs.containsKey(methodName)) {
            jobs[methodName]?.let {
                return it
            }
        }
        return null
    }

    fun cancelActiveJobs() {
        for ((methodName, job) in jobs) {
            if (job.isActive) {
                println("$TAG: ${className}: cancelling job in method ${methodName} .")
                job.cancel()
            }
        }

    }

}
