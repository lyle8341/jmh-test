package com.lyle;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * 测试各种垃圾回收器的效率
 *
 * @author lyle 2025-04-17 15:39
 */
@Warmup(iterations = 5, time = 2)
@BenchmarkMode(Mode.AverageTime) //指定显示结果
@OutputTimeUnit(TimeUnit.MILLISECONDS)//指定显示结果单位
@State(Scope.Benchmark)//变量共享范围
public class GcCollectorBenchaMark {

    //每次测试对象大小 4KB和4MB
    @Param({"4", "4096"})
    private int perSize;

    private void gcCollectorEffective(Blackhole bh) {
        //每次循环创建堆内存60%对象
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage usage = memoryMXBean.getHeapMemoryUsage();
        long retained = (long) ((usage.getMax() - usage.getUsed()) * 0.6);
        long times = retained / (1024L * perSize);
        for (int i = 0; i < 4; i++) {
            List<byte[]> objects = new ArrayList<>((int) times);
            for (int j = 0; j < times; j++) {
                objects.add(new byte[1024 * perSize]);
            }
            bh.consume(objects);
        }
    }

    @Benchmark
    @Fork(value = 1, jvmArgs = {"-Xmx4g", "-Xms4g", "-XX:+UseSerialGC"})
    public void serialGC(Blackhole bh) {
        gcCollectorEffective(bh);
    }

    @Benchmark
    @Fork(value = 1, jvmArgs = {"-Xmx4g", "-Xms4g", "-XX:+UseParallelGC"})
    public void parallelGC(Blackhole bh) {
        gcCollectorEffective(bh);
    }

    //JDK20默认G1
    @Benchmark
    @Fork(value = 1, jvmArgs = {"-Xmx4g", "-Xms4g"})
    public void g1GC(Blackhole bh) {
        gcCollectorEffective(bh);
    }

    @Benchmark
    @Fork(value = 1, jvmArgs = {"-Xmx4g", "-Xms4g", "-XX:+UseShenandoahGC"})
    public void shenandoahGC(Blackhole bh) {
        gcCollectorEffective(bh);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(GcCollectorBenchaMark.class.getSimpleName())
                .forks(1)//这里的forks数是给main方法使用的
                .build();
        new Runner(options).run();
    }
}