# FileService Gradle Project with SpringBoot 2.7.0
### Technologies used
* Spring Boot 2.7.0
* Java 17
* JUnit5, Mockito
* Gradle 
* Log4j2

### RESTful File Service

* FileService Implementation of the following requirements:

* Return all files inside a directory located on the server side. The files name should match a regular expression given as end point input. Directory name and path is defined on the server side.

* For a given input text file return the most frequent 10 words together with the number of occurrences of each word.

* For a given input text file, return for each line in the file the longest 2 words. In case there are more than 2 words with the same length on a line, filter them based on their frequency in the whole document and return the 2 most frequent words.

* uploading File to a static folder on the server

* getting list of Files’ information (file names)

### REST API
* POST /fileservice/files/matchedfiles?syntax=regex
 -the pattern and directory in request body as JSON: 
	 {
		"pattern": "^[a-zA-Z0-9._ -]+\\.(doc|pdf|csv|txt)$",
		"directory": "C:/workspace_sts4-14/fileserv/bin/main/static/data"
	}
	
	
* POST http://localhost:8082/fileservice/files/matchedfiles?syntax=regex
	 -the pattern and directory in request body as JSON: 
	 {
		"pattern": "^[a-zA-Z0-9._ -]+\\.(doc|pdf|csv|txt)$",
		"directory": "C:/workspace_sts4-14/fileserv/bin/main/static/uploads"
	 }
	Content-Type: text/plain
	Accept: application/json
	- Response body: 
	[
	    "StoreAddressList.pdf",
	    "words.txt",
	    "8-Reasons-AStartupOverACorporateJob.pdf",
	    "PathMatcher.txt",
	    "50-contacts.csv"
	]
	
	
* POST http://localhost:8082/fileservice/content/freqwords?n=10

- Request body json: 
				{ "fileName": "AppPropsExamples.txt",
  				"directory": "C://workspace_sts4-14//fileserv//bin//main//static//uploads"
				}
- ResponseBody : 
		[
		    {
		        "the frequency: 37": "[#]"
		    },
		    {
		        "the frequency: 20": "[spring]"
		    },
		    {
		        "the frequency: 16": "[security]"
		    },
		    {
		        "the frequency: 15": "[the, of]"
		    },
		    {
		        "the frequency: 11": "[server, datasource]"
		    },
		    {
		        "the frequency: 10": "[to]"
		    },
		    {
		        "the frequency: 8": "[user]"
		    },
		    {
		        "the frequency: 6": "[view]"
		    }
		]

* POST http://localhost:8082/fileservice/content/longestwords
- Request body json: { "fileName": "PathMatcher.txt",
					   "directory": "C://workspace_sts4-14//fileserv//bin//main//static//uploads }
- ResponseBody : 
[
    {
        "the current line number: 1": "[PathMatcher, implementation]",
        "length 11": "PathMatcher",
        "length 14": "implementation"
    },
    {
        "the current line number: 4": "[PathMatcher, interface]",
        "length 11": "PathMatcher",
        "length 9": "interface"
    }
]
Content-Type: application/json
Accept: application/json