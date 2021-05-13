import io.netty.buffer.Unpooled;
import wei.yigulu.iec104.apdumodel.Apdu;
import wei.yigulu.iec104.apdumodel.Asdu;
import wei.yigulu.iec104.asdudataframe.ShortFloatType;
import wei.yigulu.iec104.asdudataframe.qualitydescription.IeMeasuredQuality;

import java.io.*;
import java.util.Map;

/**
 * @author: xiuwei
 * @version:
 */
public class ReadFile {


	public static void main(String[] args) throws Exception {
		BufferedWriter out = new BufferedWriter(new FileWriter("C:\\Users\\Administrator\\Desktop\\星能3-4.15解析.txt"));


		File file = new File("C:\\Users\\Administrator\\Desktop\\104m_04_20210415.log");//定义一个file对象，用来初始化FileReader
		FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
		BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
		StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
		String s = "";
		while ((s = bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
			if (s.contains("recv :") && !s.contains("-")) {
				String f = "";
				f += s.substring(0, 15) + "\r";
				Apdu a = new Apdu().loadByteBuf(Unpooled.copiedBuffer(Hex2Bytes(s.substring(23).replaceAll(" ", ""))));
				Asdu asdu = a.getAsdu();
				if (asdu != null && asdu.getDataFrame() instanceof ShortFloatType) {
					ShortFloatType ss = (ShortFloatType) asdu.getDataFrame();
					if (ss.getAddresses().size() == 1) {
						int j = 0;
						for (Map.Entry<IeMeasuredQuality, Float> e : ss.getDatas().entrySet()) {
							f += dayin(ss.getAddresses().get(0).getAddress() + j, e.getValue());
							j++;
						}
					} else {
						int f1 = 0;
						for (Map.Entry<IeMeasuredQuality, Float> i : ss.getDatas().entrySet()) {
							f += dayin(ss.getAddresses().get(f1++).getAddress(), i.getValue());
						}
					}
				}
				if (f.length() > 16) {
					System.out.println(f);
					out.write(f);
				}
			}
		}
		bReader.close();
		out.close();
		String str = sb.toString();
		System.out.println(str);


	}


	public static byte[] Hex2Bytes(String hexString) {
		byte[] arrB = hexString.getBytes();
		int iLen = arrB.length;
		byte[] arrOut = new byte[iLen / 2];
		String strTmp = null;
		for (int i = 0; i < iLen; i += 2) {
			strTmp = new String(arrB, i, 2);
			arrOut[(i / 2)] = ((byte) Integer.parseInt(strTmp, 16));
		}
		return arrOut;
	}


	public static String dayin(int key, Float value) {
		String s = "";
		int i;
		if (key == 16385) {
			s += "                  实际功率 : " + value + "\r";
		}
		if (key > 16421 && key < 16921 && (key - 16422) % 6 == 0) {
			i = (key - 16422) / 6 + 1;
			if (i == 9 || i == 16 || i == 31 || i == 44 || i == 48 || i == 59) {
				s += "                  样" + i + " : " + value + "\r";
			} else {
				s += "                  " + i + " : " + value + "\r";
			}

		}
		return s;
	}
}
