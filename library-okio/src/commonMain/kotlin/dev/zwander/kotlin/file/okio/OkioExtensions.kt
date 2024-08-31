@file:Suppress("unused")

package dev.zwander.kotlin.file.okio

import kotlinx.io.Buffer
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import kotlinx.io.readByteArray
import okio.Timeout

fun RawSource.toOkioSource(): okio.Source {
    return object : okio.Source {
        override fun close() {
            this@toOkioSource.close()
        }

        override fun read(sink: okio.Buffer, byteCount: Long): Long {
            val intermediateBuffer = Buffer()
            val read = this@toOkioSource.readAtMostTo(intermediateBuffer, byteCount)

            sink.write(intermediateBuffer.readByteArray())

            return read
        }

        override fun timeout(): Timeout {
            return Timeout.NONE
        }
    }
}

fun okio.Source.toKotlinSource(): RawSource {
    return object : RawSource {
        override fun close() {
            this@toKotlinSource.close()
        }

        override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
            val intermediateBuffer = okio.Buffer()
            val read = this@toKotlinSource.read(intermediateBuffer, byteCount)

            sink.write(intermediateBuffer.readByteArray())

            return read
        }
    }
}

fun RawSink.toOkioSink(): okio.Sink {
    return object : okio.Sink {
        override fun close() {
            this@toOkioSink.close()
        }

        override fun flush() {
            this@toOkioSink.flush()
        }

        override fun timeout(): Timeout {
            return Timeout.NONE
        }

        override fun write(source: okio.Buffer, byteCount: Long) {
            val intermediateBuffer = Buffer()
            val okioBuffer = okio.Buffer()
            val actualCount = source.read(okioBuffer, byteCount)

            intermediateBuffer.write(okioBuffer.readByteArray())
            this@toOkioSink.write(intermediateBuffer, actualCount)
        }
    }
}

fun okio.Sink.toKotlinSink(): RawSink {
    return object : RawSink {
        override fun close() {
            this@toKotlinSink.close()
        }

        override fun flush() {
            this@toKotlinSink.flush()
        }

        override fun write(source: Buffer, byteCount: Long) {
            val intermediateBuffer = okio.Buffer()
            val kotlinBuffer = Buffer()
            val actualCount = source.readAtMostTo(kotlinBuffer, byteCount)

            intermediateBuffer.write(kotlinBuffer.readByteArray())
            this@toKotlinSink.write(intermediateBuffer, actualCount)
        }
    }
}
