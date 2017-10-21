package com.empty_stack.wikipedia_abstract_index_creator;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import lombok.SneakyThrows;

/**
 * This class with its associated {@link Main#main(String[]) main method} downloads several xml dumps of
 * the german part of wikipedia.
 * These downloads are immediatly {@link WikipediaXmlParser parsed and indexed} into a newly created
 * lucene index.
 * 
 * @author Maik Fröbe
 *
 */
public class Main
{
	private static final IndexWriter INDEX_WRITER = createConfiguredIndexWriter();
	
	@SneakyThrows
	public static void main(String[] args)
	{
		Stream.of
			(
//				"https://dumps.wikimedia.org/dewiki/20170601/dewiki-20170601-abstract1.xml", 
//				"https://dumps.wikimedia.org/dewiki/20170601/dewiki-20170601-abstract2.xml",
//				"https://dumps.wikimedia.org/dewiki/20170601/dewiki-20170601-abstract3.xml",
//				"https://dumps.wikimedia.org/dewiki/20170601/dewiki-20170601-abstract4.xml"
				"dewiki-20170601-abstract1.xml",
				"dewiki-20170601-abstract2.xml",
				"dewiki-20170601-abstract3.xml",
				"dewiki-20170601-abstract4.xml"
				
			)
			.forEach(Main::downloadXmlAndAddToIndex);
		
		INDEX_WRITER.close();
	}
	
	@SneakyThrows
	private static void downloadXmlAndAddToIndex(String url)
	{
		System.out.println("Start with "+ url);
		
//		new WikipediaXmlParser(INDEX_WRITER).parseXmlWhileAddingToIndex(new URL(url).openStream());
		new WikipediaXmlParser(INDEX_WRITER).parseXmlWhileAddingToIndex(new FileInputStream(new File(url)));
		
		System.out.println("Finnished with "+ url);
	}
	
	/**
	 * Creates and returns an {@link IndexWriter}.
	 * 
	 * If there exists already a file or directory with the name of the index to create this file/directory is deleted.
	 * 
	 * @return
	 * 		An newly created {@link IndexWriter}
	 */
	@SneakyThrows
	private static IndexWriter createConfiguredIndexWriter()
	{
		File indexDirectory = Paths.get("lucene_index").toFile();
		
		if(indexDirectory.exists())
		{
			indexDirectory.delete();
		}
		
		return new IndexWriter(FSDirectory.open(Paths.get("lucene_index")),
				new IndexWriterConfig(new StandardAnalyzer()));
	}
}
