package wei.yigulu.modbus.utils;

import com.google.common.primitives.Bytes;
import lombok.NonNull;
import wei.yigulu.modbus.domain.FunctionCode;
import wei.yigulu.modbus.domain.Obj4RequestCoil;
import wei.yigulu.modbus.domain.Obj4RequestRegister;
import wei.yigulu.modbus.domain.datatype.BooleanModbusDataInCoil;
import wei.yigulu.modbus.domain.datatype.IModbusDataType;
import wei.yigulu.modbus.domain.datatype.ModbusDataTypeEnum;
import wei.yigulu.modbus.domain.request.AbstractModbusRequest;
import wei.yigulu.modbus.domain.request.TcpModbusRequest;
import wei.yigulu.modbus.domain.response.AbstractModbusResponse;
import wei.yigulu.modbus.domain.response.RtuModbusResponse;
import wei.yigulu.modbus.domain.response.TcpModbusResponse;
import wei.yigulu.modbus.domain.tcpextracode.TransactionIdentifier;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.modbus.netty.ModbusMasterBuilderInterface;
import wei.yigulu.netty.AbstractMasterBuilder;
import wei.yigulu.netty.AbstractTcpMasterBuilder;
import wei.yigulu.utils.PCON;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * 拼接从modbus 获取的数据
 *
 * @author: xiuwei
 * @version:
 */
public class ModbusRequestDataUtils {

	private static final int MAXLENGTH = 124;


	/**
	 * 拆分map 由于modbus 每帧只能请求255个字节  125个寄存区  寄存器
	 * 所以请求数据较多时需要拆成n帧 依次请求
	 *
	 * @param modbusDataTypeEnumMap modbus寄存器地址 ----- modbus 对应该地址的数据类型枚举
	 * @param slave                 从站地址
	 * @param functionCode          功能码
	 * @return Obj4RequestData     组装好的用于请求的对象
	 * @throws ModbusException
	 */
	public static List<Obj4RequestRegister> splitModbusRequest(Map<Integer, ModbusDataTypeEnum> modbusDataTypeEnumMap, int slave, FunctionCode functionCode) throws ModbusException {
		List<Obj4RequestRegister> list = new ArrayList<>();
		Set<Integer> set = new TreeSet(modbusDataTypeEnumMap.keySet());
		Integer max = Collections.max(set);
		Integer min = Collections.min(set);
		Map<Integer, ModbusDataTypeEnum> map = new HashMap<>();
		int i = 1;
		if (max + modbusDataTypeEnumMap.get(max).getOccupiedRegister() - min < MAXLENGTH) {
			list.add(new Obj4RequestRegister(slave, functionCode, modbusDataTypeEnumMap));
			return list;
		} else {
			for (Integer e : set) {
				if (e + modbusDataTypeEnumMap.get(e).getOccupiedRegister() - min < MAXLENGTH * i) {
					map.put(e, modbusDataTypeEnumMap.get(e));
				} else {
					if (map.size() != 0) {
						list.add(new Obj4RequestRegister(slave, functionCode, map));
						map = new HashMap<>();
						map.put(e, modbusDataTypeEnumMap.get(e));
					}
					i++;
				}
			}
			if (map.size() > 0) {
				list.add(new Obj4RequestRegister(slave, functionCode, map));
			}
			return list;
		}
	}


	/**
	 * 拆分map 由于modbus 每帧只能请求255个字节(线圈)
	 * 所以请求数据较多时需要拆成n帧 依次请求
	 *
	 * @param locator      modbus寄存器地址
	 * @param slave        从站地址
	 * @param functionCode 功能码
	 * @return Obj4RequestData     组装好的用于请求的对象
	 * @throws ModbusException
	 */
	public static List<Obj4RequestCoil> splitModbusRequest(List<Integer> locator, int slave, FunctionCode functionCode) throws ModbusException {
		List<Obj4RequestCoil> list = new ArrayList<>();
		Collections.sort(locator);
		Integer max = locator.get(0);
		Integer min = locator.get(locator.size() - 1);
		List<Integer> ls = new ArrayList<>();
		if (max - min < MAXLENGTH) {
			list.add(new Obj4RequestCoil(slave, functionCode, locator));
			return list;
		} else {
			for (Integer l : locator) {
				if ((l - min) < 255) {
					ls.add(l);
				} else {
					list.add(new Obj4RequestCoil(slave, functionCode, ls));
					ls = new ArrayList<>();
					ls.add(l);
					min = l;
				}
			}
			if (ls.size() > 0) {
				list.add(new Obj4RequestCoil(slave, functionCode, ls));
			}
			return list;
		}
	}

