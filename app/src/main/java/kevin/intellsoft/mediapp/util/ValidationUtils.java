package kevin.intellsoft.mediapp.util;

public class ValidationUtils {
    public static boolean isNotEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
