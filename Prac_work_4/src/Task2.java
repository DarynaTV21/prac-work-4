import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

public class Task2 {

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
        CompletableFuture<int[]> arrayFuture = CompletableFuture.supplyAsync(() -> {
            int[] array = generateArray(20, minNum, maxNum);
            System.out.println("Згенерований масив: " + Arrays.toString(array));
            return array;
        }, executor);

        // Асинхронне завдання для обчислення попарних сум масиву
        CompletableFuture<int[]> sumsFuture = arrayFuture.thenApplyAsync(array -> {
            int[] sums = calculatePairwiseSums(array);
            System.out.println("Сума попарної послідовності натуральних чисел: " + Arrays.toString(sums));
            return sums;
        }, executor);

        // Асинхронне завдання для знаходження мінімальної суми з попарної послідовності
        CompletableFuture<Integer> minFuture = sumsFuture.thenApplyAsync(sums -> {
            int min = Arrays.stream(sums).min().getAsInt();
            System.out.println("Мінімальна сума попарної послідовності натуральних чисел: " + min);
            return min;
        }, executor);

        // Завдання, яке виконується після завершення всіх асинхронних завдань
        minFuture.thenRunAsync(() -> {
            System.out.println("Усі задачі завершено.");
            System.out.println("Загальний час роботи програми: " + (System.currentTimeMillis() - startTime) + " мс");
        }, executor);

        // Чекання завершення основного потоку
        minFuture.join();

        // Закриття executor для звільнення ресурсів
        executor.shutdown();
    }

    // Генерує масив випадкових чисел заданого розміру у діапазоні від minNum до maxNum
    public static int[] generateArray(int size, int minNum, int maxNum) {
        Random random = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(maxNum - minNum + 1) + minNum;
        }
        return array;
    }

    // Обчислює попарні суми сусідніх елементів масиву
    public static int[] calculatePairwiseSums(int[] array) {
        int[] sums = new int[array.length - 1];
        for (int i = 0; i < array.length - 1; i++) {
            sums[i] = array[i] + array[i + 1];
        }
        return sums;
    }
}
