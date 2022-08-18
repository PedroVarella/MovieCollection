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
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;

public class Aplicacao {

	private static final String CAMINHO_ABSOLUTO = System.getProperty("user.dir") + "/src/main/java/";
	private static final Charset UTF8 = Charset.forName("UTF-8");

	public static void main(String[] args) throws IOException, InterruptedException {
		List<String> tempos = new ArrayList<String>();
		Instant ini = Instant.now();
		List<Movie> movies = Collections.synchronizedList(new ArrayList<>());
		CountDownLatch cdl = new CountDownLatch(3);

		//TODO -> Criar as threads
		// Usando o NIO e apenas streams
		Path p = Paths.get(CAMINHO_ABSOLUTO + "movies1.csv");
		Path p2 = Paths.get(CAMINHO_ABSOLUTO + "movies2.csv");
		Path p3 = Paths.get(CAMINHO_ABSOLUTO + "movies3.csv");
		Thread t1 = new Thread(() -> {
			try {
				Files.lines(p, UTF8).skip(1).map(l -> l.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)).forEach(arr -> {
					movies.add(new Movie(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8], arr[9],
							arr[10], arr[11]));
				});
			} catch (Exception e) {
				System.out.println("Erro");
			}
			cdl.countDown();
		} );
		Thread t2 = new Thread(() -> {
			try {
				Files.lines(p2, UTF8).skip(0).map(l -> l.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)).forEach(arr -> {
					movies.add(new Movie(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8], arr[9],
							arr[10], arr[11]));
				});
			} catch (Exception e) {
				System.out.println("Erro");
			}
			cdl.countDown();
		} );
		Thread t3 = new Thread(() -> {
			try {
				Files.lines(p3, UTF8).skip(0).map(l -> l.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)).forEach(arr -> {
					movies.add(new Movie(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8], arr[9],
							arr[10], arr[11]));
				});
			} catch (Exception e) {
				System.out.println("Erro");
			}
			cdl.countDown();
		} );
		t1.start();
		t2.start();
		t3.start();
		cdl.await();
		
		System.out.println(movies.size());
		// Lista dos 50 melhores por ano
		Map<String, List<Movie>> mapAnoFilme = new HashMap<>();
		movies.stream().map(m -> m.getYear()).distinct()
				.forEach(a -> mapAnoFilme.put(a,
						movies.stream().filter(m -> m.getYear().equals(a))
								.sorted(Comparator.comparing(Movie::getRating).reversed()).limit(50)
								.collect(Collectors.toList())));
		// Iterando no mapa
		CountDownLatch cdl2 = new CountDownLatch(mapAnoFilme.size()+1);
		List<Thread> threads = new ArrayList<Thread>();
		threads.add(new Thread(() -> {
			try {
				gerarArquivo("Filmes de Terror", movies.stream().filter(m -> m.isOfGenre("Horror"))
				.sorted(Comparator.comparing(Movie::getRating).reversed()).limit(20).collect(Collectors.toList()));
				cdl2.countDown();	
			} catch (Exception e) {
				System.out.println("erro");
			}
			
		}));
		mapAnoFilme.forEach((ano, lista) -> {
				threads.add(new Thread(() -> {
					try {
						gerarArquivo(ano, lista);
					cdl2.countDown();	
					} catch (Exception e) {
						System.out.println("erro");
					}
					
				}));
		});
		threads.forEach(Thread::start);
		cdl2.await();
		
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
