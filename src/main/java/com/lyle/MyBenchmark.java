package com.lyle;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
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
public class MyBenchmark {

    @Benchmark
    public int testMethod() {
        int i = 0;
        i++;
        return i;
    }

    //死代码：JIT会优化忽略掉，解决方式：使用黑洞
    //@Benchmark
    public void deadCode() {
        int i = 0;
        i++;
    }

    //吸收掉未使用的变量，放置被JIT优化掉
    //@Benchmark
    public void blackhole(Blackhole bh) {
        int i = 0;
        i++;
        int j = 1;
        j++;
        BlackHoleUtil.consume(bh, i, j);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(MyBenchmark.class.getSimpleName())
                .forks(1)//这里的forks数是给main方法使用的
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(options).run();
    }
}
