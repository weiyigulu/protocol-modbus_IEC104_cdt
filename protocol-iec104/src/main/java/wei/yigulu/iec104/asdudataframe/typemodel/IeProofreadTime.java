package wei.yigulu.iec104.asdudataframe.typemodel;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

/**
 * 对时帧的具体时标实体类
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class IeProofreadTime {

	private DateTime time = new DateTime();

	private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss:SSS");

	/**
	 * Ie proofread time
	 *
	 * @param is is
	 */
	public IeProofreadTime(ByteBuf is) {
		byte[] btime = new byte[8];
		is.readBytes(btime);
		int milliSecond = (btime[0] & 0xff) + ((btime[1] & 0xff) << 8);
		int minute = btime[2] & 0xff;
		int hour = btime[3] & 0xff;
		int day = btime[4] & 0xff;
		int month = btime[5] & 0xff;
		int year = btime[6] & 0xff;
		String s = "20" + String.format("%02d", year) + "-" + String.format("%02d", month) + "-" + String.format("%02d", day) + " "
				+ String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", milliSecond / 1000) + ":" +
				String.format("%02d", milliSecond % 1000);
		time = FORMATTER.parseDateTime(s);
	}

	/**
	 * Encode *
	 *
	 * @param buffer buffer
	 */
	public void encode(List<Byte> buffer) {
		int year = time.getYear();
		int month = time.getMonthOfYear();
		int day = time.getDayOfMonth();
		int hour = time.getHourOfDay();
		int minute = time.getMinuteOfHour();
		int second = time.getSecondOfMinute();
		int milliSecond = time.getMillisOfSecond();
		String nums = Integer.toBinaryString(second * 1000 + milliSecond);
		String secondStr = nums.substring(nums.length() - 8);
		String milliSecondStr = nums.substring(0, nums.length() - 8);
		buffer.add((byte) (Integer.parseInt(Integer.toHexString(Integer.parseInt(milliSecondStr, 2)), 16)));
		buffer.add((byte) (Integer.parseInt(Integer.toHexString(Integer.parseInt(secondStr, 2)), 16)));
		buffer.add((byte) (Integer.parseInt(Integer.toHexString(minute), 16)));
		buffer.add((byte) (Integer.parseInt(Integer.toHexString(hour), 16)));
		buffer.add((byte) (Integer.parseInt(Integer.toHexString(day), 16)));
		buffer.add((byte) (Integer.parseInt(Integer.toHexString(month), 16)));
		buffer.add((byte) (Integer.parseInt(Integer.toHexString(year - 2000), 16)));
	}

	@Override
	public String toString() {
		return FORMATTER.print(time.getMillis());
	}
}
