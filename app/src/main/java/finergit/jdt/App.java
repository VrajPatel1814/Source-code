package finergit.jdt;

import java.io.IOException;
import java.nio.file.*;

public class App {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java jar <jar-file> <source-repo> <destination-repo>");
            return;
        }

        Path srcRepo = Paths.get(args[0]);  
        Path destRepo = Paths.get(args[1]);

        try {
            Files.walk(srcRepo)
                .filter(Files::isRegularFile)
                .forEach(path -> processFile(path, srcRepo, destRepo));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processFile(Path filePath, Path srcRepo, Path destRepo) {
        String relativePath = srcRepo.relativize(filePath).toString();
        Path destDir = destRepo.resolve(relativePath).getParent();
        try {
            Files.createDirectories(destDir);
            if (filePath.toString().endsWith(".java")) {
                processJavaFile(filePath, destDir);
            } else {
                copyNonJavaFile(filePath, destRepo, relativePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processJavaFile(Path filePath, Path destDir) throws IOException {
        RepositoryParser.parseJavaFile(filePath, destDir);
    }

    private static void copyNonJavaFile(Path filePath, Path destRepo, String relativePath) throws IOException {
        Path destFile = destRepo.resolve(relativePath);
        Files.copy(filePath, destFile, StandardCopyOption.REPLACE_EXISTING);
    }
}
