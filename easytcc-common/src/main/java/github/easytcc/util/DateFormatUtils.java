package github.easytcc.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Fangfang.Xu
 *
 */
public abstract class DateFormatUtils {

	private static final ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};
	
	public static String ymdFormat(Date date){
		return df.get().format(date);
	}
}
