package wei.yigulu.iec104.util;


import io.netty.channel.Channel;
import org.slf4j.Logger;
import wei.yigulu.iec104.apdumodel.Apdu;
import wei.yigulu.iec104.apdumodel.Asdu;
import wei.yigulu.iec104.asdudataframe.BooleanType;
import wei.yigulu.iec104.asdudataframe.ShortFloatType;
import wei.yigulu.iec104.asdudataframe.TotalSummonType;
import wei.yigulu.iec104.asdudataframe.typemodel.InformationBodyAddress;

import java.util.*;

/**
 * 发送数据帧的工具类
 *
 * @author: xiuwei
 * @version:
 */
public class SendDataFrameHelper {

	/**
	 * 单帧最长连续遥信个数
	 */
	public static final int MAXCONTINUITYYXNUM = 127;

	/**
	 * 单帧最长单点遥信个数
	 */
	public static final int MAXDISCONTINUITYYXNUM = 49;

	/**
	 * 单帧最长连续遥测个数
	 */
	public static final int MAXCONTINUITYYCNUM = 45;

	/**
	 * 单帧最长单点遥测个数
	 */
	public static final int MAXDISCONTINUITYYCNUM = 25;


	/**
	 * 发送遥信数据帧
	 *
	 * @param channel 通达对象
	 * @param dates   要发送的数据
	 * @param address 公共地址位
	 * @param cause   发送原因
	 * @throws Exception 异常信息
	 */
	public static void sendYxDataFrame(Channel channel, Map<Integer, Boolean> dates, Integer address, Integer cause, Logger log) throws Exception {
		BooleanType booleanType;
		Apdu apdu;
		Asdu asdu;
		Set<Integer> keys;
		Integer max;
		Integer min;
		if (dates.size() > 0) {
			keys = dates.keySet();
			max = Collections.max(keys);
			min = Collections.min(keys);
			if ((max - min) == (keys.size() - 1)) {
				for (List<Integer> li : splitAndSort(keys, MAXCONTINUITYYXNUM)) {
					booleanType = new BooleanType();
					booleanType.addAddress(new InformationBodyAddress(li.get(0)));
					for (Integer i : li) {
						booleanType.addData(dates.get(i));
					}
					apdu = new Apdu();
					asdu = booleanType.generateBack();
					asdu.setNot(cause);
					asdu.setCommonAddress(address);
					apdu.setAsdu(asdu);
					SendAndReceiveNumUtil.sendIFrame(apdu, channel, log);
					Thread.sleep(20);
				}
			} else {
				for (Map<Integer, Boolean> m : split(dates, MAXDISCONTINUITYYXNUM)) {
					booleanType = new BooleanType();
					for (Map.Entry<Integer, Boolean> em : m.entrySet()) {
						booleanType.addDataAndAdd(new InformationBodyAddress(em.getKey()), em.getValue());
					}
					apdu = new Apdu();
					asdu = booleanType.generateBack();
					asdu.setNot(cause);
					asdu.setCommonAddress(address);
					apdu.setAsdu(asdu);
					SendAndReceiveNumUtil.sendIFrame(apdu, channel, log);
					Thread.sleep(20);
				}
			}
		}
	}


	/**
	 * 发送遥信数据帧 不连续
	 *
	 * @param channel 通达对象
	 * @param dates   要发送的数据
	 * @param address 公共地址位
	 * @param cause   发送原因
	 * @throws Exception 异常信息
	 */
	public static void sendYxDataFrameDiscontinuity(Channel channel, Map<Integer, Boolean> dates, Integer address, Integer cause, Logger log) throws Exception {
		BooleanType booleanType;
		Apdu apdu;
		Asdu asdu;
		if (dates.size() > 0) {
			for (Map<Integer, Boolean> m : split(dates, MAXDISCONTINUITYYXNUM)) {
				booleanType = new BooleanType();
				for (Map.Entry<Integer, Boolean> em : m.entrySet()) {
					booleanType.addDataAndAdd(new InformationBodyAddress(em.getKey()), em.getValue());
				}
				apdu = new Apdu();
				asdu = booleanType.generateBack();
				asdu.setNot(cause);
				asdu.setCommonAddress(address);
				apdu.setAsdu(asdu);
				SendAndReceiveNumUtil.sendIFrame(apdu, channel, log);
				Thread.sleep(20);
			}
		}
	}

