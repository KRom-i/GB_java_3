package Lesson_04;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Streams {

    public static void main (String[] args) throws IOException {

        List<Developer> developerList = new ArrayList<>();

        developerList.add(new Developer("Alex", "Java", 4000));
        developerList.add(new Developer("Peter", "Python", 3000));
        developerList.add(new Developer("Michel", "PHP", 5000));
        developerList.add(new Developer("John", "Java", 4500));
        developerList.add(new Developer("Perry", "C++", 4100));
        developerList.add(new Developer("Vivien", "PHP", 3500));
        developerList.add(new Developer("Tomas", "Java", 4200));
        developerList.add(new Developer("Alisa", "Python", 3900));
        developerList.add(new Developer("Barry", "Java", 4000));
        developerList.add(new Developer("Bob", "C++", 4600));
        developerList.add(new Developer("Tom", "Python", 3600));


       List<Developer> javDev = developerList.stream()
               .filter(p -> "Java".equals(p.getLanguage()))
               .filter(d -> d.getSalary() > 4100)
               .collect(Collectors.toList());

       javDev.forEach(System.out::println);

        System.out.println("----------------------------------");

       developerList.stream()
               .filter(k -> "Python".equals(k.getLanguage()))
               .sorted((k1, k2) -> Integer.compare(k1.getSalary(), k2.getSalary()))
               .forEach(System.out::println);

        System.out.println("----------------------------------");

        developerList.stream()
                .filter(k -> "C++".equals(k.getLanguage()))
                .map(Developer::getName)
                .sorted()
                .forEach(System.out::println);

        System.out.println("----------------------------------");

        List<String> strings = Arrays.asList("one", "two", "three");
        strings.stream();

        Stream<String> streams = Stream.of("one", "two", "three");

        Stream<String> streams1 = Arrays.stream(new String[]{"one", "two", "three"});

        Stream<String> streams2 = Files.lines(Path.of("server", "FileTestStream.txt"));

        IntStream intStream = "123".chars();
        IntStream intStream2 = "one".chars();

        Stream<Object> streams3 = Stream.builder()
                .add("one")
                .add("two")
                .add("three")
                .build();


        readFile();
    }


    private static void readFile () throws IOException {

        List<String> text = Files.newBufferedReader(Path.of("Server/FileTestStream.txt")).lines()
        .flatMap(line -> Arrays.stream(line.split(" ")))
                .map(v -> v.replaceAll("[!:;'-_.,]", "").toLowerCase(Locale.ROOT))
                .filter(line -> !line.isBlank())
                .sorted(Comparator.reverseOrder())
                .distinct()
                .collect(Collectors.toList());

        System.out.println(text);

        Map<String, Integer> integerMap = Files.newBufferedReader(Path.of("Server/FileTestStream.txt")).lines()
                .flatMap(line -> Arrays.stream(line.split(" ")))
                .map(v -> v.replaceAll("[!?:;'-_.,]", "").toLowerCase(Locale.ROOT))
                .filter(line -> !line.isBlank())
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toMap(Function.identity(), value -> 1, Integer::sum));

        System.out.println(integerMap);
    }
}
