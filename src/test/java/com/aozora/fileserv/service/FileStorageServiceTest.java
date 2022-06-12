/**
 * 
 */
package com.aozora.fileserv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import com.aozora.fileserv.config.FileServiceProperties;

/**
 * @author SabinaM
 *
 */
@ExtendWith(MockitoExtension.class)
public class FileStorageServiceTest {
	
	@InjectMocks
	private FileStorageService fileStorageService;
	
	@Mock
	private FileServiceProperties serviceProperties;
		
	private String fileName;
	private String directoryPath;
	private String dataDirectoryPath;
	
	/**
	 * 
	 */
	public FileStorageServiceTest() {
		// TODO Auto-generated constructor stub
	}
	@BeforeEach
	void setUp() throws Exception {
			fileName = "PathMatcher.txt";
			directoryPath ="C:/workspace_sts4-14/fileserv/bin/main/static/uploads/";
			dataDirectoryPath = "C:/workspace_sts4-14/fileserv/bin/main/static/data/";
			
			
	}
	@Test
	void givenFileWhenGettingResourceInUploads_ThenCorrect() {
		 when(serviceProperties.getUploadDir()).thenReturn(directoryPath);
		 Resource resource = fileStorageService.getResourceInUploads(fileName);
			assertTrue(resource.exists());
			assertEquals(resource.getFilename(),fileName);
			assertTrue(resource.isReadable());
		 Mockito.verify(serviceProperties, times(1)).getUploadDir();
		
	}
	
	@Test
	void givenFileWhenGettingResourceInData_ThenCorrect() {
		
		when(serviceProperties.getDIRECTORY_PATH()).thenReturn(dataDirectoryPath);
		Resource resource = fileStorageService.getResourceInData(fileName);
		Mockito.verify(serviceProperties, times(1)).getDIRECTORY_PATH();
		assertNotNull(resource);
			assertTrue(resource.exists());
			assertEquals(resource.getFilename(),fileName);
			assertTrue(resource.isReadable());
		
	}
	
	@Test
	void givenPatternWhenGettingAllThatMatchRegex_ThenCorrect() {
		String pattern ="^[a-zA-Z0-9._ -]+\\.(doc|pdf|csv|txt)$";
		String patternSyntax = "regex";
		Set<Resource> actualResources=fileStorageService.getAllThatMatchRegex(pattern, patternSyntax, dataDirectoryPath);
		Set<String> actualFilenames= new HashSet<>();
		actualResources.forEach( r -> actualFilenames.add(r.getFilename()));
		/*
		 * [ "words.txt", "StoreAddressList.pdf",
		 * "8-Reasons-AStartupOverACorporateJob.pdf", 
		 * "PathMatcher.txt", "50-contacts.csv" ]
		 */
		Set<String> expectedFilenames = new HashSet<>();
		expectedFilenames.add("words.txt");
		expectedFilenames.add("StoreAddressList.pdf");
		expectedFilenames.add("8-Reasons-AStartupOverACorporateJob.pdf");
		expectedFilenames.add("PathMatcher.txt");
		expectedFilenames.add("50-contacts.csv" );
		assertEquals(expectedFilenames.size(), actualFilenames.size());
		assertTrue(actualFilenames.contains("words.txt"));		
		
	}
	
	@Test
	void givenPatternEmptyDirectoryPathWhenGettingAllThatMatchRegex_ThenIncorrect() {
		String pattern ="^[a-zA-Z0-9._ -]+\\.(doc|pdf|csv|txt)$";
		String patternSyntax = "regex";
		
		Exception exception= assertThrows(NullPointerException.class,()->{
			Set<Resource> actualResources=fileStorageService.getAllThatMatchRegex(pattern, patternSyntax, "");
		});
		
	}
	
	@Test
	void givenWrongPatternWhenGettingAllThatMatchRegex_ThenIncorrect() {
		String pattern ="*.txt";
		String patternSyntax = "regex";
		
		Exception exception= assertThrows(PatternSyntaxException.class,()->{
			 Set<Resource> actualResources = fileStorageService.getAllThatMatchRegex(pattern, patternSyntax, directoryPath);
		});
		
	
	}

}
