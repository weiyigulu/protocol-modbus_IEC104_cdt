package wei.yigulu.modbus.domain;


import com.google.common.primitives.Bytes;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wei.yigulu.modbus.domain.datatype.Register;
import wei.yigulu.modbus.domain.datatype.RegisterValue;
import wei.yigulu.modbus.domain.datatype.numeric.P_AB;
import wei.yigulu.modbus.domain.request.AbstractModbusRequest;
import wei.yigulu.modbus.exceptiom.ModbusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * modbus  slave的数据容器
 *
 * @author: xiuwei
 * @version:
 */
public class ModbusSlaveDataContainer {

	@Setter
	@Getter
	@Accessors(chain = true)
	private Logger log = LoggerFactory.getLogger(this.getClass());


	Map<Integer, DataDrawer> slaveDataDrawer = new ConcurrentHashMap<>();


	public byte[] getDataBytesOfSlave(AbstractModbusRequest abstractModbusRequest) throws ModbusException {
		Integer byteNum = abstractModbusRequest.getQuantity();
		if (byteNum == null || byteNum == 0) {
			throw new ModbusException(3001, "请求体缺少数据个数 ");
		}
		FunctionCode functionCode = abstractModbusRequest.getFunctionCode();
		if (functionCode == null || functionCode.getCode() > 4 || functionCode.getCode() < 0) {
			throw new ModbusException(3002, "不支持请求体的功能码 " + functionCode);
		}
		Integer position = abstractModbusRequest.getStartAddress();
		if (position == null || position < 0) {
			throw new ModbusException(3003, "请求体请求起始地址不合法 " + position);
		}
		Integer slaveId = abstractModbusRequest.getSlaveId();
		if (slaveId == null || slaveId < 0) {
			throw new ModbusException(3004, "请求体请求设备地址不合法 " + position);
		}
		if (this.slaveDataDrawer.containsKey(slaveId)) {
			if (functionCode == FunctionCode.READ_COILS || functionCode == FunctionCode.READ_DISCRETE_INPUTS) {
				return this.slaveDataDrawer.get(slaveId).getCoilDataBytes(position, byteNum);
			} else {
				return this.slaveDataDrawer.get(slaveId).getRegisterDataBytes(position, byteNum);
			}
		} else {
			throw new ModbusException(3004, "请求体请求设备地址不合法 " + position);
		}
	}

	public void setRegister(int slaveId, int position, RegisterValue value) {
		getOrCreate(slaveId).setRegister(position, value);
	}

	public void setRegister(int slaveId, int position, List<RegisterValue> value) {
		getOrCreate(slaveId).setRegister(position, value);
	}

	public void setRegister(int slaveId, List<RegisterValue> value) {
		getOrCreate(slaveId).setRegister(value);
	}

	public void setCoil(int slaveId, int position, boolean value) {
		getOrCreate(slaveId).setCoil(position, value);
	}

	private DataDrawer getOrCreate(int slave) {
		if (!this.slaveDataDrawer.containsKey(slave)) {
			this.slaveDataDrawer.put(slave, new DataDrawer());
		}
		return this.slaveDataDrawer.get(slave);
	}

	/**
	 * 数据抽屉
	 */
	private class DataDrawer {

		List<Boolean> coils = new CopyOnWriteArrayList<>();

		List<Register> registers = new CopyOnWriteArrayList<>();

		public void setRegister(int position, RegisterValue value) {
			if (registers.size() < position + value.getModbusDataTypeEnum().getOccupiedRegister()) {
				int num = position + value.getModbusDataTypeEnum().getOccupiedRegister() - registers.size();
				for (int i = 0; i < num; i++) {
					this.registers.addAll(new P_AB(BigDecimal.ZERO).getRegisters());
				}
			}
			for (int i = 0; i < value.getModbusDataTypeEnum().getOccupiedRegister(); i++) {
				this.registers.remove(position);
			}
			this.registers.addAll(position, value.getRegisters());
		}

		public void setRegister(int position, List<RegisterValue> value) {
			for (RegisterValue r : value) {
				setRegister(position, r);
				position += r.getModbusDataTypeEnum().getOccupiedRegister();
			}
		}


		public void setRegister(List<RegisterValue> value) {
			setRegister(0, value);
		}


		/**
		 * 设置线圈值
		 *
		 * @param position 线圈的位置  第几个线圈 从0开始
		 * @param value    值
		 */
		public void setCoil(int position, boolean value) {
			int coilsSize = this.coils.size();
			if (coilsSize <= position) {
				for (int i = 0; i < coilsSize - position + 1; i++) {
					coils.add(false);
				}
			}
			this.coils.set(position, value);
		}


		public byte[] getRegisterDataBytes(int position, int registerNum) {
			List<Byte> bytes = new ArrayList<>(registerNum * 2);
			int registersSize = this.registers.size();
			for (int i = position; i < position + registerNum; i++) {
				if (registersSize > i) {
					bytes.add(this.registers.get(i).getB1());
					bytes.add(this.registers.get(i).getB2());
				} else {
					bytes.add((byte) 0);
					bytes.add((byte) 0);
				}
			}
			return Bytes.toArray(bytes);
		}

		public byte[] getCoilDataBytes(int position, int bitNum) {
			int coilsSize = coils.size();
			List<Byte> bytes = new ArrayList<>();
			byte b = 0;
			int siftNum = 0;
			for (int i = position; i < bitNum + position; i++) {
				if (coilsSize > i && coils.get(i)) {
					b |= (0x01 << siftNum);
				}
				if (++siftNum == 8) {
					siftNum = 0;
					bytes.add(b);
					b = 0;
				}
			}
			if (siftNum != 0) {
				bytes.add(b);
			}
			return Bytes.toArray(bytes);
		}
	}
}
