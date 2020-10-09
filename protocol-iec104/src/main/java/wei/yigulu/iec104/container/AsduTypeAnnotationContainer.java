package wei.yigulu.iec104.container;


import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import wei.yigulu.iec104.annotation.AsduType;
import wei.yigulu.iec104.apdumodel.Vsq;
import wei.yigulu.iec104.asdudataframe.AbstractDataFrameType;
import wei.yigulu.iec104.exception.Iec104Exception;
import wei.yigulu.iec104.util.PropertiesReader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用了AsduType的注解类的读取
 * 将所有使用了该注解的类统计在一起
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@Getter
@Slf4j
public class AsduTypeAnnotationContainer {

	private static class LazyHolder {
		private static final AsduTypeAnnotationContainer INSTANCE = new AsduTypeAnnotationContainer();
	}

	/**
	 * Gets instance *
	 *
	 * @return the instance
	 */
	public static final AsduTypeAnnotationContainer getInstance() {
		return AsduTypeAnnotationContainer.LazyHolder.INSTANCE;
	}

	private Map<Integer, DataTypeClasses> dataTypes = null;

	/**
	 * 框架中的i格式帧数据帧的类型继承类的所在包
	 */
	private static final String DATAFRAMEPAKAGENAME = "wei/yigulu/iec104/asdudataframe";


	/**
	 * 使用者的i格式帧数据帧的类型继承类的所在包
	 * 扫描所有包 故值为空串
	 */
	private static final String DATAFRAMEPAKAGENAME1 = PropertiesReader.getInstance().getProp("asduDateType", "");


	/**
	 * 获取继承类的TYPEID的属性名
	 */
	private static final String TYPEIDATTRIBUTENAME = "TYPEID";


	/**
	 * 获取继承类的TYPEID的属性名
	 */
	private static final String LOADMETHODNAME = "loadByteBuf";


	private AsduTypeAnnotationContainer() {


	}

	/**
	 * Gets data types *
	 *
	 * @return the data types
	 * @throws Iec104Exception iec exception
	 */
	public Map<Integer, DataTypeClasses> getDataTypes() throws Iec104Exception {
		if (this.dataTypes == null) {
			Reflections f = new Reflections(DATAFRAMEPAKAGENAME);
			Set<Class<? extends AbstractDataFrameType>> set = f.getSubTypesOf(AbstractDataFrameType.class);
			Reflections f1 = new Reflections(DATAFRAMEPAKAGENAME1);
			Set<Class<?>> set1 = f1.getTypesAnnotatedWith(AsduType.class);
			Field check;
			Method load;
			dataTypes = new ConcurrentHashMap<>();
			int typeId;
			try {
				for (Class<?> c : set) {
					log.debug("扫描到APDU处理类" + c.getSimpleName());
					try {
						check = c.getField(TYPEIDATTRIBUTENAME);
						load = c.getMethod(LOADMETHODNAME, ByteBuf.class, Vsq.class);
						load.setAccessible(true);
						dataTypes.put(check.getInt(null), new DataTypeClasses(c, check.getInt(null), load));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				for (Class<?> c : set1) {
					log.debug("扫描到APDU处理类" + c.getSimpleName());
					check = c.getField(TYPEIDATTRIBUTENAME);
					typeId = check.getInt(null);
					if (c.getAnnotation(AsduType.class).typeId() != 0) {
						typeId = c.getAnnotation(AsduType.class).typeId();
					}
					load = c.getMethod(LOADMETHODNAME, ByteBuf.class, Vsq.class);
					load.setAccessible(true);
					dataTypes.put(check.getInt(null), new DataTypeClasses(c, typeId, load));

				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Iec104Exception("初始化获取Asdu类时发生异常");
			}
		}
		return this.dataTypes;
	}
}


