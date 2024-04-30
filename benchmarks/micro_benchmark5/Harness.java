import java.math.BigDecimal;

public class Harness {

    // Interface for Pi calculators
    interface PiCalculator {
        BigDecimal calculatePi(int numDigits);
    }

    // BBP Pi calculator implementation
    static class BBPPiCalculator implements PiCalculator {
        @Override
        public BigDecimal calculatePi(int numDigits) {
            int scale = numDigits + 10;  // Add extra precision for accuracy
            BigDecimal pi = BigDecimal.ZERO;
            BigDecimal term1, term2, term3, term4;

            for (int k = 0; k < numDigits; k++) {
                term1 = BigDecimal.valueOf(1).divide(BigDecimal.valueOf(16).pow(k), scale, BigDecimal.ROUND_HALF_EVEN);
                term2 = BigDecimal.valueOf(4).divide(BigDecimal.valueOf(8 * k + 1), scale, BigDecimal.ROUND_HALF_EVEN);
                term3 = BigDecimal.valueOf(2).divide(BigDecimal.valueOf(8 * k + 4), scale, BigDecimal.ROUND_HALF_EVEN);
                term4 = BigDecimal.valueOf(1).divide(BigDecimal.valueOf(8 * k + 5), scale, BigDecimal.ROUND_HALF_EVEN);
                pi = pi.add(term1.multiply(term2.subtract(term3).subtract(term4)));
            }

            return pi.setScale(numDigits, BigDecimal.ROUND_HALF_UP);
        }
    }

    // Chudnovsky Pi calculator implementation
    static class ChudnovskyPiCalculator implements PiCalculator {
        @Override
        public BigDecimal calculatePi(int numDigits) {
            // Chudnovsky algorithm implementation here
            // Placeholder code for demonstration purposes
            return BigDecimal.valueOf(3.14159265358979323846264338327950288419716939937510)
                    .setScale(numDigits, BigDecimal.ROUND_HALF_UP);
        }
    }

    // Pi generator class
    static class PiGenerator {
        private PiCalculator piCalculator;

        public PiGenerator(PiCalculator piCalculator) {
            this.piCalculator = piCalculator;
        }

        public BigDecimal generatePi(int numDigits) {
            return piCalculator.calculatePi(numDigits);
        }
    }

    public static void main(String[] args) {
        PiCalculator bbpCalculator = new BBPPiCalculator();
        PiCalculator chudnovskyCalculator = new ChudnovskyPiCalculator();

        PiGenerator bbpPiGenerator = new PiGenerator(bbpCalculator);
        PiGenerator chudnovskyPiGenerator = new PiGenerator(chudnovskyCalculator);

        int numDigits = 1500;  // Change this value to generate more or fewer digits

        BigDecimal piBBP = bbpPiGenerator.generatePi(numDigits);
        BigDecimal piChudnovsky = chudnovskyPiGenerator.generatePi(numDigits);

    }
}
