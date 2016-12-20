package com.imservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.model.*;
import com.storage.*;

import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <h1>Spring RestController</h1> This class serves as a rest controller mapping
 * the http calls to java methods. The methods call reading or writing
 * operations on the database.
 *
 * Handles all incident related http calls, including file up- & download.
 * 
 * @author Florian Nachtigall
 * @version 1.0
 * @since 2016-12-20
 */
@RequestMapping("/incident/{userId}")
@RestController
public class IncidentRestController {

	private final IncidentRepository incidentRepository;
	private final UserRepository userRepository;
	private final StorageService storageService;

	private final AtomicLong counter = new AtomicLong();
	private static final String template = "Hello, %s!";

	@Autowired
	IncidentRestController(IncidentRepository incidentRepository, UserRepository userRepository,
			StorageService storageService) {
		this.incidentRepository = incidentRepository;
		this.userRepository = userRepository;
		this.storageService = storageService;
	}

	/**
	 * This method is used to test the rest api and the accessibility of the server.
	 * @return Greeting - Test Object  for this use case only
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/greeting")
	public Greeting greeting(@PathVariable String userId) {
		return new Greeting(counter.incrementAndGet(), String.format(template, userId));
	}

	/**
	 * This method is used read a single incident.
	 * 
	 * @param incidentId uniquely identifying an incident
	 * @param userId to validate the user
	 * @return Incident If found, return incident with this id
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/{incidentId}")
	public Incident readIncident(@PathVariable String userId, @PathVariable Long incidentId) {
		this.validateUser(userId);
		return this.incidentRepository.findOne(incidentId);
	}

	/**
	 * This method is used read a all incidents from a user.
	 * 
	 * @param userId to validate the user and find his/her incidents
	 * @return List<Incident> List containing all incidents from the user
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/userIncidents")
	public List<Incident> readIncidents(@PathVariable String userId) {
		this.validateUser(userId);
		return this.incidentRepository.findByUserUsername(userId);
	}

	/**
	 * This method is used read all active incidents.
	 * 
	 * @param userId to validate the user
	 * @return List<Incident> List containing all incidents not archived yet
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/all")
	public List<Incident> readAllIncidents(@PathVariable String userId) {
		this.validateUser(userId);
		return this.incidentRepository.findByActiveTrue();
	}

	/**
	 * This method is used read all archived incidents.
	 * 
	 * @param userId to validate the user
	 * @return List<Incident> List containing all archived incidents
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/allArchieved")
	public List<Incident> readAllArchievedIncidents(@PathVariable String userId) {
		this.validateUser(userId);
		return this.incidentRepository.findByActiveFalse();
	}
	
	/**
	 * This method is used to create and add a new incident to the database.
	 * 
	 * @param userId to validate the user
	 * @return ResponseEntity<?> With status code and uri of saved incident
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> add(@PathVariable String userId, @RequestBody Incident input) {
		this.validateUser(userId);

		return this.userRepository.findByUsername(userId).map(user -> {
			Incident result = incidentRepository.save(new Incident(user, input.getTitel(), input.getLocation(),
					input.getExactLocation(), input.getDescription(), input.getImagePath()));

			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId())
					.toUri();

			return ResponseEntity.created(location).build();
		}).orElse(ResponseEntity.noContent().build());

	}

	/**
	 * This method is used to archive an incident.
	 * 
	 * @param userId to validate the user
	 * @param incidentId uniquely identifying an incident
	 * @return ResponseEntity<?> With status code stating success or failure of operation
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/archieve/{incidentId}")
	public ResponseEntity<?> archieve(@PathVariable String userId, @PathVariable Long incidentId) {
		this.validateUser(userId);
		this.incidentRepository.findOne(incidentId).setActive(false);
		incidentRepository.flush();
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * This method is used to reactive an incident from the archive.
	 * 
	 * @param userId to validate the user	 
	 * @param incidentId uniquely identifying an incident
	 * @return ResponseEntity<?> With status code stating success or failure of operation
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/reactivate/{incidentId}")
	public ResponseEntity<?> reactivate(@PathVariable String userId, @PathVariable Long incidentId) {
		this.validateUser(userId);
		this.incidentRepository.findOne(incidentId).setActive(true);
		incidentRepository.flush();
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * This method is used to download a file previously uploaded to the server using the the filename.
	 *  
	 * @param filename Identifying the file among others and defining the file type
	 * @return ResponseEntity<Resource> Returning the file as a multipart/form-data body of a ReponseEntity
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/file")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@RequestParam String filename) {
		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().headers(this.creatingHttpHeaderforFileTransfer(file.getFilename())).body(file);
	}

	/**
	 * This method is used to download a file previously uploaded to the server using the id of the corresponding incident.
	 *  
	 * @param incidentId uniquely identifying an incident
	 * @return ResponseEntity<Resource> Returning the file as a multipart/form-data body of a ReponseEntity
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/{incidentId}/file")
	@ResponseBody
	public ResponseEntity<Resource> serveIncidentFile(@PathVariable Long incidentId) {
		Incident incident = this.incidentRepository.findOne(incidentId);
		if (incident == null) {
			throw new StorageFileNotFoundException("Incident not found.");
		}
		Resource file = storageService.loadAsResource(incident.getImagePath());
		return ResponseEntity.ok().headers(this.creatingHttpHeaderforFileTransfer(file.getFilename())).body(file);
	}

	/**
	 * This method is used to upload a file to the server.
	 *  
	 * @param userId To validate the user
	 * @param filename Identifying the file among others and defining the file type
	 * @return ResponseEntity<?> With status code stating success or failure of operation and the uri location of the uploaded file
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/file")
	public ResponseEntity<?> handleFileUpload(@PathVariable String userId, @RequestParam("file") MultipartFile file) {
		this.validateUser(userId);
		storageService.store(file);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.queryParam("filename", file.getOriginalFilename()).build().toUri();
		return ResponseEntity.created(location).build();
	}
	
	/**
	 * This private method is used validate a user.
	 *  
	 * @param userId to validate the user
	 * @exception UserNotFoundException In case username is not valid
	 */
	private void validateUser(String userId) {
		this.userRepository.findByUsername(userId).orElseThrow(() -> new UserNotFoundException(userId));
	}

	/**
	 * This is private helper method to build a http header for file handling with respective content-type
	 *  
	 * @param filename to extract file type
	 */
	private HttpHeaders creatingHttpHeaderforFileTransfer(String filename) {
		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

		switch (filename.substring(filename.lastIndexOf(".") + 1)) {
		case "jpg":
			header.add(HttpHeaders.CONTENT_TYPE, "image/jpg");
			break;
		case "gif":
			header.add(HttpHeaders.CONTENT_TYPE, "image/gif");
			break;
		case "png":
			header.add(HttpHeaders.CONTENT_TYPE, "image/png");
			break;
		default:
			throw new StorageFileNotFoundException("Not an image type.");
		}
		return header;
	}

	/**
	 * This is a class exception handler.
	 * If a StorageFileNotFoundException is thrown in a method of this class, it is handled here.
	 *  
	 * @param StorageFileNotFoundException Including all information of the exception
	 * @return ResponseEntity<> With http 404 not found status code
	 */
	@ExceptionHandler(StorageFileNotFoundException.class)
	private ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		exc.printStackTrace();
		return ResponseEntity.notFound().build();
	}
}