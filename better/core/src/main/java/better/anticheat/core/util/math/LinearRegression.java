package better.anticheat.core.util.math;

import lombok.Getter;

public class LinearRegression {

    @Getter
    private final double intercept, slope;
    private final double svar0, svar1;

    /**
     * Performs a linear regression.
     */
    public LinearRegression(final double[] x, final double[] y) {
        if (y.length != x.length) {
            throw new IllegalStateException();
        }
        final int n = x.length;

        double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
        for (int i = 0; i < n; i++) {
            sumx += x[i];
            sumx2 += x[i] * x[i];
            sumy += y[i];
        }
        final double xbar = sumx / n;
        final double ybar = sumy / n;

        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
        for (int i = 0; i < n; i++) {
            xxbar += (x[i] - xbar) * (x[i] - xbar);
            yybar += (y[i] - ybar) * (y[i] - ybar);
            xybar += (x[i] - xbar) * (y[i] - ybar);
        }

        slope = xybar / xxbar;
        intercept = ybar - slope * xbar;

        double rss = 0.0;
        double ssr = 0.0;

        for (int i = 0; i < n; i++) {
            final double fit = slope * x[i] + intercept;
            rss += (fit - y[i]) * (fit - y[i]);
            ssr += (fit - ybar) * (fit - ybar);
        }

        final int degreesOfFreedom = n - 2;
        final double svar = rss / degreesOfFreedom;
        svar1 = svar / xxbar;
        svar0 = svar / n + xbar * xbar * svar1;
    }

    public static LinearRegression simple(final double[] x, final double[] y) {
        return new LinearRegression(x, y);
    }

    public double interceptStdErr() {
        return Math.sqrt(svar0);
    }

    public double slopeStdErr() {
        return Math.sqrt(svar1);
    }

    public double predict(final double x) {
        return slope * x + intercept;
    }
}
