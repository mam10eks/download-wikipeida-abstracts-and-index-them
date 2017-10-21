package com.empty_stack.wikipedia_abstract_index_creator;

import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

import lombok.Data;
import lombok.SneakyThrows;

/**
 * This class uses a streaming xml parser to parse xml retrieved from a {@link InputStream}
 * which needs to be in a predefined format (See the associated tests in case the schema changes).
 * 
 * 
 * @author Maik Fröbe
 *
 */
@Data
public class WikipediaXmlParser
{
	private final IndexWriter indexWriter;
	
	public static final String INDEX_FIELD_TITLE = "title";
	
	public static final String INDEX_FIELD_CONTENT = "content";
	
	public static final String INDEX_FIELD_LINK = "link";

	@SneakyThrows
	public void parseXmlWhileAddingToIndex(InputStream inputStream)
	{
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(inputStream);
		Document document = new Document();
		
		while(xmlEventReader.hasNext())
		{
			XMLEvent xmlEvent = xmlEventReader.nextEvent();
			
			if(xmlEvent.isStartElement())
			{
				if(xmlEvent.asStartElement().getName().getLocalPart().equals("title"))
				{
					addCharacterContentAsDocumentField(INDEX_FIELD_TITLE, document, xmlEventReader);
				}
				else if(xmlEvent.asStartElement().getName().getLocalPart().equals("abstract"))
				{
					addCharacterContentAsDocumentField(INDEX_FIELD_CONTENT, document, xmlEventReader);
				}
				else if(xmlEvent.asStartElement().getName().getLocalPart().equals("url"))
				{
					addCharacterContentAsDocumentField(INDEX_FIELD_LINK, document, xmlEventReader);
				}
			}
			else if(xmlEvent.isEndElement())
			{
				if(xmlEvent.asEndElement().getName().getLocalPart().equals("doc"))
				{
					if(document.get(INDEX_FIELD_LINK) == null || document.get(INDEX_FIELD_TITLE) == null)
					{
						throw new RuntimeException("The link and title field are mandatory");
					}
					
					indexWriter.addDocument(document);
					document = new Document();
				}
			}
		}
	}
	
	@SneakyThrows
	private static final void addCharacterContentAsDocumentField(String fieldName, Document document, XMLEventReader eventReader)
	{
		XMLEvent xmlEvent = null;
		
		if(eventReader.hasNext() && (xmlEvent = eventReader.nextEvent()).isCharacters())
		{
			document.add(new TextField(fieldName, xmlEvent.asCharacters().getData(), Field.Store.YES));
		}
	}
}
