package org.geysermc.integratedpack;

public class MathUtils {
    /**
     * From https://javarevisited.blogspot.com/2016/07/how-to-calculate-gcf-and-lcm-of-two-numbers-in-java-example.html
     * @param a number 1
     * @param b number 2
     * @return greatest common factor
     */
    public static int gcf(int a, int b) {
        if (b == 0) {
            return a;
        } else {
            return (gcf(b, a % b));
        }
    }

    /**
     * From https://javarevisited.blogspot.com/2016/07/how-to-calculate-gcf-and-lcm-of-two-numbers-in-java-example.html
     * @param a number 1
     * @param b number 2
     * @return lowest common multiple
     */
    public static int lcm(int a, int b) {
        return (a * b) / gcf(a, b);
    }

    public static int lcm(int... numbers) {
        if (numbers == null || numbers.length == 0) {
            return 0;
        }
        int result = numbers[0]; // lcm of 1 number is... itself of course
        for (int i = 1; i < numbers.length; i++) {
            result = lcm(result, numbers[i]);
        }
        return result;
    }
}
