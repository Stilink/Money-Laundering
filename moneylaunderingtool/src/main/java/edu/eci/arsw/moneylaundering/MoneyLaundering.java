package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoneyLaundering {
    private TransactionAnalyzer transactionAnalyzer;
    private TransactionReader transactionReader;
    private int amountOfFilesTotal;
    private AtomicInteger amountOfFilesProcessed;

    public MoneyLaundering() {
        transactionAnalyzer = new TransactionAnalyzer();
        transactionReader = new TransactionReader();
        amountOfFilesProcessed = new AtomicInteger();
    }

    public void processTransactionData(int nThreads, AtomicBoolean paused) {
        amountOfFilesProcessed.set(0);
        List<File> transactionFiles = getTransactionFileList();
        amountOfFilesTotal = transactionFiles.size();
        /**
         * Variable que indica los intervalos que deben cubrir cada uno de los hilos.
         * Sin embargo, cabe añadir que esto solo funcionará exacto para los n que
         * dividan a el tamaño total de archivos. Eso nos deja el caso de
         * amountOfFilesTotal%n!=0. Para esto pensaremos una solución con el hilo final.
         */
        int inter = (int) amountOfFilesTotal / nThreads;
        List<Thread> threads = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < nThreads - 1; i++) {
            List<File> arrayForTheThread = transactionFiles.subList(start, start + inter);
            MoneyLaunderingThread moneyLaunderingThread = new MoneyLaunderingThread(arrayForTheThread,
                    amountOfFilesProcessed, transactionAnalyzer, paused);
            threads.add(moneyLaunderingThread);
            moneyLaunderingThread.start();
            start += inter;
        }
        /**
         * Aquí es donde arreglamos con el ultimo hilo. Lo que haremos será mandar n-1
         * hilos arriba, y al final le pondremos la lista de tamaño
         * (amountOfFilesTotal/n)+(amountOfFilesTotal%n). Eso nos dará el rango
         * completo.
         */
        List<File> arrayForTheThread = transactionFiles.subList(start, amountOfFilesTotal);
        MoneyLaunderingThread moneyLaunderingThread = new MoneyLaunderingThread(arrayForTheThread,
                amountOfFilesProcessed, transactionAnalyzer, paused);
        threads.add(moneyLaunderingThread);
        moneyLaunderingThread.start();

        /**
         * Ahora debemos unir resultados.
         */
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        /*
         * for(File transactionFile : transactionFiles) { List<Transaction> transactions
         * = transactionReader.readTransactionsFromFile(transactionFile);
         * for(Transaction transaction : transactions) {
         * transactionAnalyzer.addTransaction(transaction); }
         * amountOfFilesProcessed.incrementAndGet(); }
         */

    }

    public List<String> getOffendingAccounts() {
        return transactionAnalyzer.listOffendingAccounts();
    }

    private List<File> getTransactionFileList() {
        List<File> csvFiles = new ArrayList<>();
        try (Stream<Path> csvFilePaths = Files.walk(Paths.get("src/main/resources/"))
                .filter(path -> path.getFileName().toString().endsWith(".csv"))) {
            csvFiles = csvFilePaths.map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvFiles;
    }

    public static void main(String[] args) {
        System.out.println(getBanner());
        System.out.println(getHelp());
        AtomicBoolean paused = new AtomicBoolean(false);
        MoneyLaundering moneyLaundering = new MoneyLaundering();
        Thread processingThread = new Thread(() -> moneyLaundering.processTransactionData(22, paused));
        processingThread.start();
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (line.contains("exit")) {
                System.exit(0);
            } else {
                paused.set(true);
                synchronized(paused){
                    paused.notifyAll();
                }
                String message = "Processed %d out of %d files.\nFound %d suspect accounts:\n%s";
                List<String> offendingAccounts = moneyLaundering.getOffendingAccounts();
                String suspectAccounts = offendingAccounts.stream().reduce("", (s1, s2) -> s1 + "\n" + s2);
                message = String.format(message, moneyLaundering.amountOfFilesProcessed.get(),
                        moneyLaundering.amountOfFilesTotal, offendingAccounts.size(), suspectAccounts);
                System.out.println(message);
                System.out.println("");
                System.out.println("");
                System.out.println("Sistema pausado, presione enter para continuar el análisis.");
                String enter = scanner.nextLine();
                if (line.contains("exit")) {
                    System.exit(0);
                }else{
                    paused.set(false);
                }
                System.out.println("Analizando");
                continue;
            }

        }
    }

    private static String getBanner() {
        String banner = "\n";
        try {
            banner = String.join("\n", Files.readAllLines(Paths.get("src/main/resources/banner.ascii")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return banner;
    }

    private static String getHelp() {
        String help = "Type 'exit' to exit the program. Press 'Enter' to get a status update\n";
        return help;
    }
}