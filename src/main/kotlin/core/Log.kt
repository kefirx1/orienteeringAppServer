package pl.dev.bkwiatkowski.core

import org.slf4j.LoggerFactory

object Log {
    private val logger = LoggerFactory.getLogger("ApplicationLogger")

    fun info(message: String) {
        logger.info(message)
    }

    fun warn(message: String) {
        logger.warn(message)
    }

    fun error(message: String, throwable: Throwable? = null) {
        logger.error(message, throwable)
    }
}
