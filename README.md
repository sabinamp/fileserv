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

### REST API<hr />
<ul>
	<li>
	<p> POST /fileservice/files/matchedfiles?syntax=regex</p>
    <p>-the pattern and directory in request body as JSON: 
	<pre><code> { "pattern": "^[a-zA-Z0-9._ -]+\\.(doc|pdf|csv|txt)$", "directory": "C:/workspace_sts4-14/fileserv/bin/main/static/data"}</code></pre>
	</p>
	</li>
	
   <li> 
   <p> POST http://localhost:8082/fileservice/files/matchedfiles?syntax=regex </p>
	<p> -the pattern and directory in request body as JSON: <br/>
	<pre><code> {"pattern": "^[a-zA-Z0-9._ -]+\\.(doc|pdf|csv|txt)$", "directory": "C:/workspace_sts4-14/fileserv/bin/main/static/uploads" }
	 </code></pre>
	 Content-Type: application/json <br/>
	 Accept: application/json <br/>
	- Response body: <br/>
	<pre><code>["StoreAddressList.pdf", "words.txt", "8-Reasons-AStartupOverACorporateJob.pdf", "PathMatcher.txt", "50-contacts.csv"] </code></pre>
	</p>
	</li>
	<li>
	<p> POST http://localhost:8082/fileservice/content/freqwords?n=10 </p>
	<p>
	- Request body json: 
				<pre><code>{"fileName": "AppPropsExamples.txt", "directory": "C://workspace_sts4-14//fileserv//bin//main//static//uploads"}</code></pre>
				<br/>
	- ResponseBody : 
		<pre><code>[{ "the frequency: 37": "[#]"}, {"the frequency: 20": "[spring]"}, {"the frequency: 16": "[security]"}, {"the frequency: 15": "[the, of]"}, {"the frequency: 11": "[server, datasource]"}, {"the frequency: 10": "[to]"}, {"the frequency: 8": "[user]"}, {"the frequency: 6": "[view]"}]
		</code></pre>
		</p>
	</li>
</li> 
	<p> POST http://localhost:8082/fileservice/content/longestwords </p>
	<p>- Request body json: 
	<pre><code>{ "fileName": "PathMatcher.txt", "directory": "C://workspace_sts4-14//fileserv//bin//main//static//uploads } </code></pre>
	- ResponseBody : 
	<pre><code>[ {"the current line number: 1": "[PathMatcher, implementation]", "length 11": "PathMatcher", "length 14": "implementation"}, 
	    {"the current line number: 4": "[PathMatcher, interface]", "length 11": "PathMatcher", "length 9": "interface" } ]</code></pre>
	Content-Type: application/json
	Accept: application/json
	</p>
</li>
</ul>