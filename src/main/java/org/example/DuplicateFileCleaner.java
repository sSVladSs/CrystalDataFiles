package org.example;

import org.example.Scaners.SHA256;
import org.example.Visitor.Visitor;

import java.nio.file.Path;
import java.util.Scanner;

public class DuplicateFileCleaner {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        String regex = "[A-Za-z0-9]:\\W+[A-Za-zА-Яа-я0-9\\W+]*";
        System.out.print("Введите путь к дирректории: ");
        while (!scanner.hasNext(regex)) {
            System.out.println("Not");
            scanner.next();
        }
        Path path = Path.of(scanner.next());

        // Начало счетчика выполнения программы
        var start = System.currentTimeMillis();

        Visitor visitor = new Visitor(path, new SHA256(path));
        visitor.removeDuplicateFiles();

        // Конец счетчика выполнения программы
        var end = System.currentTimeMillis();
        ExecutionTime(end, start);
    }

    private static void ExecutionTime(long end, long start) {
        var resultTime = end - start;
        int minute = (int) ((resultTime / 1000) / 60);
        int second = (int) (resultTime / 1000) % 60;
        System.out.println("Execution time: " + minute + " minute " + second + " second");
    }
}