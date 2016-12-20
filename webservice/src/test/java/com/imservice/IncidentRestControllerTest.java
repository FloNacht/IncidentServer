package com.imservice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.model.*;
import com.storage.StorageService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = WebserviceApplication.class)
@WebAppConfiguration
public class IncidentRestControllerTest {

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	private MockMvc mockMvc;

	private String userName = "bdussault";

	private HttpMessageConverter mappingJackson2HttpMessageConverter;

	private User user;

	private List<Incident> incidentList = new ArrayList<>();
	
	@Autowired
	private IncidentRepository incidentRepository;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private StorageService storageService;

	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {

		this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
				.filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().orElse(null);

		assertNotNull("the JSON message converter must not be null", this.mappingJackson2HttpMessageConverter);
	}

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();

		this.incidentRepository.deleteAllInBatch();
		this.userRepository.deleteAllInBatch();
		
		this.user = userRepository.save(new User(userName, "password"));
				
		this.incidentList.add(incidentRepository.save(new Incident(user, "A location", "An exact location", "A description", "image dir")));
		this.incidentList.add(incidentRepository.save(new Incident(user, "A location 2", "An exact location 2", "A description 2", "image dir 2")));
		this.incidentList.add(incidentRepository.save(new Incident(user, "A location 3", "An exact location 3", "A description 3", "image dir 3")));

	}

	@Test
	public void userNotFound() throws Exception {
		mockMvc.perform(post("/incidents/peter").content(this.json(new Incident())).contentType(contentType))
				.andExpect(status().isNotFound());
	}

	@Test
	public void readSingleIncident() throws Exception {
		mockMvc.perform(get("/incident/" + userName + "/" + this.incidentList.get(0).getId()))
				.andExpect(status().isOk()).andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.id", is(this.incidentList.get(0).getId().intValue())))
				.andExpect(jsonPath("$.location", is("A location")))
				.andExpect(jsonPath("$.exactLocation", is("An exact location")))
				.andExpect(jsonPath("$.description", is("A description")))
				.andExpect(jsonPath("$.imagePath", is("image dir")))
				.andExpect(jsonPath("$.actice", is(true)));

	}

	@Test
	public void readIncidents() throws Exception {
		mockMvc.perform(get("/incident/" + userName + "/all")).andExpect(status().isOk())
				.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].id", is(this.incidentList.get(0).getId().intValue())))
				.andExpect(jsonPath("$[0].location", is("A location")))
				.andExpect(jsonPath("$[0].exactLocation", is("An exact location")))
				.andExpect(jsonPath("$[0].description", is("A description")))
				.andExpect(jsonPath("$[0].imagePath", is("image dir")))
				.andExpect(jsonPath("$[0].actice", is(true)))
				.andExpect(jsonPath("$[1].id", is(this.incidentList.get(1).getId().intValue())))
				.andExpect(jsonPath("$[1].location", is("A location 2")))
				.andExpect(jsonPath("$[1].exactLocation", is("An exact location 2")))
				.andExpect(jsonPath("$[1].description", is("A description 2")))
				.andExpect(jsonPath("$[1].imagePath", is("image dir 2")))
				.andExpect(jsonPath("$[1].actice", is(true)))
				.andExpect(jsonPath("$[2].id", is(this.incidentList.get(2).getId().intValue())))
				.andExpect(jsonPath("$[2].location", is("A location 3")))
				.andExpect(jsonPath("$[2].exactLocation", is("An exact location 3")))
				.andExpect(jsonPath("$[2].description", is("A description 3")))
				.andExpect(jsonPath("$[2].imagePath", is("image dir 3")))
				.andExpect(jsonPath("$[2].actice", is(true)));;
	}

	@Test
	public void createIncident() throws Exception {
		String incidentJson = json(new Incident(user, "A location 4", "An exact location 4", "A description 4", "image dir 4"));

		this.mockMvc.perform(post("/incident/" + userName).contentType(contentType).content(incidentJson))
				.andExpect(status().isCreated());
	}
	
	@Test
	public void archieveIncident() throws Exception {
		this.mockMvc.perform(post("/incident/" + userName + "/archieve/" + this.incidentList.get(1).getId()))
				.andExpect(status().isOk());
	}
	
	@Test
	public void reactivateIncident() throws Exception {
		this.mockMvc.perform(post("/incident/" + userName + "/archieve/" + this.incidentList.get(2).getId()))
				.andExpect(status().isOk());
		this.mockMvc.perform(post("/incident/" + userName + "/reactivate/" + this.incidentList.get(2).getId()))
				.andExpect(status().isOk());
		this.mockMvc.perform(get("/incident/" + userName + "/" + this.incidentList.get(2).getId()))
		.andExpect(status().isOk()).andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$.id", is(this.incidentList.get(2).getId().intValue())))
		.andExpect(jsonPath("$.location", is("A location 3")))
		.andExpect(jsonPath("$.exactLocation", is("An exact location 3")))
		.andExpect(jsonPath("$.description", is("A description 3")))
		.andExpect(jsonPath("$.imagePath", is("image dir 3")))
		.andExpect(jsonPath("$.actice", is(true)));
	}
	
	@Test
	public void readArchievedIncidents() throws Exception {
		this.mockMvc.perform(post("/incident/" + userName + "/archieve/" + this.incidentList.get(0).getId()))
				.andExpect(status().isOk());
		this.mockMvc.perform(post("/incident/" + userName + "/archieve/" + this.incidentList.get(2).getId()))
				.andExpect(status().isOk());
		this.mockMvc.perform(get("/incident/" + userName + "/allArchieved")).andExpect(status().isOk())
		.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", hasSize(2)))
		.andExpect(jsonPath("$[0].id", is(this.incidentList.get(0).getId().intValue())))
		.andExpect(jsonPath("$[0].location", is("A location")))
		.andExpect(jsonPath("$[0].exactLocation", is("An exact location")))
		.andExpect(jsonPath("$[0].description", is("A description")))
		.andExpect(jsonPath("$[0].imagePath", is("image dir")))
		.andExpect(jsonPath("$[0].actice", is(false)))
		.andExpect(jsonPath("$[1].id", is(this.incidentList.get(2).getId().intValue())))
		.andExpect(jsonPath("$[1].location", is("A location 3")))
		.andExpect(jsonPath("$[1].exactLocation", is("An exact location 3")))
		.andExpect(jsonPath("$[1].description", is("A description 3")))
		.andExpect(jsonPath("$[1].imagePath", is("image dir 3")))
		.andExpect(jsonPath("$[1].actice", is(false)));;
	}

	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
}