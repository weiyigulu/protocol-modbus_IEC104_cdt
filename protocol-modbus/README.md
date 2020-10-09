###### 本工具是本人基于工作所需开发的针对于Modbus通讯的辅助工具


本工具基于netty框架。建议有netty使用经验和对Modbus通讯规约的人使用。如果不了解Modbus通讯规约，理解本工具的工作流程可能会有些麻烦。
默认的通讯机制将一问一答式的异步变同步程序获取方式

# 使用方式 

## Matser的创建 以及数据的接收处理：

### Matser的创建 

```java
	SimpleTcpMasterBuilder master = new SimpleTcpMasterBuilder("127.0.0.1", 5002);
		master.create();
```

create() 方法会阻塞线程，如果不希望阻塞线程可以使用createByUnBlock()，以工具内的单线程池执行。

### 数据的接收


##### 如果有疑问可以向 weiyigulu524710549@gmail.com  邮箱留言

