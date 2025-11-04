package kevin.intellsoft.mediapp.util;

public class ValidationUtils {
    public static boolean isNotEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public static boolean isPositiveNumber(String s) {
        try {
            double v = Double.parseDouble(s);
            return v > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
