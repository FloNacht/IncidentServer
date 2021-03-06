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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = WebserviceApplication.class)
@WebAppConfiguration
public class UserRestControllerTest {

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;

	private String userName = "bdussault";

	private String password = "abcd1234";

	private HttpMessageConverter mappingJackson2HttpMessageConverter;

	private User user;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private IncidentRepository incidentRepository;

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

		this.user = userRepository.saveAndFlush(new User(userName, password));
	}

	@Test
	public void login() throws Exception {
		mockMvc.perform(get("/login/" + userName + "/" + password)).andExpect(status().isAccepted());
	}

	@Test
	public void loginWithParam() throws Exception {
		mockMvc.perform(get("/login").param("username", userName).param("password", password))
				.andExpect(status().isAccepted());
	}

	@Test
	public void getRole() throws Exception {
		this.mockMvc.perform(get("/user/" + userName + "/role")).andExpect(status().isOk())
				.andExpect(content().contentType(contentType)).andExpect(content().string("\"STUDENT\""));
	}

	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
}