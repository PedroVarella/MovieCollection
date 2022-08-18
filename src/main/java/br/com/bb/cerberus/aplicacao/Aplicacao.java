package br.com.bb.cerberus.aplicacao;

import br.com.bb.cerberus.model.Movie;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;

public class Aplicacao {

	private static final String CAMINHO_ABSOLUTO = System.getProperty("user.dir") + "/src/main/java/";
	private static final Charset UTF8 = Charset.forName("UTF-8");

	public static void main(String[] args) throws IOException {
		List<String> tempos = new ArrayList<String>();
		Instant ini = Instant.now();
		List<Movie> movies = new ArrayList<>();

		//TODO -> Criar as threads
		// Usando o NIO e apenas streams
		Path p = Paths.get(CAMINHO_ABSOLUTO + "movies1.csv");

		try {
			Files.lines(p, UTF8).skip(1).map(l -> l.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)).forEach(arr -> {
				movies.add(new Movie(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8], arr[9],
						arr[10], arr[11]));
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Lista dos filmes de terror
		gerarArquivo("Filmes de Terror", movies.stream().filter(m -> m.isOfGenre("Horror"))
				.sorted(Comparator.comparing(Movie::getRating).reversed()).limit(20).collect(Collectors.toList()));

		// Lista dos 50 melhores por ano
		Map<String, List<Movie>> mapAnoFilme = new HashMap<>();
		movies.stream().map(m -> m.getYear()).distinct()
				.forEach(a -> mapAnoFilme.put(a,
						movies.stream().filter(m -> m.getYear().equals(a))
								.sorted(Comparator.comparing(Movie::getRating).reversed()).limit(50)
								.collect(Collectors.toList())));
		// Iterando no mapa
		mapAnoFilme.forEach((ano, lista) -> {
			try {
				gerarArquivo(ano, lista);
			} catch (RuntimeException | IOException e) {
				e.printStackTrace();
			}
		});

		// String filePathIn = "C:\\Users\\F8295739\\Documents\\02 -
		// Projetos\\ProjetoArquivos\\src\\main\\java\\";

		Scanner scannerIn = new Scanner(new File(CAMINHO_ABSOLUTO + "movies1.csv"));

		scannerIn.nextLine();
		while (scannerIn.hasNext()) {
			String[] line = scannerIn.nextLine().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
			movies.add(new Movie(line[0], line[1], line[2], line[3], line[4], line[5], line[6], line[7], line[8],
					line[9], line[10], line[11]));
		}

		movies.sort(Comparator.comparing(Movie::getRating).reversed());
		createFileMovies("reversed.csv", movies);

		ArrayList<Movie> bestHorror = (ArrayList<Movie>) movies.stream().filter(Movie::isHorrorMovie).limit(20)
				.collect(Collectors.toList());
		createFileMovies("bestHorror.csv", bestHorror);

		Instant fim = Instant.now();
		Duration delta = Duration.between(ini, fim);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SS")
				.withZone(ZoneId.systemDefault());
		tempos.add("Início processamento: " + formatter.format(ini));
		tempos.add("Fim processamento: " + formatter.format(fim));
		tempos.add("Tempo em milisegundos: " + delta.toMillis() + " milisegundos");
		tempos.add("Tempo em segundos: " + delta.toSeconds() + " segundos");
		System.out.println("Início processamento: " + formatter.format(ini));
		System.out.println("Fim processamento: " + formatter.format(fim));
		System.out.println("Tempo em milisegundos: " + delta.toMillis() + " milisegundos");
		System.out.println("Tempo em segundos: " + delta.toSeconds() + " segundos");
		gerarArquivo("Tempo de Processamento", tempos);
	}

	private static void createFileMovies(String fileNameOut, List<Movie> movies) throws RuntimeException, IOException {

		// TODO -> automatizar o filepath
		String filePathOut = "C:\\Users\\F8295739\\Documents\\02 - Projetos\\ProjetoArquivos\\src\\main\\java\\";

		try (FileOutputStream out = new FileOutputStream(CAMINHO_ABSOLUTO.concat(fileNameOut))) {
			movies.forEach(movie -> {
				try {
					out.write(movie.toString().getBytes());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}

	// Buffered Writer é aparentemente mais rápido.
	private static <T> void gerarArquivo(String nmArquivo, List<T> lista) throws RuntimeException, IOException {

		Path caminho = Paths.get(CAMINHO_ABSOLUTO.concat(nmArquivo).concat("-NIO"));

		try (BufferedWriter bw = Files.newBufferedWriter(caminho, UTF8)) {
			lista.forEach(m -> {
				try {
					bw.write(String.format("%s \n", m));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}
}