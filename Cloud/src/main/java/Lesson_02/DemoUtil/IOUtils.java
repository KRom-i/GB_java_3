package Lesson_02.DemoUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class IOUtils {

    public static void main (String[] args) throws IOException, InterruptedException {

        Path path = Paths.get("DIR_JAVA_OI/Client" + File.separator + "test.txt");
//        System.out.println(path);
//        System.out.println(path.toAbsolutePath());

        Path path1 = Paths.get("DIR_JAVA_OI" + File.separator, "Client", "test.txt");
//        System.out.println(path1);
//        System.out.println(path1.toAbsolutePath());

        String root = "DIR_JAVA_IO";
        Path path2 = Paths.get(root, "Client", "test.txt");
//        System.out.println(path2);
//        System.out.println(path2.toAbsolutePath());

        Path path3 = Paths.get(root);
//        path3.toAbsolutePath().iterator().forEachRemaining(System.out::println);

        Path pathClientDir = Paths.get(root, "Client");

//        demoNIOWatchService(path3);

//        Проверка на существование
        System.out.println("File test.txt exist: " + Files.exists(Paths.get(pathClientDir + File.separator, "test.txt")));

        Path path4 = Paths.get(pathClientDir + File.separator, "test.txt");
//        if (!Files.exists(path4)){
//            Files.createFile(path4);
//            System.out.println(path4.getFileName());
//        } else {
//            System.out.println(path4.getFileName());
//        }

//        Path server = Paths.get(root + File.separator, "Server", "testMove.txt");
//  Перемещение файла

//        Path pathMove = Files.move(server, path4);

//   Копирование файла

//        Path pathCopy =  Files.copy(
//                Paths.get(root + File.separator, "server", "bigfile.txt"),
//                Paths.get(root + File.separator, "client", "bigfile.txt"));

//        Запись в файл

//        Path pathWrite = Path.of(root + File.separator, "client", "test.txt");
//        Files.writeString(pathWrite, "\n\nTest string", StandardOpenOption.APPEND);

//        Удаление файла
//        Files.delete(pathWrite);


//        Создание директории
        Files.createDirectories(Path.of(root + File.separator, "Dir0", "dir1", "dir4"));

//        Обход дерева файлов
//        demoFileVisitor(root);

//        Поиск файлов
        demoSearchFiles(root, "bigFile.txt");

    }

    private static void demoSearchFiles (String root, String nameFile) throws IOException {

        Files.walkFileTree(Path.of(root), new SimpleFileVisitor<>(){
            @Override
            public FileVisitResult visitFile (Path file, BasicFileAttributes attrs) throws IOException {
                if (nameFile.equalsIgnoreCase(file.getFileName().toString())){
                    System.out.println(file.getFileName() + " is founded. Path: " + file.toAbsolutePath());
                    return FileVisitResult.CONTINUE;
                } else {
                    return FileVisitResult.CONTINUE;
                }
            }
        });
    }

    private static void demoFileVisitor (String root) throws IOException {
        Files.walkFileTree(Path.of(root), new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory (Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("preVisitDirectory - " + dir.getFileName());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile (Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("visitFile - " + file.getFileName());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed (Path file, IOException exc) throws IOException {
                System.out.println("visitFileFailed - " + file.getFileName());
                return FileVisitResult.TERMINATE;
            }

            @Override
            public FileVisitResult postVisitDirectory (Path dir, IOException exc) throws IOException {
                System.out.println("postVisitDirectory - " + dir.getFileName());
                return FileVisitResult.CONTINUE;
            }
        });
    }


    private static void demoNIOWatchService(Path path) throws IOException {

        WatchService service = FileSystems.getDefault().newWatchService();
        path.register(service,
                       StandardWatchEventKinds.ENTRY_CREATE,
                       StandardWatchEventKinds.ENTRY_DELETE,
                       StandardWatchEventKinds.ENTRY_MODIFY);

        new Thread(()->{

            String notification = "Event type: %s. File: %s\n";

            while (true){

                try{
                    WatchKey key = service.take();
                    if (key.isValid()){
                        List<WatchEvent<?>> listKeys = key.pollEvents();
                        for(WatchEvent<?> event : listKeys
                        ) {
                            System.out.printf(notification, event.kind(), event.context());
                        }
                    }
                    key.reset();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

        }).start();

    }
}
