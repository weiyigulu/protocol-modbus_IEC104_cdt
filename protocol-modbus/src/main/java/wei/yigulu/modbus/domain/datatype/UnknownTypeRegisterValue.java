package wei.yigulu.modbus.domain.datatype;


import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * AB +AB 数据类型
 *
 * @author: xiuwei
 * @version:
 */
@NoArgsConstructor
public class UnknownTypeRegisterValue extends RegisterValue {

	Register  register=new Register();

	@Override
	public List<Register> getRegisters() {
		List<Register> l=new ArrayList<>();
		l.add(this.register);
		return l;
	}

	@Override
	public UnknownTypeRegisterValue decode(byte[] bytes, int offset) {
		this.register=new Register(bytes[offset],bytes[offset+1]);
		return this;
	}

	@Override
	public UnknownTypeRegisterValue decode(ByteBuffer byteBuf) {
		this.register=new Register(byteBuf.get(),byteBuf.get());
		return this;
	}

	@Override
	public UnknownTypeRegisterValue encode(List<Byte> bytes) {
		bytes.add(this.register.getB1());
		bytes.add(this.register.getB2());
		return this;
	}
}
