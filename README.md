# JMH

### 运行方式一(推荐)
> mvn clean verify(或idea中maven栏lifecycle中的verify)  

> java -jar target/benchmarks.jar

### 运行方式二
[main方法方式](./src/main/java/com/lyle/MyBenchmark.java)

### 编写测试方法需要注意几点
+ 死代码问题(JIT会优化忽略掉)
  ```
  //解决方式：使用黑洞
  @Benchmark
  public void deadCode() {
      int i = 0;
      i++;
  }
  ```
+ 黑洞的用法
  ```
  //防止被JIT优化掉
  @Benchmark
  public void blackhole(Blackhole bh) {
      int i = 0;
      i++;
      int j = 1;
      j++;
      BlackHoleUtil.consume(bh, i, j);
  }
  ```
  
### [测试结果可视化](https://jmh.morethan.io)




## springboot整合JMH
+ 1.引入依赖
  ```
  <dependency>
      <groupId>org.openjdk.jmh</groupId>
      <artifactId>jmh-core</artifactId>
      <version>${jmh.version}</version>
      <scope>test</scope>
  </dependency>
  <dependency>
      <groupId>org.openjdk.jmh</groupId>
      <artifactId>jmh-generator-annprocess</artifactId>
      <version>${jmh.version}</version>
      <scope>test</scope>
  </dependency>
  ```
  
+ 2.测试代码
  ```java
  package com.lyle;
  
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
  import org.openjdk.jmh.runner.options.OptionsBuilder;
  
  /**
   * @author lyle 2025-04-17 12:45
   */
  @Warmup(iterations = 5, time = 1)//Amount of iterations，time of each iteration
  @Fork(value = 1, jvmArgs = {"-Xmx1g", "-Xms1g"}) //启动多少个进程（也就是全流程执行几遍）
  @BenchmarkMode(Mode.AverageTime) //指定显示结果
  @OutputTimeUnit(TimeUnit.NANOSECONDS)//指定显示结果单位
  @State(Scope.Benchmark)//变量共享范围
  public class PracticeBenchmarkTest {
  
      private UserController userController;
      private ApplicationContext context;
  
      //初始化将springboot容器启动，端口随机
      //每个测试方法(@Benchmark)执行前都会调用一次
      @Setup
      public void setup() {
          //容器启动n次，所以端口随机
          this.context = new SpringApplication(WebTestApplication.class).run();
          userController = this.context.getBean(UserController.class);
      }
  
      @Test
      public void executeJMHRunner() throws RunnerException {
          new Runner(new OptionsBuilder()
                  .shouldDoGC(true)
                  .forks(0)//因为手动启动了springboot进程，不需要额外
                  .resultFormat(ResultFormatType.JSON)
                  .shouldFailOnError(true)
                  .build()).run();
      }
  
      @Benchmark
      public void test1(Blackhole bh) {
          bh.consume(userController.findUser());
      }
  }
  ```