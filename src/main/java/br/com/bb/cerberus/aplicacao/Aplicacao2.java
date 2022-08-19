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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

//Gera o mapa de filmes em memória na thread main e grava cada arquivo em uma thread separada
public class Aplicacao2 {

	private static final String CAMINHO_ABSOLUTO = System.getProperty("user.dir") + "/src/main/java/";
	private static final Charset UTF8 = Charset.forName("UTF-8");

	public static void main(String[] args) throws IOException, InterruptedException {
		List<String> tempos = new ArrayList<String>();
		Instant ini = Instant.now();
		List<Movie> movies = Collections.synchronizedList(new ArrayList<>());
		// List<Movie> movies = new ArrayList<Movie>();

		final CountDownLatch latch = new CountDownLatch(3);

		Thread t1 = new Thread(() -> {
			fileToList("movies1.csv", 1, movies);
			latch.countDown();
		});
		Thread t2 = new Thread(() -> {
			fileToList("movies2.csv", 0, movies);
			latch.countDown();
		});
		Thread t3 = new Thread(() -> {
			fileToList("movies3.csv", 0, movies);
			latch.countDown();
		});

		t1.start();
		t2.start();
		t3.start();

		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Map<String, List<Movie>> mapAnoFilme = new HashMap<>();
		movies.stream().map(m -> m.getYear()).distinct()
				.forEach(a -> mapAnoFilme.put(a,
						movies.stream().filter(m -> m.getYear().equals(a))
								.sorted(Comparator.comparing(Movie::getRating).reversed()).limit(50)
								.collect(Collectors.toList())));
		int qtThreads = mapAnoFilme.size();

		final CountDownLatch latch2 = new CountDownLatch(qtThreads + 1);

		List<Thread> arrThread = new ArrayList<Thread>();

		for (Map.Entry<String, List<Movie>> entry : mapAnoFilme.entrySet()) {
			arrThread.add(new Thread(() -> {
				try {
					gerarArquivo(entry.getKey(), entry.getValue());
				} catch (RuntimeException | IOException e) {
					e.printStackTrace();
				}
				latch2.countDown();
			}));
		}

		arrThread.forEach(Thread::start);

		Thread t4 = new Thread(() -> {
			try {
				gerarArquivo("Filmes de Terror",
						movies.stream().filter(m -> m.isOfGenre("Horror"))
								.sorted(Comparator.comparing(Movie::getRating).reversed()).limit(20)
								.collect(Collectors.toList()));
			} catch (RuntimeException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			latch2.countDown();
		});

		t4.start();

		try {
			latch2.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Instant fim = Instant.now();
		Duration delta = Duration.between(ini, fim);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SS")
				.withZone(ZoneId.systemDefault());
		tempos.add("Início processamento: " + formatter.format(ini));
		tempos.add("Fim processamento: " + formatter.format(fim));
		tempos.add("Tempo em milisegundos: " + delta.toMillis() + " milisegundos");
		tempos.add("Tempo em segundos: " + delta.toSeconds() + " segundos");
		// System.out.println("Início processamento: " + formatter.format(ini));
		// System.out.println("Fim processamento: " + formatter.format(fim));
		// System.out.println("Tempo em milisegundos: " + delta.toMillis() + "
		// milisegundos");
		// System.out.println("Tempo em segundos: " + delta.toSeconds() + " segundos");
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