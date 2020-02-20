package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MoneyLaunderingThread
 */
public class MoneyLaunderingThread extends Thread {
    private AtomicInteger amountOfFilesProcessed;
    private List<File> transactionFiles;
    private TransactionReader transactionReader = new TransactionReader();
    private TransactionAnalyzer transactionAnalyzer;
    private AtomicBoolean paused;

    public MoneyLaunderingThread(List<File> transFiles, AtomicInteger amountFilesProcessed,
            TransactionAnalyzer transactionAnalyzer, AtomicBoolean paused) {
        this.transactionFiles = transFiles;
        this.amountOfFilesProcessed = amountFilesProcessed;
        this.transactionAnalyzer = transactionAnalyzer;
        this.paused = paused;
    }

    @Override
    public void run() {
        for (File transactionFile : transactionFiles) {
            List<Transaction> transactions = transactionReader.readTransactionsFromFile(transactionFile);
            for (Transaction transaction : transactions) {
                if (paused.get()) {
                    synchronized (paused) {
                        try {
                            paused.wait();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                transactionAnalyzer.addTransaction(transaction);
            }
            amountOfFilesProcessed.incrementAndGet();
        }
    }
}