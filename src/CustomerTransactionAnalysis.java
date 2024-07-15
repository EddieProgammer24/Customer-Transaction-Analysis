import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CustomerTransactionAnalysis {

    public static void main(String[] args) {
        String filename = "customer_transactions.csv";
        List<Transaction> transactions = readData(filename);
        if (transactions != null) {
            analyzeData(transactions);
        }
    }

    // Function to read data from a CSV file
    private static List<Transaction> readData(String filename) {
        List<Transaction> transactions = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip header row
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int custNo = Integer.parseInt(values[0].trim());
                boolean isMember = values[1].trim().equalsIgnoreCase("Yes");
                Date transDate = dateFormat.parse(values[2].trim());
                double totalPurchase = Double.parseDouble(values[3].trim());
                double couponAmtUsed = Double.parseDouble(values[4].trim());
                double rewardCashIssued = Double.parseDouble(values[5].trim());

                transactions.add(new Transaction(custNo, isMember, transDate, totalPurchase, couponAmtUsed, rewardCashIssued));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    // Function to analyze the data
    private static void analyzeData(List<Transaction> transactions) {
        int totalRecords = transactions.size();
        double totalRewardCash = 0.0;
        double totalPurchases = 0.0;
        Set<Integer> nonMembers = new HashSet<>();
        Set<Integer> allCustomers = new HashSet<>();
        Date earliestDate = null;
        Date latestDate = null;

        // Set to store transaction dates for date range determination
        Set<Date> transactionDates = new HashSet<>();

        for (Transaction transaction : transactions) {
            totalRewardCash += transaction.getRewardCashIssued();
            totalPurchases += transaction.getTotalPurchase();
            allCustomers.add(transaction.getCustNo());

            if (!transaction.isMember()) {
                nonMembers.add(transaction.getCustNo());
            }

            // Determine earliest and latest transaction dates
            Date transDate = transaction.getTransDate();
            if (earliestDate == null || transDate.before(earliestDate)) {
                earliestDate = transDate;
            }
            if (latestDate == null || transDate.after(latestDate)) {
                latestDate = transDate;
            }

            transactionDates.add(transDate);
        }

        // Calculate ratio of Reward Cash to total purchases
        double ratioRewardToPurchases = totalPurchases != 0 ? totalRewardCash / totalPurchases : 0;

        // Display results
        System.out.println("Total Records: " + totalRecords);
        System.out.println("Total Reward Cash Issued: $" + String.format("%.2f", totalRewardCash));
        System.out.println("Ratio of Reward Cash to Total Purchases: " + String.format("%.4f", ratioRewardToPurchases));
        System.out.println("Distinct Customers Not Members of Reward Program: " + nonMembers);

        // Determine date range
        if (earliestDate != null && latestDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            System.out.println("Transaction Date Range: " + dateFormat.format(earliestDate) + " to " + dateFormat.format(latestDate));

            // Calculate and print days difference using the date comparison routine
            long daysDifference = compareDates(earliestDate, latestDate);
            System.out.println("Days Difference between earliest and latest date: " + daysDifference);
        }
    }

    // Function to compare dates and return difference in days
    private static long compareDates(Date date1, Date date2) {
        long diffInMillies = Math.abs(date2.getTime() - date1.getTime());
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}

// Transaction class to store transaction details
class Transaction {
    private int custNo;
    private boolean isMember;
    private Date transDate;
    private double totalPurchase;
    private double couponAmtUsed;
    private double rewardCashIssued;

    public Transaction(int custNo, boolean isMember, Date transDate, double totalPurchase, double couponAmtUsed, double rewardCashIssued) {
        this.custNo = custNo;
        this.isMember = isMember;
        this.transDate = transDate;
        this.totalPurchase = totalPurchase;
        this.couponAmtUsed = couponAmtUsed;
        this.rewardCashIssued = rewardCashIssued;
    }

    public int getCustNo() {
        return custNo;
    }

    public boolean isMember() {
        return isMember;
    }

    public Date getTransDate() {
        return transDate;
    }

    public double getTotalPurchase() {
        return totalPurchase;
    }

    public double getCouponAmtUsed() {
        return couponAmtUsed;
    }

    public double getRewardCashIssued() {
        return rewardCashIssued;
    }
}
