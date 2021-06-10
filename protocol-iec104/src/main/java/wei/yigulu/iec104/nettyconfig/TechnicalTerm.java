package wei.yigulu.iec104.nettyconfig;

/**
 * 104的专业术语
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
public class TechnicalTerm {

	/**
	 * 建立连接时发送的起始帧
	 */
	public static byte[] START = new byte[]{0x68, 0x04, 0x07, 0x00, 0x00, 0x00};

	/**
	 * 建立连接时对起始帧的应答
	 */
	public static byte[] STARTBACK = new byte[]{0x68, 0x04, 0x0B, 0x00, 0x00, 0x00};

	/**
	 * STOP
	 */
	public static byte[] STOP = new byte[]{0x68, 0x04, 0x13, 0x00, 0x00, 0x00};

	/**
	 * STOPBACK
	 */
	public static byte[] STOPBACK = new byte[]{0x68, 0x04, 0x23, 0x00, 0x00, 0x00};

	/**
	 * TEST
	 */
	public static byte[] TEST = new byte[]{0x68, 0x04, 0x43, 0x00, 0x00, 0x00};

	/**
	 * TESTBACK
	 */
	public static byte[] TESTBACK = new byte[]{0x68, 0x04, (byte) 0x83, 0x00, 0x00, 0x00};

	/**
	 * 总召唤
	 */
	public static byte[] GENERALINTERROGATION = new byte[]{0x68, 0x0e, 0x00, 0x00, 0x00, 0x00, 0x64, 0x01, 0x06, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x14};


	/**
	 *
	 *
	 * 遥测：09----带品质描述的遥测量，每个遥测值占3个字节
	 *      0a----带3个字节时标的且具有品质描述的遥测值，每个遥测值占6个字节
	 *      0b---不带时标的标度化值，每个遥测值占3个字节
	 *      0c---带3个字节时标的标度化值，每个遥测值占6个字节
	 *      0d---带品质描述的浮点值，每个遥测值占5个字节
	 *      0e---带3个字节时标且具有品质描述的浮点值，每个遥测值占8个字节
	 *      15---不带品质描述的遥测值，每个遥测值占2个字节
	 * 遥信：01---不带时标的单点遥信，每个遥信占1个字节
	 *      03---不带时标的双点遥信，每个遥信占1个字节
	 *      14---具有状态变位检测的成组单点遥信，每个字节包括8个遥信
	 * SOE：02---带3个字节短时标的单点遥信
	 *      04---带3个字节短时标的双点遥信
	 *      1e---带7个字节时标的单点遥信
	 *      1f---带7个字节时标的双点遥信
	 * 遥脉：0f---不带时标的电度量，每个电度量占5个字节
	 *      10---带3个字节短时标的电度量，每个电度量占8个字节
	 *      25---带7个字节长时标的电度量，每个电度量占12个字节
	 * 其他：2d---单点遥控
	 * 2e---双点遥控
	 *      2f---双电遥调
	 *      64---召唤全数据
	 *      65---召唤全电度
	 *      67---时钟同步命令
	 *
	 * */

	/**
	 * 单点信息
	 */
	public static final Integer SINGEL_POINT_TYPE = 1;


	/**
	 * 双点信息
	 */
	public static final Integer DOUBLE_POINT_TYPE = 3;


	/**
	 * 测量值，规一化值
	 */
	public static final Integer NORMALIZED_INTEGER_TYPE = 9;

	/**
	 * 测量值，标度化值
	 */
	public static final Integer SCALING_INTEGER_TYPE = 11;
	/**
	 * 测量值，短浮点数
	 */
	public static final Integer SHORT_FLOAT_TYPE = 13;


	/**
	 * 测量值，无品质位规一化值
	 */
	public static final Integer NOQUALITY_NORMALIZED_INTEGER_TYPE = 21;


	/**
	 * 总召唤
	 */
	public static final Integer TOTAL_SUMMONTYPE_TYPE = 0x64;


	/**
	 * 设置短浮点命令
	 */
	public static final Integer SHORT_FLOAT_COMMAND_TYPE = 0x32;


	/**
	 * 单点遥控命令
	 */
	public static final Integer SINGLE_BOOLEAN_COMMAND_TYPE = 0x2D;



	/**
	 * 归一化值遥调命令
	 */
	public static final Integer NORMALIZATION_COMMAND_TYPE = 0x30;


	/**
	 * 双点遥控命令
	 */
	public static final Integer DOUBLE_BOOLEAN_COMMAND_TYPE = 0x2E;


	/**
	 * 对时帧
	 */
	public static final Integer DATESYNCHRONIZATION_TYPE = 0x67;

}
