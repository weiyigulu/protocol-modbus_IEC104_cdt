package wei.yigulu.modbus.domain;


import com.google.common.primitives.Bytes;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wei.yigulu.modbus.domain.datatype.BooleanModbusDataInCoil;
import wei.yigulu.modbus.domain.datatype.CoilValue;
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
		Integer functionCode = abstractModbusRequest.getFunctionCode();
		if (functionCode == null || functionCode > 4 || functionCode < 0) {
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
			if (functionCode == 1 || functionCode == 2) {
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

		List<CoilValue> coils = new CopyOnWriteArrayList<>();

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

		public void setCoil(int position, CoilValue value) {
			if (coils.size() <= position) {
				int num = position + 1 - coils.size();
				for (int i = 0; i < num; i++) {
					this.coils.add(new BooleanModbusDataInCoil());
				}
			}
			this.coils.set(position, value);
		}


		public byte[] getRegisterDataBytes(int position, int bitNum) {
			List<Byte> bytes = new ArrayList<>(bitNum * 2);
			int registersSize = this.registers.size();
			for (int i = position; i < position + bitNum; i++) {
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

			return null;
		}
	}
}
