# FileService Gradle Project with SpringBoot 2.7.0

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
		"directory": "C:/workspace_sts4-14/fileservice/bin/main/static/data"
	}
	
** POST http://localhost:8082/fileservice/files/matchedfiles?syntax=regex
	 -the pattern and directory in request body as JSON: 
	 {
		"pattern": "^[a-zA-Z0-9._ -]+\\.(doc|pdf|csv|txt)$",
		"directory": "C:/workspace_sts4-14/fileservice/bin/main/static/data"
	}
	Content-Type: text/plain
	Accept: application/json
	Response: 
	[
	    "StoreAddressList.pdf",
	    "words.txt",
	    "8-Reasons-AStartupOverACorporateJob.pdf",
	    "PathMatcher.txt",
	    "50-contacts.csv"
	]
	
