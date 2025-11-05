package kevin.intellsoft.mediapp.util;

public class BMIUtils {
    public static float calculate(float weightKg, float heightCm) {
        if (heightCm <= 0) return 0;
        float heightM = heightCm / 100f;
        return weightKg / (heightM * heightM);
    }

    public static String getStatus(float bmi) {
        if (bmi < 18.5f) return "Underweight";
        else if (bmi < 25f) return "Normal";
        else return "Overweight";
    }
}
