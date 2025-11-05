package kevin.intellsoft.mediapp.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final SimpleDateFormat SERVER_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    /**
     * Converts timestamp (milliseconds) to a proper server date string "yyyy-MM-dd".
     * Example: 1762359869077 -> "2025-11-05"
     */
    public static String formatServerDate(long epochMillis) {
        try {
            return SERVER_FORMAT.format(new Date(epochMillis));
        } catch (Exception e) {
            return "";
        }
    }

    public static long startOfDayMillis(long epochMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(epochMillis);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static long endOfDayMillis(long epochMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(epochMillis);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTimeInMillis();
    }

    public static String formatDisplayDate(long epochMillis) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return f.format(new Date(epochMillis));
    }

    public static int calculateAgeYears(long dobMillis) {
        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(dobMillis);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) age--;
        return age;
    }
}
