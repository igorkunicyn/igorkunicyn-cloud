package client.service.impl;

import client.service.WithFileWorkable;
import domain.FileUpload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class WorkWithFiles implements WithFileWorkable {

    private static WorkWithFiles instance;

    public static WorkWithFiles getInstance() {
        if (instance == null) {
            instance = new WorkWithFiles();
        }
        return instance;
    }

    public static FileUpload createFileForCopyOrMoveToServer(String source, String target) {
        String nameFile = Paths.get(target).getFileName().toString();
        String path = Paths.get(target).getParent().toString() + File.separator;
        byte[] bytes = createByteArrayFromFile(Paths.get(source));
        return new FileUpload(nameFile, path, bytes);
    }

    private static byte[] createByteArrayFromFile(Path path) {
        byte[] bytes = {};
        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static void createByteArrayToFile(FileUpload fileUpload) {
        try {
            Files.createFile(Paths.get(fileUpload.getPath()));
            Files.write(Paths.get(fileUpload.getPath()), fileUpload.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String openDirectoryService(String s) {
        File directory = new File(s);
        if (!directory.exists()) {
            return "Directory is not exists";
        }
        StringBuilder builder = new StringBuilder();
        for (File childFile : Objects.requireNonNull(directory.listFiles())) {
            if (childFile.isDirectory()) {
                builder.append("<DIR>");
            } else {
                builder.append("           ");
            }
            builder.append(childFile.getName()).append(System.lineSeparator());
        }
        return builder.toString();
    }

    @Override
    public void deleteDirectoryAndFileService(String path) {
        try {
            Files.delete(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void createDirectoryAndFileService(String path) {
        try {
            if (!Paths.get(path).getFileName().toString().contains(".")) {
                Files.createDirectory(Paths.get(path));
            } else {
                Files.createFile(Paths.get(path));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void copyDirectoryAndFileService(String source, String target) {
        try {
            Files.copy(Paths.get(source), Paths.get(target));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}