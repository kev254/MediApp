package kevin.intellsoft.mediapp.util;

public class BMIUtils {

    public static double calcBmi(double weightKg, double heightCm) {
        if (heightCm <= 0 || weightKg <= 0) return 0.0;
        double h = heightCm / 100.0;
        return weightKg / (h * h);
    }

    public static String bmiCategory(double bmi) {
        if (bmi <= 0) return "Unknown";
        if (bmi < 18.5) return "Underweight";
        if (bmi >= 18.5 && bmi < 25) return "Normal";
        return "Overweight";
    }
}