package kevin.intellsoft.mediapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String toIsoDate(long epochMillis) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return fmt.format(new Date(epochMillis));
    }

    public static int calculateAgeYears(long dobMillis) {
        java.util.Calendar dob = java.util.Calendar.getInstance();
        dob.setTimeInMillis(dobMillis);
        java.util.Calendar today = java.util.Calendar.getInstance();
        int age = today.get(java.util.Calendar.YEAR) - dob.get(java.util.Calendar.YEAR);
        if (today.get(java.util.Calendar.DAY_OF_YEAR) < dob.get(java.util.Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }
}