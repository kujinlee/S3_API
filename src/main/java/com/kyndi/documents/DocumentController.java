package com.kyndi.documents;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentController {
	
	private final Logger logger = LoggerFactory.getLogger(DocumentController.class);
	
	@Autowired
	private DocumentService docSvc;
	
	@Autowired
	private DocumentCache docCache;
	
	private String cachePath;
	
	@Value("${cache-path}")
	public void setCachePath(String cachePath) {
		this.cachePath = cachePath;
	}
	
	@RequestMapping(value = "/documents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Document> getDocuments() {
		return docSvc.findAll();
		
	}

	@RequestMapping(value = "/documents/{filename:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> downloadDocumentByName(
			@PathVariable(value = "filename") String filename) {

		ResponseEntity<InputStreamResource> responseEntity = null;

		InputStream itemInputStream = docSvc.getFile(filename);
		docCache.putItem(filename, itemInputStream);
		// TODO: enhance. docCache.putItem closes inputStream. re-create inputSteam
		itemInputStream = docSvc.getFile(filename);

		responseEntity = ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE))
				.header("Content-disposition", "attachment;filename=" + filename).body(new InputStreamResource(itemInputStream));

		return responseEntity;

	}
	
	@RequestMapping(value = "/documents/{filename:.+}/metadata", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Document getDocument(@PathVariable(value = "filename") String filename) {
		return docSvc.findByName(filename);	
	}
	
}