	/**
	 * 验证并创建请求对象 寄存器数据
	 * 验证map中的数据类型和地址位的关系是否正确
	 * 如果验证后 关系不对将抛出异常
	 *
	 * @param locator
	 * @return
	 * @throws ModbusException
	 */
	public static <T extends AbstractModbusRequest> T verifyAndCreateRequest(T requestType, int slaveId, FunctionCode functionCode, @NonNull Map<Integer, ModbusDataTypeEnum> locator) throws ModbusException {
		if (FunctionCode.READ_HOLDING_REGISTERS != functionCode && FunctionCode.READ_INPUT_REGISTERS != functionCode) {
			throw new ModbusException("功能码与查询类型不符");
		}
		Set<Integer> ketSet = new TreeSet(locator.keySet());
		if (locator.size() == 0) {
			return null;
		}
		int preLoc = 0;
		int preOcc = 0;
		for (Integer i : ketSet) {
			if (preLoc + preOcc > i) {
				throw new ModbusException("入参的偏移量（" + i + "）步长小于前个数据占用长度");
			}
			preLoc = i;
			preOcc = locator.get(i).getOccupiedRegister();
		}
		int min = Collections.min(ketSet);
		int max = Collections.max(ketSet);
		int needRegister = max - min + locator.get(max).getOccupiedRegister();
		if (needRegister > 125) {
			throw new ModbusException("请求的modbus数据量过多,响应数据帧长度超过255");
		}
		requestType.setStartAddress(min).setQuantity(needRegister).setSlaveId(slaveId).setFunctionCode(functionCode);
		return requestType;
	}


	/**
	 * 验证并创建请求对象  线圈数据类型
	 * 验证map中的数据类型和地址位的关系是否正确
	 * 如果验证后 关系不对将抛出异常
	 *
	 * @param locator
	 * @return
	 * @throws ModbusException
	 */
	public static <T extends AbstractModbusRequest> T verifyAndCreateRequest(T requestType, int slaveId, FunctionCode functionCode, @NonNull List<Integer> locator) throws ModbusException {
		if (FunctionCode.READ_COILS != functionCode && FunctionCode.READ_DISCRETE_INPUTS != functionCode) {
			throw new ModbusException("功能码与查询类型不符");
		}
		int min = Collections.min(locator);
		int max = Collections.max(locator);
		if (max - min > 255) {
			throw new ModbusException("请求的modbus数据量过多,超出长度的表达范围");
		}
		requestType.setStartAddress(min).setQuantity(max - min + 1).setSlaveId(slaveId).setFunctionCode(functionCode);
		return requestType;
	}

	/**
	 * 请求数据 将请求传入后   返回modbus的响应
	 *
	 * @param masterBuilder master 对象
	 * @param modbusRequest 想要发送的请求
	 * @param response      modbus的响应
	 * @param <T>
	 * @return
	 * @throws ModbusException
	 */
	public static <T extends AbstractModbusResponse> T requestData(AbstractMasterBuilder masterBuilder, AbstractModbusRequest modbusRequest, T response) throws ModbusException {
		if (!(masterBuilder instanceof ModbusMasterBuilderInterface)) {
			throw new RuntimeException("请传人实现了<ModbusMasterBuilderInterface>的Master");
		}
		if (masterBuilder.getFuture() != null && masterBuilder.getFuture().channel().isActive()) {
			List<Byte> byteList = new ArrayList<>();
			modbusRequest.encode(byteList);
			byte[] bb = Bytes.toArray(byteList);
			masterBuilder.sendFrameToOpposite(bb);
			ByteBuffer buffer;
			if (modbusRequest instanceof TcpModbusRequest) {
				buffer = ((ModbusMasterBuilderInterface) masterBuilder).getOrCreateSynchronousWaitingRoom().getData(((TcpModbusRequest) modbusRequest).getTcpExtraCode().getTransactionIdentifier().getSeq());
			} else {
				buffer = ((ModbusMasterBuilderInterface) masterBuilder).getOrCreateSynchronousWaitingRoom().getData(0);
			}
			if (buffer == null) {
				throw new ModbusException("Slave端响应超时");
			}
			response.decode(buffer);
		} else {
			throw new ModbusException("当前并Master未链接到Salve端");
		}
		return response;
	}


	/**
	 * 不了解该工具的可直接使用该方法  寄存器
	 * 在请求数据的发送和接收报文的基础上进行 编码和解码  编码请求报文  解码接收到的报文
	 * 传入想要的  点位--数据类型  得到数据的map  对外暴露 逻辑上最简单的方法
	 *
	 * @param masterBuilder
	 * @param locator
	 * @param slaveId
	 * @param functionCode
	 * @return
	 * @throws ModbusException
	 */
	public static Map<Integer, IModbusDataType> getData(AbstractMasterBuilder masterBuilder, Map<Integer, ModbusDataTypeEnum> locator, Integer slaveId, FunctionCode functionCode) throws ModbusException {
		List<Obj4RequestRegister> list = splitModbusRequest(locator, slaveId, functionCode);
		return getRegisterData(masterBuilder, list);
	}


