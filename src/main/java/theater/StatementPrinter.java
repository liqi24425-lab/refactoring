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
    @SuppressWarnings({"checkstyle:FinalLocalVariable", "checkstyle:SuppressWarnings", "checkstyle:MagicNumber", "checkstyle:NeedBraces", "checkstyle:LineLength", "checkstyle:Indentation"})
    public String statement() {
        int totalAmount = 0;
        int volumeCredits = 0;
        StringBuilder result = new StringBuilder("Statement for " + invoice.getCustomer() + System.lineSeparator());

        NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);

        for (Performance performance : invoice.getPerformances()) {

            // add volume credits
            volumeCredits += Math.max(performance.audience - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
            // add extra credit for every five comedy attendees
            if ("comedy".equals(getPlay(performance).type)) volumeCredits += performance.audience / Constants.COMEDY_EXTRA_VOLUME_FACTOR;

            // print line for this order
            result.append(String.format("  %s: %s (%s seats)%n", getPlay(performance).name, frmt.format(getAmount(performance) / 100), performance.audience));
            totalAmount += getAmount(performance);
        }
        result.append(String.format("Amount owed is %s%n", frmt.format(totalAmount / 100)));
        result.append(String.format("You earned %s credits%n", volumeCredits));
        return result.toString();
    }

    @SuppressWarnings({"checkstyle:FinalLocalVariable", "checkstyle:SuppressWarnings"})
    private Play getPlay(Performance performance) {
        Play play = plays.get(performance.playID);
        return play;
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
