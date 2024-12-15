import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

public class Task1 {

    public static void main(String[] args) {

        // Створення сканера для введення даних
        Scanner scr = new Scanner(System.in);
        System.out.print("Введіть діапазон чисел для генерування масиву (у форматі minNum maxNum): ");
        int minNum = scr.nextInt();
        int maxNum = scr.nextInt();

        // Використання пулу потоків для виконання асинхронних завдань
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Запуск таймера для вимірювання часу виконання програми
        long startTime = System.currentTimeMillis();

        // Асинхронне завдання для генерування масиву
        CompletableFuture<int[]> initialArrayFuture = CompletableFuture.supplyAsync(() -> {
            int[] array = generateArray(minNum, maxNum);
            System.out.println("Згенерований масив: " + Arrays.toString(array));
            System.out.println("Час на генерування масиву: " + (System.currentTimeMillis() - startTime) + " мс");
            return array;
        }, executor);

        // Асинхронне завдання для збільшення масиву на 5 одиниць
        CompletableFuture<int[]> incrementedArrayFuture = initialArrayFuture.thenApplyAsync(array -> {
            int[] incrementedArray = incrementArray(array);
            System.out.println("Збільшений масив на 5 одиниць: " + Arrays.toString(incrementedArray));
            System.out.println("Час на збільшення масиву: " + (System.currentTimeMillis() - startTime) + " мс");
            return incrementedArray;
        }, executor);

        // Асинхронне завдання для обчислення суми початкового та збільшеного масивів
        CompletableFuture<Integer> sumFuture = initialArrayFuture.thenCombineAsync(incrementedArrayFuture, (initialArray, incrementedArray) -> {
            int sumInitial = findSumOfArray(initialArray);
            int sumIncremented = findSumOfArray(incrementedArray);
            int totalSum = sumInitial + sumIncremented;
            System.out.println("Сума початкового масиву: " + sumInitial);
            System.out.println("Сума збільшеного масиву: " + sumIncremented);
            System.out.println("Загальна сума: " + totalSum);
            System.out.println("Час на обчислення суми: " + (System.currentTimeMillis() - startTime) + " мс");
            return totalSum;
        }, executor);

        // Асинхронне завдання для обчислення факторіалу загальної суми
        CompletableFuture<BigInteger> factorialFuture = sumFuture.thenApplyAsync(totalSum -> {
            BigInteger factorial = findFactorial(totalSum);
            System.out.println("Факторіал загальної суми: " + factorial);
            System.out.println("Час на обчислення факторіалу: " + (System.currentTimeMillis() - startTime) + " мс");
            return factorial;
        }, executor);

        // Завдання, яке виконується після завершення всіх асинхронних завдань
        factorialFuture.thenRunAsync(() -> {
            System.out.println("Усі задачі завершено.");
            System.out.println("Загальний час роботи програми: " + (System.currentTimeMillis() - startTime) + " мс");
        }, executor);

        // Чекання завершення основного потоку
        factorialFuture.join();

        // Закриття executor для звільнення ресурсів
        executor.shutdown();
    }

    // Генерує масив випадкових чисел у заданому діапазоні
    public static int[] generateArray(int minNum, int maxNum) {
        int[] array = new int[10];
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(maxNum - minNum + 1) + minNum;
        }
        return array;
    }

    // Збільшує кожен елемент масиву на 5 одиниць
    public static int[] incrementArray(int[] array){
        int[] array2 = new int[10];
        for (int i = 0; i < array.length; i++) {
            array2[i] = array[i] + 5;
        }
        return array2;
    }

    // Обчислює суму елементів масиву
    public static int findSumOfArray(int[] array){
        int totalSum = 0;
        for (int j : array) {
            totalSum += j;
        }
        return totalSum;
    }

    // Обчислює факторіал числа
    public static BigInteger findFactorial(int totalSum) {
        if (totalSum == 0) {
            return BigInteger.ONE;
        }
        BigInteger factorial = BigInteger.ONE;
        for (int i = 2; i <= totalSum; i++) {
            factorial = factorial.multiply(BigInteger.valueOf(i));
        }
        return factorial;
    }
}
