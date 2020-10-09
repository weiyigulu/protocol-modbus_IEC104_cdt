package wei.yigulu.utils;

/**
 * CRC校验工具
 *
 * @author: xiuwei
 * @version:
 */
public class CrcUtils {

	/**
	 * 计算CRC16校验码
	 *
	 * @param bytes
	 * @return
	 */
	public static Integer generateCRC16(byte[] bytes) {
		int CRC = 0x0000ffff;
		int POLYNOMIAL = 0x0000a001;

		int i, j;
		for (i = 0; i < bytes.length; i++) {
			CRC ^= ((int) bytes[i] & 0x000000ff);
			for (j = 0; j < 8; j++) {
				if ((CRC & 0x00000001) != 0) {
					CRC >>= 1;
					CRC ^= POLYNOMIAL;
				} else {
					CRC >>= 1;
				}
			}
		}
		return CRC;
	}

	/**
	 * 生成crc8
	 * 宽度 8
	 * 多项式  07-------x8+x2+x+1----10000111------00000111
	 * 初始值 0x00；
	 * 结果异或值 0xff;
	 *
	 * @param data 数据
	 * @return int
	 */
	public static int generateCRC8(byte[] data) {
		int init = 0x00;
		int genPoly = 0x07;
		int width = 8;
		int xorout = 0xff;
		for (int i = 0; i < data.length; i++) {
			init ^= data[i];
			for (int j = 0; j < width; j++) {
				if ((init & 0x80) != 0) {
					init = (init << 1) ^ genPoly;
				} else {
					init <<= 1;
				}
			}
		}
		init ^= xorout;
		return init & 0xff;
	}


}
