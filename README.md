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