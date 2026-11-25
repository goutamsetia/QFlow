package com.qflow.streams;
import java.util.*;

public class StreamDemo {

    public static void main(String[] args) {

        // Creating a list of token numbers
        List<Integer> tokens = Arrays.asList(
            101, 102, 103, 104, 105, 106
        );

        // Display all tokens
        System.out.println("All Tokens:");

        tokens.forEach(System.out::println);

        // Display tokens greater than 103
        System.out.println("\nTokens greater than 103:");

        tokens.stream()
              .filter(token -> token > 103)
              .forEach(System.out::println);


        // Using map() to transform token numbers
        System.out.println("\nTokens after adding 1000:");

        tokens.stream()
              .map(token -> token + 1000)
              .forEach(System.out::println);


// Using sorted() to arrange token numbers
System.out.println("\nSorted Tokens:");

tokens.stream()
      .sorted()
      .forEach(System.out::println);


// Using distinct() to remove duplicate token numbers
List<Integer> duplicateTokens = Arrays.asList(
    101, 102, 101, 103, 102, 104
);

System.out.println("\nUnique Tokens:");

duplicateTokens.stream()
                .distinct()
                .forEach(System.out::println);


// Using limit() to display first 3 tokens
System.out.println("\nFirst 3 Tokens:");

tokens.stream()
      .limit(3)
      .forEach(System.out::println);


// Using skip() to skip first 3 tokens
System.out.println("\nTokens after skipping first 3:");

tokens.stream()
      .skip(3)
      .forEach(System.out::println);

    }
}
