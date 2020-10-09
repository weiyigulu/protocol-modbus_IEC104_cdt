package wei.yigulu.iec104.util;


import wei.yigulu.iec104.exception.Iec104Exception;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 配置信息读取
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
public class PropertiesReader {


	private static final String FILENAME = "IEC104";

	private static class LazyHolder {
		private static final PropertiesReader INSTANCE = new PropertiesReader();
	}

	private PropertiesReader() {
	}

	/**
	 * Gets instance *
	 *
	 * @return the instance
	 */
	public static final PropertiesReader getInstance() {
		return LazyHolder.INSTANCE;
	}


	private ResourceBundle resourceBundle;


	/**
	 * Gets resource bundle *
	 *
	 * @return the resource bundle
	 * @throws Iec104Exception iec exception
	 */
	public synchronized ResourceBundle getResourceBundle() throws Iec104Exception {
		if (resourceBundle == null) {
			try {
				resourceBundle = ResourceBundle.getBundle(FILENAME);
			} catch (MissingResourceException e) {
				throw new Iec104Exception("缺少文件名为" + FILENAME + "的properties配置文件");
			}
		}
		return resourceBundle;
	}


	/**
	 * Gets prop *
	 *
	 * @param propName prop name
	 * @return the prop
	 * @throws Iec104Exception iec exception
	 */
	public String getProp(String propName) throws Iec104Exception {
		String str;
		try {
			str = getResourceBundle().getString(propName);
		} catch (MissingResourceException e) {
			throw new Iec104Exception("缺少该属性信息");
		}
		return str;
	}

	/**
	 * Gets prop *
	 *
	 * @param propName     prop name
	 * @param defaultValue default value
	 * @return the prop
	 */
	public String getProp(String propName, String defaultValue) {
		String str;
		try {
			str = getResourceBundle().getString(propName);
		} catch (Exception e) {
			str = defaultValue;
		}
		return str;
	}

	/**
	 * Gets int prop *
	 *
	 * @param propName prop name
	 * @return the int prop
	 * @throws Iec104Exception iec exception
	 */
	public int getIntProp(String propName) throws Iec104Exception {
		int i;
		try {
			i = Integer.parseInt(getResourceBundle().getString(propName));
		} catch (NumberFormatException e) {
			throw new Iec104Exception(propName + "：该属性的配置只能为数字");
		} catch (Exception e) {
			throw new Iec104Exception("缺少该属性信息");
		}
		return i;
	}

	/**
	 * Gets int prop *
	 *
	 * @param propName     prop name
	 * @param defaultValue default value
	 * @return the int prop
	 */
	public int getIntProp(String propName, int defaultValue) {
		int i;
		try {
			i = Integer.parseInt(getResourceBundle().getString(propName));
		} catch (Exception e) {
			i = defaultValue;
		}
		return i;
	}


	/**
	 * Gets long prop *
	 *
	 * @param propName prop name
	 * @return the long prop
	 * @throws Iec104Exception iec exception
	 */
	public Long getLongProp(String propName) throws Iec104Exception {
		Long i;
		try {
			i = Long.parseLong(getResourceBundle().getString(propName));
		} catch (MissingResourceException e) {
			throw new Iec104Exception("缺少该属性信息");
		} catch (NumberFormatException e) {
			throw new Iec104Exception(propName + "：该属性的配置只能为数字");
		}
		return i;
	}


	/**
	 * Get long prop long
	 *
	 * @param propName     prop name
	 * @param defaultValue default value
	 * @return the long
	 */
	public Long getLongProp(String propName, Long defaultValue) {
		Long i;
		try {
			i = Long.parseLong(getResourceBundle().getString(propName));
		} catch (Exception e) {
			i = defaultValue;
		}
		return i;
	}


	/**
	 * Gets boolean prop *
	 *
	 * @param propName prop name
	 * @return the boolean prop
	 * @throws Iec104Exception iec exception
	 */
	public boolean getBooleanProp(String propName) throws Iec104Exception {
		boolean i;
		try {
			i = Boolean.parseBoolean(getResourceBundle().getString(propName));
		} catch (MissingResourceException e) {
			throw new Iec104Exception("缺少该属性信息");
		}
		return i;
	}


	/**
	 * Gets boolean prop *
	 *
	 * @param propName     prop name
	 * @param defaultValue default value
	 * @return the boolean prop
	 */
	public boolean getBooleanProp(String propName, boolean defaultValue) {
		boolean i;
		try {
			i = Boolean.parseBoolean(getResourceBundle().getString(propName));
		} catch (Exception e) {
			i = defaultValue;
		}
		return i;
	}


}
