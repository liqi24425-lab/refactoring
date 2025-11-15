package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
@SuppressWarnings({"checkstyle:LineLength", "checkstyle:SuppressWarnings"})
public class StatementPrinter {
    @SuppressWarnings({"checkstyle:VisibilityModifier", "checkstyle:SuppressWarnings"})
    public Invoice invoice;
    @SuppressWarnings({"checkstyle:VisibilityModifier", "checkstyle:SuppressWarnings"})
    public Map<String, Play> plays;

    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     * @return the formatted statement
     * @throws RuntimeException if one of the play types is not known
     */
    @SuppressWarnings({"checkstyle:FinalLocalVariable", "checkstyle:SuppressWarnings", "checkstyle:MagicNumber", "checkstyle:NeedBraces", "checkstyle:LineLength", "checkstyle:Indentation", "checkstyle:MultipleStringLiterals"})
    public String statement() {

        StringBuilder result = new StringBuilder("Statement for " + invoice.getCustomer() + System.lineSeparator());
        for (Performance performance : invoice.getPerformances()) {
            result.append(String.format("  %s: %s (%s seats)%n", getPlay(performance).name, usd(getAmount(performance)), performance.audience));
        }

        result.append(String.format("Amount owed is %s%n", usd(getTotalAmount())));
        result.append(String.format("You earned %s credits%n", getTotalVolumeCredits()));
        return result.toString();
    }

    private int getTotalAmount() {
        int totalAmount = 0;
        for (Performance p : invoice.getPerformances()) {
            totalAmount += getAmount(p);
        }
        return totalAmount;
    }

    private int getTotalVolumeCredits() {
        int result = 0;
        for (Performance p : invoice.getPerformances()) {
            result += Math.max(p.audience - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
            if ("comedy".equals(getPlay(p).type)) {
                result += p.audience / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
            }
        }
        return result;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private static String usd(int totalAmount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(totalAmount / 100);
    }

    @SuppressWarnings("checkstyle:ParameterAssignment")
    private static int getVolumeCredits(Performance performance, int result) {
        result += Math.max(performance.audience - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
        return result;
    }

    @SuppressWarnings({"checkstyle:FinalLocalVariable", "checkstyle:SuppressWarnings"})
    private Play getPlay(Performance performance) {
        return plays.get(performance.playID);
    }

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:SuppressWarnings", "checkstyle:ParameterName", "checkstyle:FinalLocalVariable"})
    private int getAmount(Performance performance) {
        int result = 0;
        Play play = getPlay(performance);

        switch (play.type) {
            case "tragedy":
                result = 40000;
                if (performance.audience > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    result += 1000 * (performance.audience - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;

            case "comedy":
                result = Constants.COMEDY_BASE_AMOUNT;
                if (performance.audience > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.audience - Constants.COMEDY_AUDIENCE_THRESHOLD));
                }
                result += Constants.COMEDY_AMOUNT_PER_AUDIENCE * performance.audience;
                break;

            default:
                throw new RuntimeException(
                        String.format("unknown type: %s", play.type)
                );
        }

        return result;
    }

}
