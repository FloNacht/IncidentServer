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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.model.*;
import com.storage.*;

import java.net.URI;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

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

	@RequestMapping(method = RequestMethod.GET, path = "/greeting")
	public Greeting greeting(@PathVariable String userId) {
		return new Greeting(counter.incrementAndGet(), String.format(template, userId));
	}

	@RequestMapping(method = RequestMethod.GET, path = "/{incidentId}")
	public Incident readIncident(@PathVariable String userId, @PathVariable Long incidentId) {
		this.validateUser(userId);
		return this.incidentRepository.findOne(incidentId);
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/userIncidents")
	public Collection<Incident> readIncidents(@PathVariable String userId) {
		this.validateUser(userId);
		return this.incidentRepository.findByUserUsername(userId);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/all")
	public Collection<Incident> readAllIncidents(@PathVariable String userId) {
		this.validateUser(userId);
		return this.incidentRepository.findByActiveTrue();
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/allArchieved")
	public Collection<Incident> readAllArchievedIncidents(@PathVariable String userId) {
		this.validateUser(userId);
		return this.incidentRepository.findByActiveFalse();
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> add(@PathVariable String userId, @RequestBody Incident input) {
		this.validateUser(userId);

		return this.userRepository.findByUsername(userId).map(user -> {
			Incident result = incidentRepository.save(new Incident(user, input.getLocation(), input.getDescription(), input.getExactLocation(), input.getImagePath()));

			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId())
					.toUri();

			return ResponseEntity.created(location).build();
		}).orElse(ResponseEntity.noContent().build());

	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/archieve/{incidentId}")
	public ResponseEntity<?> archieve(@PathVariable String userId, @PathVariable Long incidentId) {
		this.validateUser(userId);
		this.incidentRepository.findOne(incidentId).setActive(false);
		incidentRepository.flush();
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/reactivate/{incidentId}")
	public ResponseEntity<?> reactivate(@PathVariable String userId, @PathVariable Long incidentId) {
		this.validateUser(userId);
		this.incidentRepository.findOne(incidentId).setActive(true);
		incidentRepository.flush();
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/file/{filename}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"") //extracts Content-Type from path variable 'filename'
				.body(file);
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/{incidentId}/file")
	@ResponseBody
	public ResponseEntity<Resource> serveIncidentFile(@PathVariable Long incidentId) {

		Incident incident = this.incidentRepository.findOne(incidentId);

		if (incident == null) {
			throw new StorageFileNotFoundException("Incident not found.");
		}

		Resource file = storageService.loadAsResource(incident.getImagePath());

		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"");

		switch (file.getFilename().substring(file.getFilename().lastIndexOf(".") + 1)) {
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

		return ResponseEntity.ok().headers(header).body(file);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/file")
	public String handleFileUpload(@PathVariable String userId, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		this.validateUser(userId);
		storageService.store(file, userId);
		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");
		return userId + "_" + file.getOriginalFilename();
	}
	
//	@RequestMapping(method = RequestMethod.GET, path = "/files")
//	public String listUploadedFiles(Model model) throws IOException {
//
//		model.addAttribute("files", storageService.loadAll()
//				.map(path -> MvcUriComponentsBuilder
//						.fromMethodName(IncidentRestController.class, "serveFile", path.getFileName().toString())
//						.build().toString())
//				.collect(Collectors.toList()));
//
//		return "uploadForm";
//	}
	
	private void validateUser(String userId) {
		this.userRepository.findByUsername(userId).orElseThrow(() -> new UserNotFoundException(userId));
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	private ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		exc.printStackTrace();
		return ResponseEntity.notFound().build();
	}
}