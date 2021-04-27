该项目为协议通讯工具包的合集  

其中使用到的 开源技术主要有

-  netty  https://github.com/netty/netty.git

- purejavacomm  https://github.com/nyholku/purejavacomm.git

现阶段支持IEC104，modbus（TCP，RTU），CDT（RTU），后期可能会扩展更多

使用方式请参见项目内的介绍，也可参考本人的另一个项目  protocolconverter

使用者可以install发布到本地 ，如果有条件的可以deploy到自己家的仓库

有问题可以发邮件到 524710549@qq.com 与我联系，探讨交流

欢迎大家批评指正。

104依赖引入

```java
<dependency>
  <groupId>wei.yigulu</groupId>
  <artifactId>protocol-iec104</artifactId>
  <version>1.4.7</version>
</dependency>
```





modbus依赖引入

```java
<dependency>
  <groupId>wei.yigulu</groupId>
  <artifactId>protocol-modbus</artifactId>
  <version>1.2.0</version>
</dependency>
```







CDT依赖引入

```java
<dependency>
  <groupId>wei.yigulu</groupId>
  <artifactId>protocol-cdt</artifactId>
  <version>1.0.0</version>
</dependency>
```