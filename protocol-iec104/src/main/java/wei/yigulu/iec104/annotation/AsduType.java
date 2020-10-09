package wei.yigulu.iec104.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Asdu type
 *
 * @author xiuwei
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsduType {

	/**
	 * Value string
	 *
	 * @return the string
	 */
	String value() default "";

	/**
	 * Type id int
	 *
	 * @return the int
	 */
	int typeId() default 0;

	/**
	 * Is prior boolean
	 *
	 * @return the boolean
	 */
	boolean isPrior() default false;

	/**
	 * Builder name string
	 *
	 * @return the string
	 */
	String builderName() default "";


}
