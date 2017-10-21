package com.empty_stack.wikipedia_abstract_index_creator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.approvaltests.Approvals;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import lombok.SneakyThrows;

/**
 * 
 * @author Maik Fröbe
 *
 */
public class WikipediaXmlParserTest
{
	private WikipediaXmlParser xmlParser;
	
	private List<Iterable<? extends IndexableField>> documentsAddedToIndex;
	
	@Before
	@SneakyThrows
	public void prepareTest()
	{
		documentsAddedToIndex = new ArrayList<>();
		IndexWriter mockedIndexWriter = Mockito.mock(IndexWriter.class);
		Mockito.when(mockedIndexWriter.addDocument(Matchers.any())).thenAnswer(new Answer<Long>()
		{
			@Override
			public Long answer(InvocationOnMock invocation) throws Throwable
			{
				if(invocation.getArguments() == null || invocation.getArguments().length != 1 || 
					!(invocation.getArguments()[0] instanceof Document))
				{
					throw new RuntimeException("It is assumed that the mocked index writer only indexes Documents");
				}
				
				documentsAddedToIndex.add((Document)invocation.getArguments()[0]);
				return 0l;
			}
		});
		
		xmlParser = new WikipediaXmlParser(mockedIndexWriter);
	}
	
	private static InputStream testResourceAsInputStream(String resourceName)
	{
		return WikipediaXmlParserTest.class.getResourceAsStream(resourceName);
	}
	
	@Test
	public void approveInputWithAbstractAndUrlAndTitle()
	{
		xmlParser.parseXmlWhileAddingToIndex(testResourceAsInputStream("InputWithAbstractAndUrlAndTitle.xml"));
		
		Approvals.verify(documentsAddedToIndex);
	}
	
	@Test
	public void approveInputWithUrl()
	{
		xmlParser.parseXmlWhileAddingToIndex(testResourceAsInputStream("InputWithUrlAndTitle.xml"));
		
		Approvals.verify(documentsAddedToIndex);
	}
	
	@Test(expected=RuntimeException.class)
	public void checkThatInputWithoutTitleThrowsAnException()
	{
		xmlParser.parseXmlWhileAddingToIndex(testResourceAsInputStream("InputWithoutTitle.xml"));
	}
	
	@Test(expected=RuntimeException.class)
	public void checkThatInputWithoutUrlThrowsAnException()
	{
		xmlParser.parseXmlWhileAddingToIndex(testResourceAsInputStream("InputWithoutUrl.xml"));
	}
}
