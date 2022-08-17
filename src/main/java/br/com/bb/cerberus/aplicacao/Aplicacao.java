package br.com.bb.cerberus.aplicacao;

import br.com.bb.cerberus.model.Movie;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class Aplicacao {

    public static void main(String[] args) throws IOException {
        Instant ini = Instant.now();
        List<Movie> movies = new ArrayList<>();

        // TODO -> automatizar o filePathIn
        String filePathIn = "C:\\Users\\F8295739\\Documents\\02 - Projetos\\ProjetoArquivos\\src\\main\\java\\";

        Scanner scannerIn = new Scanner(new File(filePathIn + "movies1.csv"));

        scannerIn.nextLine();
        while (scannerIn.hasNext()) {
            String[] line = scannerIn.nextLine().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            movies.add(new Movie(line[0], line[1], line[2], line[3], line[4], line[5], line[6], line[7], line[8], line[9], line[10], line[11]));
        }

        movies.sort(Comparator.comparing(Movie::getRating).reversed());
        createFileMovies("reversed.csv", movies);

        ArrayList<Movie> bestHorror = (ArrayList<Movie>) movies.stream().filter(Movie::isHorrorMovie).limit(20).collect(Collectors.toList());
        createFileMovies("bestHorror.csv", bestHorror);

        Instant fim = Instant.now();
        Duration delta = Duration.between(ini, fim);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SS").withZone(ZoneId.systemDefault());
        System.out.println("Início processamento: " + formatter.format(ini));
        System.out.println("Fim processamento: " + formatter.format(fim));
        System.out.println("Tempo em milisegundos: " + delta.toMillis() + " milisegundos");
        System.out.println("Tempo em segundos: " + delta.toSeconds() + " segundos");
    }

    private static void createFileMovies(String fileNameOut, List<Movie> movies) throws RuntimeException, IOException {

        // TODO -> automatizar o filepath
        String filePathOut = "C:\\Users\\F8295739\\Documents\\02 - Projetos\\ProjetoArquivos\\src\\main\\java\\";

        try (FileOutputStream out = new FileOutputStream(filePathOut.concat(fileNameOut))) {
            movies.forEach(movie -> {
                try {
                    out.write(movie.toString().getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}