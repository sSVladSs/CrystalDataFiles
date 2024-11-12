package org.example.Visitor;

import org.example.Scaners.Hash;
import org.example.Scaners.SHA256;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Visitor {
    // Мапа для хранения ключей
    private final Map<String, Path> filesHashMap = new HashMap<>();
    // Список удаленных файлов
    private final List<String> inputFile = new ArrayList<>();
    private double amountOfDeletedData = 0;
    private final Path directory;
    private final Hash hash;

    public Visitor(Path directory, SHA256 hash) {
        this.directory = directory;
        this.hash = hash;
    }

    public void removeDuplicateFiles() {
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<>() {
                int counter = 0;

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String fileHash = hash.scan(file);
                    if (filesHashMap.containsKey(fileHash)) {
                        // Добавляем файл в список удаленных
                        inputFile.add(++counter + ". " + file.toString());
                        // Записываем путь к файлу найденному ранее
                        inputFile.add(" - Фалй найденный раннее находится по пути: " + filesHashMap.get(fileHash).toString());
                        // Записываем количество освобожденной памяти в МБ
                        amountOfDeletedData += (double) Files.size(file) / 1_048_576;
                        // Удаляем повторяющийся файл
                        System.out.println("Removing duplicate file: " + file);
                        Files.delete(file);
                    } else {
                        filesHashMap.put(fileHash, file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    System.err.println("Failed to access file: " + file + "due to " + exc.getMessage());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Записываем выходной .txt файл с отчетом об удалении
        writeRemovedFile();
    }

    private void writeRemovedFile() {
        // Получаем текущую дату и время
        Date now = new Date();
        // Форматируем дату и время для имени файла
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        // Создаем имя файла
        String filename = "File_" + dateFormat.format(now) + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("report\\" + filename))) {
            for (String line : inputFile) {
                writer.write(line);
                writer.newLine(); // Переход на новую строку
            }
            writer.write("\nОбъем освобожденной памяти: " + new DecimalFormat("#.##").format(amountOfDeletedData) + "(Mb)\n");
            writer.write("Количество удаленных файлов: " + inputFile.size() / 2);

            System.out.println("\nДанные успешно записаны в файл: record/" + filename);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
