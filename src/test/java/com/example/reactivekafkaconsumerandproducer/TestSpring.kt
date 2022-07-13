package com.example.reactivekafkaconsumerandproducer

import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.BiConsumer
import java.util.function.Consumer

@SpringBootTest
class TestSpring {

    var log: Logger = LoggerFactory.getLogger(ArithmeticException::class.java)

    @Test
    fun exponentialRepeatScenario2(): Flux<String>? {
        val i = AtomicInteger()
        return Mono.create { s: MonoSink<String> ->
            if (i.incrementAndGet() == 4) {
                s.success("hey")
            } else {
                s.success()
            }
        }.repeatWhen { repeat: Flux<Long?> ->
            repeat.zipWith(
                Flux.range(1, 3)
            ) { t1: Long?, t2: Int? -> t2 }
        }
    }

    @Test
    fun testTime() {
        println(parseT140LocalDateTime("T140-00000024556-2021-12-17-T1408871035105.ASCII.T140.20211217123729.txt"))
    }

    private val year = IntRange(0, 4)
    private val month = IntRange(4, 6)
    private val dayOfMonth = IntRange(6, 8)
    private val hour = IntRange(8, 10)
    private val minute = IntRange(10, 12)
    private val second = IntRange(12, 14)
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("uuuuMMddHHmmss")

    private fun parseT140LocalDateTime(filename: String): LocalDateTime {
        val splitFileName = filename.split(".")
        val notParsedDate = splitFileName[splitFileName.size - 3]
        return parseToLocalDateTime("20211217123729")
    }

    private fun parseToLocalDateTime(notParsedDate: String): LocalDateTime {
        return LocalDateTime.parse(notParsedDate, dateTimeFormatter)
    }

    @Test
    fun testEmpty() {
        Flux.empty<Result<String>>().collectList()
            .subscribe{
                log.info("Size:" + it.firstOrNull())
            }
    }

    @Test
    fun onErrorReturnDirectly_Mono() {
        Flux.range(0, 10)
            .map { i: Int -> i / i } // will produce ArithmeticException
            .onErrorReturn(4)
            .doOnNext { num: Int? ->
                log.info("doOnNext: {}", num)
            }
            .subscribe { num: Int? -> log.info("Number: {}", num) }
    }

    @Test
    fun onErrorReturnIfArithmeticException_Mono() {
        Flux.range(0, 10)
            .map { i: Int -> i / i } // will produce ArithmeticException
            .onErrorReturn<ArithmeticException>(ArithmeticException::class.java, 4)
            .doOnNext { num: Int? ->
                log.info("doOnNext: {}", num)
            }
            .subscribe { num: Int? -> log.info("Number: {}", num) }
    }

    @Test
    fun onErrorReturnIfPredicatePasses_Mono() {
        Flux.range(0, 10)
            .map { i: Int -> i / i } // will produce ArithmeticException
            .onErrorReturn({ error: Throwable? -> error is ArithmeticException }, 4)
            .doOnNext { num: Int? ->
                log.info("doOnNext: {}", num)
            }
            .subscribe { num: Int? -> log.info("Number: {}", num) }
    }

    @Test
    fun onErrorResume_Mono() {
        Flux.range(0, 10)
            .map { i: Int -> i / i } // will produce ArithmeticException
            .onErrorResume { error: Throwable? ->
                Mono.just(
                    4
                )
            }
            .doOnNext { num: Int? ->
                log.info("doOnNext: {}", num)
            }
            .subscribe { num: Int? -> log.info("Number: {}", num) }
    }

    @Test
    fun onErrorResumeIfArithmeticException_Mono() {
        Flux.range(0, 10)
            .map { i: Int -> i / i } // will produce ArithmeticException
            .onErrorResume(
                ArithmeticException::class.java
            ) { error: ArithmeticException? ->
                Mono.just(
                    4
                )
            }
            .doOnNext { num: Int? ->
                log.info("doOnNext: {}", num)
            }
            .subscribe { num: Int? -> log.info("Number: {}", num) }
    }

    @Test
    fun onErrorResumeIfPredicatePasses_Mono() {
        Flux.range(0, 10)
            .map { i: Int -> i / i } // will produce ArithmeticException
            .onErrorResume(
                { error: Throwable? -> error is ArithmeticException }
            ) { error: Throwable? -> Mono.just(4) }
            .doOnNext { num: Int? ->
                log.info("doOnNext: {}", num)
            }
            .subscribe { num: Int? -> log.info("Number: {}", num) }
    }