	/**
	 * 发送遥测 数据帧
	 *
	 * @param channel 通道对象
	 * @param dates   需要发送的数据
	 * @param address 公共地址位
	 * @param cause   发送的原因
	 * @throws Exception 异常
	 */
	public static void sendYcDataFrame(Channel channel, Map<Integer, Number> dates, Integer address, Integer cause, Logger log) throws Exception {
		Apdu apdu;
		Asdu asdu;
		ShortFloatType shortFloatType;
		Set<Integer> keys;
		Integer max;
		Integer min;
		if (dates.size() > 0) {
			keys = dates.keySet();
			max = Collections.max(keys);
			min = Collections.min(keys);
			if ((max - min) == (keys.size() - 1)) {
				for (List<Integer> li : splitAndSort(keys, MAXCONTINUITYYCNUM)) {
					shortFloatType = new ShortFloatType();
					shortFloatType.addAddress(new InformationBodyAddress(li.get(0)));
					for (Integer i : li) {
						shortFloatType.addData(dates.get(i).floatValue());
					}
					apdu = new Apdu();
					asdu = shortFloatType.generateBack();
					asdu.setNot(cause);
					asdu.setCommonAddress(address);
					apdu.setAsdu(asdu);
					SendAndReceiveNumUtil.sendIFrame(apdu, channel, log);
					Thread.sleep(20);
				}
			} else {
				for (Map<Integer, Number> m : split(dates, MAXDISCONTINUITYYCNUM)) {
					shortFloatType = new ShortFloatType();
					for (Map.Entry<Integer, Number> em : m.entrySet()) {
						shortFloatType.addDataAndAdd(new InformationBodyAddress(em.getKey()), em.getValue().floatValue());
					}
					apdu = new Apdu();
					asdu = shortFloatType.generateBack();
					asdu.setNot(cause);
					asdu.setCommonAddress(address);
					apdu.setAsdu(asdu);
					SendAndReceiveNumUtil.sendIFrame(apdu, channel, log);
					Thread.sleep(20);
				}
			}
		}
	}


	/**
	 * 发送遥测 数据帧 不连续
	 *
	 * @param channel 通道对象
	 * @param dates   需要发送的数据
	 * @param address 公共地址位
	 * @param cause   发送的原因
	 * @throws Exception 异常
	 */
	public static void sendYcDataFrameDiscontinuity(Channel channel, Map<Integer, Number> dates, Integer address, Integer cause, Logger log) throws Exception {
		Apdu apdu;
		Asdu asdu;
		ShortFloatType shortFloatType;
		if (dates.size() > 0) {
			for (Map<Integer, Number> m : split(dates, MAXDISCONTINUITYYCNUM)) {
				shortFloatType = new ShortFloatType();
				for (Map.Entry<Integer, Number> em : m.entrySet()) {
					shortFloatType.addDataAndAdd(new InformationBodyAddress(em.getKey()), em.getValue().floatValue());
				}
				apdu = new Apdu();
				asdu = shortFloatType.generateBack();
				asdu.setNot(cause);
				asdu.setCommonAddress(address);
				apdu.setAsdu(asdu);
				SendAndReceiveNumUtil.sendIFrame(apdu, channel, log);
				Thread.sleep(20);
			}
		}
	}

	/**
	 * 发送总召唤 帧
	 *
	 * @param channel 通道对象
	 * @param address 公共地址位
	 * @param cause   发送的原因
	 * @throws Exception 异常
	 */
	public static void sendTotalSummonFrame(Channel channel, Integer address, Integer cause, Logger log) throws Exception {
		Apdu apdu = new Apdu();
		Asdu asdu;
		TotalSummonType dataFrameType = new TotalSummonType();
		dataFrameType.setAddress(new InformationBodyAddress(0));
		dataFrameType.setValue(20);
		asdu = dataFrameType.generateBack();
		asdu.setNot(cause);
		asdu.setCommonAddress(address);
		apdu.setAsdu(asdu);
		SendAndReceiveNumUtil.sendIFrame(apdu, channel, log);
	}

	/**
	 * 将发送的数据集合拆成n个长度合适的集合
	 *
	 * @param map    总集合
	 * @param maxLen 设定最大的长度
	 * @param <T>    类型T
	 * @return 分集合
	 */
	public static <T> List<HashMap<Integer, T>> split(Map<Integer, T> map, int maxLen) {
		List<HashMap<Integer, T>> list = new ArrayList<>();
		HashMap transfer = new HashMap(maxLen);
		int j = 0;
		for (Integer o : map.keySet()) {
			if (j < maxLen) {
				transfer.put(o, map.get(o));
				j++;
			} else {
				list.add(transfer);
				transfer = new HashMap(maxLen);
				transfer.put(o, map.get(o));
				j = 1;
			}
		}
		list.add(transfer);
		return list;
	}

	/**
	 * 进行拆分并排序 主要是排序map 中的keyset 对keyset的int型进行排序
	 * *
	 *
	 * @param set    要排序的set
	 * @param maxLen 设定的最长长度
	 * @return 拆分后的集合 的集合
	 */
	public static List<List<Integer>> splitAndSort(Set<Integer> set, int maxLen) {
		List<List<Integer>> list = new ArrayList<>();
		List<Integer> ls = new ArrayList<>();
		List<Integer> transfer = new ArrayList<>(maxLen);
		for (Integer i : set) {
			ls.add(i);
		}
		Collections.sort(ls);
		int j = 0;
		for (Integer i : ls) {
			if (j < maxLen) {
				transfer.add(i);
				j++;
			} else {
				list.add(transfer);
				transfer = new ArrayList<>(maxLen);
				transfer.add(i);
				j = 1;
			}
		}
		list.add(transfer);
		return list;
	}


}
