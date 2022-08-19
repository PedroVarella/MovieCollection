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
import java.util.stream.Collectors;

//Todo sequencial
public class Aplicacao3 {

	private static final String CAMINHO_ABSOLUTO = System.getProperty("user.dir") + "/src/main/java/";
	private static final Charset UTF8 = Charset.forName("UTF-8");

	public static void main(String[] args) throws IOException, InterruptedException {
		List<String> tempos = new ArrayList<String>();
		Instant ini = Instant.now();
		List<Movie> movies = new ArrayList<Movie>();

		fileToList("movies1.csv", 1, movies);
		fileToList("movies2.csv", 0, movies);
		fileToList("movies3.csv", 0, movies);

		System.out.println(movies.size());

		Map<String, List<Movie>> mapAnoFilme = new HashMap<>();
		movies.stream().map(m -> m.getYear()).distinct()
				.forEach(a -> mapAnoFilme.put(a,
						movies.stream().filter(m -> m.getYear().equals(a))
								.sorted(Comparator.comparing(Movie::getRating).reversed()).limit(50)
								.collect(Collectors.toList())));
		for (Map.Entry<String, List<Movie>> el : mapAnoFilme.entrySet()) {
			gerarArquivo(el.getKey(), el.getValue());
		}

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

	private static void fileToList(String nmArquivo, int skipNum, List<Movie> lista) {
		Path p = Paths.get(CAMINHO_ABSOLUTO + nmArquivo);

		try {
			Files.lines(p, UTF8).skip(skipNum).map(l -> l.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1))
					.forEach(arr -> {
						lista.add(new Movie(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8],
								arr[9], arr[10], arr[11]));
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Buffered Writer é aparentemente mais rápido.
	private static <T> void gerarArquivo(String nmArquivo, List<T> lista) throws RuntimeException, IOException {

		Path caminho = Paths.get(CAMINHO_ABSOLUTO.concat(nmArquivo).concat(" - NIO"));

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