    @Test
    fun onErrorContinue_Mono() {
        Flux.range(0, 10)
            .map { i: Int -> i / i } // will produce ArithmeticException
            .onErrorContinue { error: Throwable?, obj: Any? ->
                log.info(
                    "error:[{}], obj:[{}]",
                    error,
                    obj
                )
            }
            .doOnNext { num: Int? ->
                log.info("doOnNext: {}", num)
            }
            .subscribe { num: Int? -> log.info("Number: {}", num) }
    }

    @Test
    fun onErrorContinueIfArithmeticException_Mono() {
        Flux.range(0, 10)
            .map { i: Int -> i / i } // will produce ArithmeticException
            .onErrorContinue<ArithmeticException>(
                ArithmeticException::class.java,
                BiConsumer { error: Throwable?, obj: Any? ->
                    log.info(
                        "error:[{}], obj:[{}]",
                        error,
                        obj
                    )
                }
            ).doOnNext { num: Int? ->
                log.info("doOnNext: {}", num)
            }
            .subscribe(Consumer { num: Int? -> log.info("Number: {}", num) })
    }

    @Test
    fun onErrorContinueIfPredicatePasses_Mono() {
        Flux.range(0, 10)
            .map { i: Int -> i / i } // will produce ArithmeticException
            .onErrorContinue(
                { error: Throwable? -> error is ArithmeticException }
            ) { error: Throwable?, obj: Any? ->
                log.info(
                    "error:[{}], obj:[{}]",
                    error,
                    obj
                )
            }.doOnNext { num: Int? ->
                log.info("doOnNext: {}", num)
            }
            .subscribe { num: Int? -> log.info("Number: {}", num) }
    }

    @Test
    fun doOnError_Mono() {
        Flux.range(0, 10)
            .map { i: Int -> i / i } // will produce ArithmeticException
            .doOnError { error: Throwable? -> log.info("caught error") }
            .doOnNext { num: Int? ->
                log.info("doOnNext: {}", num)
            }
            .subscribe { num: Int? -> log.info("Number: {}", num) }
    }

    @Test
    fun doOnErrorIfArithmeticException_Mono() {
        Flux.range(0, 10)
            .map { i: Int -> i / i } // will produce ArithmeticException
            .doOnError(
                ArithmeticException::class.java
            ) { error: ArithmeticException? -> log.info("caught error") }
            .doOnNext { num: Int? ->
                log.info("doOnNext: {}", num)
            }
            .subscribe { num: Int? -> log.info("Number: {}", num) }
    }

    @Test
    fun doOnErrorIfPredicatePasses_Mono() {
        Flux.range(0, 10)
            .map { i: Int -> i / i } // will produce ArithmeticException
            .doOnError(
                { error: Throwable? -> error is ArithmeticException }
            ) { error: Throwable? -> log.info("caught error") }
            .doOnNext { num: Int? ->
                log.info("doOnNext: {}", num)
            }
            .subscribe { num: Int? -> log.info("Number: {}", num) }
    }

    @Test
    fun OnErrorMap_Mono() {
        Flux.range(0, 10)
            .map { i: Int -> i / i } // will produce ArithmeticException
            .onErrorMap { error: Throwable? -> RuntimeException("SomeMathException") }
            .doOnNext { num: Int? ->
                log.info("doOnNext: {}", num)
            }
            .subscribe { num: Int? -> log.info("Number: {}", num) }
    }

    @Test
    fun OnErrorMapIfArithmeticException_Mono() {
        Flux.range(0, 10)
            .map { i: Int -> i / i } // will produce ArithmeticException
            .onErrorMap(
                ArithmeticException::class.java
            ) { error: ArithmeticException? -> RuntimeException("SomeMathException") }
            .doOnNext { num: Int? ->
                log.info("doOnNext: {}", num)
            }
            .subscribe { num: Int? -> log.info("Number: {}", num) }
    }

    @Test
    fun OnErrorMapIfPredicatePasses_Mono() {
        Flux.range(0, 10)
            .map { i: Int -> i / i } // will produce ArithmeticException
            .onErrorMap(
                { error: Throwable? -> error is ArithmeticException }
            ) { error: Throwable? -> RuntimeException("SomeMathException") }
            .doOnNext { num: Int? ->
                log.info("doOnNext: {}", num)
            }
            .subscribe { num: Int? -> log.info("Number: {}", num) }
    }
}
