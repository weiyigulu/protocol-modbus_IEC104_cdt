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

```java
	ModbusTcpMasterBuilder master = new ModbusTcpMasterBuilder("127.0.0.1", 5001);
		master.createByUnBlock();
		TcpSynchronousWaitingRoom.waitTime=5000L;
		Thread.sleep(3000L);
		Map<Integer, ModbusDataTypeEnum> map = new HashMap<>();
//构建一个请求数据的列表  该map代表 取0-120 地址位 ， 数据类型为P_AB的 数据 这个根据实际需要进行修改 占用两个寄存器的数据 地址位该数据起始寄存器地址
		for (int i = 0; i <= 120; i++) {
			map.put(i , ModbusDataTypeEnum.P_AB);
		}
		List<Obj4RequestRegister> ll = ModbusRequestDataUtils.splitModbusRequest(map, 1, FunctionCode.READ_HOLDING_REGISTERS);

		for (; ; ) {
			try {
				Map<Integer, IModbusDataType> map1 = ModbusRequestDataUtils.getRegisterData(master, ll);
				ArrayList<Integer> lll = new ArrayList<Integer>(map1.keySet());
				Collections.sort(lll);
				for (Integer i : lll) {
					if(map1.get(i) instanceof  NumericModbusData) {
						System.out.println(i + " ============ " + ((NumericModbusData) map1.get(i)).getValue());
					}else {
						System.out.println(i + " ============ " + JSON.toJSONString(((BooleanModbusDataInRegister) map1.get(i)).getValues()));
				}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Thread.sleep(3000L);
		}
```

#### 发送遥控遥调命令

``` java
ModbusRtuMasterBuilder master = new ModbusRtuMasterBuilder("COM1");
		master.createByUnBlock();
		TcpSynchronousWaitingRoom.waitTime = 5000L;
		Thread.sleep(3000L);
		Random random = new Random();
		BigDecimal val;
		BigDecimal val1;
		for (; ; ) {
			val = BigDecimal.valueOf(random.nextInt(100));
			System.out.println("数据个数：" + val);
			List<RegisterValue> list = new ArrayList<>();
			for (int i = 0; i <= val.intValue(); i++) {
				val1 = BigDecimal.valueOf(random.nextInt(11));
				System.out.println("数据值：" + val1);
				list.add(new P_AB().setValue(val1));
			}
            //ModbusCommandDataUtils.commandRegister(master, 1, 0, list) 发送命令返回结果
			System.out.println(ModbusCommandDataUtils.commandRegister(master, 1, 0, list));
			Thread.sleep(60000L);
		}
```



### SLAVER的创建 

```java
ModbusTcpSlaverBuilder slaverBuilder = new ModbusTcpSlaverBuilder(502);
		slaverBuilder.createByUnBlock();

		Random random = new Random();
		boolean f;
		for (; ; ) {
			for (int i = 0; i < 10; i++) {
                //设置地址位  数据类型&值
				slaverBuilder.getModbusSlaveDataContainer().setRegister(1, i, new BADC(BigDecimal.valueOf(random.nextFloat())));
				/*f = random.nextBoolean();
				System.out.println(i + ":" + f);
				slaverBuilder.getModbusSlaveDataContainer().setCoil(1, i, f);*/
			}
			Thread.sleep(2000L);
		}
```



### 数据的接收


##### 如果有疑问可以向 weiyigulu524710549@gmail.com  邮箱留言

