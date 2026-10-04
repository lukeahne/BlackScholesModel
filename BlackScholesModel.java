import java.util.*;
public class BlackScholesModel {
    public static double d1(double S, double K, double T, double r, double sigma) {
        return (Math.log(S / K) + (r + 0.5 * sigma * sigma) * T) / (sigma * Math.sqrt(T));
    }
    public static double d2(double S, double K, double T, double r, double sigma) {
        return d1(S, K, T, r, sigma) - sigma * Math.sqrt(T);
    }
   public static double normalCDF(double x) {
        if (x < -8.0) return 0.0;
        if (x >  8.0) return 1.0;

        // Abramowitz & Stegun constants
        final double a1 =  0.319381530;
        final double a2 = -0.356563782;
        final double a3 =  1.781477937;
        final double a4 = -1.821255978;
        final double a5 =  1.330274429;
        final double p  =  0.2316419;

        double absX = Math.abs(x);
        double t = 1.0 / (1.0 + p * absX);
        double poly = t * (a1 + t * (a2 + t * (a3 + t * (a4 + t * a5))));
        double pdf  = Math.exp(-0.5 * absX * absX) / Math.sqrt(2 * Math.PI);
        double cdf  = 1.0 - pdf * poly;

        return (x >= 0) ? cdf : 1.0 - cdf;
    }
    public static double normalPDF(double x) {
        return Math.exp(-0.5 * x * x) / Math.sqrt(2 * Math.PI);
    }

    // Option pricing
    
    public static double callPrice(double S, double K, double T, double r, double sigma) {
        validateInputs(S, K, T, r, sigma);
        double d1 = d1(S, K, T, r, sigma);
        double d2 = d2(S, K, T, r, sigma);
        return S * normalCDF(d1) - K * Math.exp(-r * T) * normalCDF(d2);
    }

    public static double putPrice(double S, double K, double T, double r, double sigma) {
        validateInputs(S, K, T, r, sigma);
        double d1 = d1(S, K, T, r, sigma);
        double d2 = d2(S, K, T, r, sigma);
        return K * Math.exp(-r * T) * normalCDF(-d2) - S * normalCDF(-d1);
    }

    // Greeks
    
    public static double delta(double S, double K, double T, double r, double sigma, boolean isCall) {
        double d1 = d1(S, K, T, r, sigma);
        return isCall ? normalCDF(d1) : normalCDF(d1) - 1.0;
    }
    public static double gamma(double S, double K, double T, double r, double sigma) {
        double d1 = d1(S, K, T, r, sigma);
        return normalPDF(d1) / (S * sigma * Math.sqrt(T));
    }
    public static double vega(double S, double K, double T, double r, double sigma) {
        double d1 = d1(S, K, T, r, sigma);
        return S * normalPDF(d1) * Math.sqrt(T) / 100.0;
    }
    public static double theta(double S, double K, double T, double r, double sigma, boolean isCall) {
        double d1 = d1(S, K, T, r, sigma);
        double d2 = d2(S, K, T, r, sigma);
        double term1 = -(S * normalPDF(d1) * sigma) / (2 * Math.sqrt(T));

        if (isCall) {
            return (term1 - r * K * Math.exp(-r * T) * normalCDF(d2)) / 365.0;
        } else {
            return (term1 + r * K * Math.exp(-r * T) * normalCDF(-d2)) / 365.0;
        }
    }
    public static double rho(double S, double K, double T, double r, double sigma, boolean isCall) {
        double d2 = d2(S, K, T, r, sigma);
        if (isCall) {
            return K * T * Math.exp(-r * T) * normalCDF(d2) / 100.0;
        } else {
            return -K * T * Math.exp(-r * T) * normalCDF(-d2) / 100.0;
        }
    }
    public static boolean verifyPutCallParity(double S, double K, double T, double r, double sigma, double tolerance) {
        double C = callPrice(S, K, T, r, sigma);
        double P = putPrice(S, K, T, r, sigma);
        double lhs = C - P;
        double rhs = S - K * Math.exp(-r * T);
        return Math.abs(lhs - rhs) < tolerance;
    }
    private static void validateInputs(double S, double K, double T, double r, double sigma) {
        if (S <= 0)     throw new IllegalArgumentException("Spot price S must be > 0. Got: " + S);
        if (K <= 0)     throw new IllegalArgumentException("Strike price K must be > 0. Got: " + K);
        if (T <= 0)     throw new IllegalArgumentException("Time to expiration T must be > 0. Got: " + T);
        if (sigma <= 0) throw new IllegalArgumentException("Volatility sigma must be > 0. Got: " + sigma);
    }
    private static String fmt(double val) {
        return String.format("%10.6f", val);
    }
    private static void printOptionReport(String label, double S, double K, double T, double r, double sigma) {
    boolean isCall = label.equalsIgnoreCase("CALL");
    double price;
    if (isCall) {
        price = callPrice(S, K, T, r, sigma);
    } else {
        price = putPrice(S, K, T, r, sigma);
    }
    System.out.println(label + " OPTION");
    System.out.printf("Price: %.4f%n", price);
    System.out.printf("Delta: %.4f%n", delta(S, K, T, r, sigma, isCall));
    System.out.printf("Gamma: %.4f%n", gamma(S, K, T, r, sigma));
    System.out.printf("Vega: %.4f%n", vega(S, K, T, r, sigma));
    System.out.printf("Theta: %.4f%n", theta(S, K, T, r, sigma, isCall));
    System.out.printf("Rho: %.4f%n", rho(S, K, T, r, sigma, isCall));
    System.out.printf("d1: %.4f%n", d1(S, K, T, r, sigma));
    System.out.printf("d2: %.4f%n", d2(S, K, T, r, sigma));
    System.out.println();
    }
    public static void main(String[] args) {
        System.out.println("BLACK-SCHOLES OPTION PRICING MODEL");
        System.out.println();

        // Example 1: At-the-money European option
        double S     = 100.0;
        double K     = 100.0;
        double T     = 1.0;
        double r     = 0.05;
        double sigma = 0.20;

        System.out.println("Example 1: At-The-Money (S = K = $100)\n");
        printOptionReport("CALL", S, K, T, r, sigma);
        printOptionReport("PUT",  S, K, T, r, sigma);

        boolean parity = verifyPutCallParity(S, K, T, r, sigma, 1e-8);
        System.out.println("Put-Call Parity satisfied: " + parity);
        System.out.println();

         // Example 2: In-the-Money Call
        
        System.out.println("Example 2: In-The-Money Call (S=$110, K=$100, T=3mo)\n");
        printOptionReport("CALL", 110.0, 100.0, 3.0 / 12.0, 0.05, 0.25);

        // Examble 3: User input
        
        System.out.println("Example 3: Interactive Input\n");

        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Spot price S      : $");  double iS = sc.nextDouble();
            System.out.print("Strike price K    : $");  double iK = sc.nextDouble();
            System.out.print("Expiry (days)     :  ");  double iDays = sc.nextDouble();
            System.out.print("Risk-free rate (%) :  "); double iR = sc.nextDouble() / 100.0;
            System.out.print("Volatility (%)    :  ");  double iSigma = sc.nextDouble() / 100.0;

            double iT = iDays / 365.0;
            System.out.println();
            printOptionReport("CALL", iS, iK, iT, iR, iSigma);
            printOptionReport("PUT",  iS, iK, iT, iR, iSigma);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input: skipping interactive example.");
        }
    }
}