	/**
	 * 不了解该工具的可直接使用该方法  线圈
	 * 在请求数据的发送和接收报文的基础上进行 编码和解码  编码请求报文  解码接收到的报文
	 * 传入想要的  点位--数据类型  得到数据的map  对外暴露 逻辑上最简单的方法
	 *
	 * @param masterBuilder
	 * @param locator
	 * @param slaveId
	 * @param functionCode
	 * @return
	 * @throws ModbusException
	 */
	public static Map<Integer, Boolean> getData(AbstractMasterBuilder masterBuilder, List<Integer> locator, Integer slaveId, FunctionCode functionCode) throws ModbusException {
		List<Obj4RequestCoil> list = splitModbusRequest(locator, slaveId, functionCode);
		return getCoilData(masterBuilder, list);
	}


	/**
	 * 这个方法适用于重复请求 寄存器 如重复请求前100个数据  使用该方法
	 * 将省去系统编码的过程
	 *
	 * @param masterBuilder
	 * @param locators
	 * @return
	 */
	public static Map<Integer, IModbusDataType> getRegisterData(AbstractMasterBuilder masterBuilder, List<Obj4RequestRegister> locators) throws ModbusException {
		Map<Integer, IModbusDataType> map = new HashMap<>();
		Map<Integer, IModbusDataType> map1 = null;
		for (Obj4RequestRegister m : locators) {
			try {
				map1 = getRegisterData(masterBuilder, m);
				if (map1 != null) {
					map.putAll(map1);
				}
			} catch (ModbusException e) {
				if ("当前并Master未链接到Salve端".equals(e.getMsg())) {
					throw e;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}


	public static Map<Integer, IModbusDataType> getRegisterData(AbstractMasterBuilder masterBuilder, Obj4RequestRegister locator) throws ModbusException {
		Map<Integer, IModbusDataType> map = null;
		AbstractModbusResponse response;
		try {
			if (masterBuilder instanceof AbstractTcpMasterBuilder) {
				response = requestData(masterBuilder, locator.getTcpModbusRequest().setTransactionIdentifier(TransactionIdentifier.getInstance((AbstractTcpMasterBuilder) masterBuilder)), new TcpModbusResponse());
			} else {
				response = requestData(masterBuilder, locator.getRtuModbusRequest(), new RtuModbusResponse());
			}
			byte[] bytes = response.getDataBytes();
			if (bytes != null && bytes.length > 0) {
				map = new HashMap<>();
				int min = Collections.min(locator.getLocator().keySet());
				for (Map.Entry<Integer, ModbusDataTypeEnum> e : locator.getLocator().entrySet()) {
					if (bytes.length >= e.getKey() - min + e.getValue().getOccupiedRegister()) {
						map.put(e.getKey(), e.getValue().getObject().decode(bytes, e.getKey() - min));
					}
				}
			}
		} catch (ModbusException e) {
			if ("当前并Master未链接到Salve端".equals(e.getMsg())) {
				throw e;
			}
			masterBuilder.getLog().error(e.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}


	/**
	 * 这个方法适用于重复请求 线圈 如重复请求前100个数据  使用该方法
	 * 将省去系统编码的过程
	 *
	 * @param masterBuilder
	 * @param locators
	 * @return
	 */
	public static Map<Integer, Boolean> getCoilData(AbstractMasterBuilder masterBuilder, List<Obj4RequestCoil> locators) throws ModbusException {
		Map<Integer, Boolean> map = new HashMap<>();
		Map<Integer, Boolean> map1 = null;
		AbstractModbusResponse requestData;
		BooleanModbusDataInCoil booleanModbusDataInCoil = new BooleanModbusDataInCoil();
		for (Obj4RequestCoil m : locators) {
			try {
				if (masterBuilder instanceof AbstractTcpMasterBuilder) {
					requestData = requestData(masterBuilder, m.getTcpModbusRequest().setTransactionIdentifier(TransactionIdentifier.getInstance((AbstractTcpMasterBuilder) masterBuilder)), new TcpModbusResponse());
				} else {
					requestData = requestData(masterBuilder, m.getRtuModbusRequest(), new RtuModbusResponse());
				}
				byte[] bytes = requestData.getDataBytes();
				if (bytes != null && bytes.length > 0) {
					map1 = new HashMap<>();
					int min = Collections.min(m.getLocator());
					int whichByteFlag = -1;
					int index;
					for (Integer i : m.getLocator()) {
						index = (i - min) / PCON.BYTEBITS;
						if (whichByteFlag != index) {
							whichByteFlag = index;
							booleanModbusDataInCoil = new BooleanModbusDataInCoil();
							booleanModbusDataInCoil.decode(bytes, index);
						}
						map1.put(i, booleanModbusDataInCoil.getValue((i - min) % PCON.BYTEBITS));
					}
				}
				if (map1 != null) {
					map.putAll(map1);
				}
			} catch (ModbusException e) {
				if ("当前并Master未链接到Salve端".equals(e.getMsg())) {
					throw e;
				}
				masterBuilder.getLog().error(e.getMsg());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}


}


