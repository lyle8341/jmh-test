package com.lyle;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@Warmup(iterations = 5, time = 1)//Amount of iterations，time of each iteration
@Fork(value = 1, jvmArgs = {"-Xmx1g", "-Xms1g"}) //启动多少个进程（也就是全流程执行几遍）
@BenchmarkMode(Mode.AverageTime) //指定显示结果
@OutputTimeUnit(TimeUnit.NANOSECONDS)//指定显示结果单位
@State(Scope.Benchmark)//变量共享范围
public class DateBenchmark {

    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
    private Date date = new Date();
    private LocalDateTime localDateTime = LocalDateTime.now();
    private static ThreadLocal<SimpleDateFormat> simpleDateFormatThreadLocal = new ThreadLocal<>();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(FORMAT);

    //初始化方法
    @Setup
    public void setup() {
        System.err.println("====================");
        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
        simpleDateFormatThreadLocal.set(format);
    }

    @Benchmark
    public void testDate(Blackhole bh) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT);
        String str = simpleDateFormat.format(date);
        bh.consume(str);
    }

    @Benchmark
    public void testLocalDateTime(Blackhole bh) {
        String str = localDateTime.format(DateTimeFormatter.ofPattern(FORMAT));
        bh.consume(str);
    }

    @Benchmark
    public void testDateThreadLocal(Blackhole bh) {
        String str = simpleDateFormatThreadLocal.get().format(date);
        bh.consume(str);
    }

    @Benchmark
    public void testLocalDateTimeShare(Blackhole bh) {
        String str = localDateTime.format(DATE_TIME_FORMATTER);
        bh.consume(str);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(DateBenchmark.class.getSimpleName())
                .forks(1)//这里的forks数是给main方法使用的
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(options).run();
    }
}