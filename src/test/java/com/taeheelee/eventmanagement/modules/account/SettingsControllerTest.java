package com.taeheelee.eventmanagement.modules.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taeheelee.eventmanagement.infra.MockMvcForTest;
import com.taeheelee.eventmanagement.modules.account.form.TagForm;
import com.taeheelee.eventmanagement.modules.account.form.ZoneForm;
import com.taeheelee.eventmanagement.modules.tag.Tag;
import com.taeheelee.eventmanagement.modules.tag.TagRepository;
import com.taeheelee.eventmanagement.modules.zone.Zone;
import com.taeheelee.eventmanagement.modules.zone.ZoneRepository;

@MockMvcForTest
public class SettingsControllerTest {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	AccountRepository accountRepository;
	@Autowired
	TagRepository tagRepository;
	@Autowired
	ZoneRepository zoneRepository;
	@Autowired
	AccountService accountService;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	ObjectMapper objectMapper;

	private Zone testZone = Zone.builder().city("testC").province("testP").build();

	@BeforeEach
	void beforeEach() {
		zoneRepository.save(testZone);
	}

	@AfterEach
	void afterEach() {
		accountRepository.deleteAll();
		zoneRepository.deleteAll();
	}

	@WithAccount("testUser")
	@DisplayName("Profie edit view test")
	@Test
	void show_updateProfileForm() throws Exception {

		mockMvc.perform(get(SettingController.SETTINGS_PROFILE_URL)).andExpect(status().isOk())
				.andExpect(model().attributeExists("account")).andExpect(model().attributeExists("profile"));
	}

	@WithAccount("testUser")
	@DisplayName("Edit profile - success")
	@Test
	void updateProfile_success() throws Exception {
		String bio = "Change bio.";
		mockMvc.perform(post(SettingController.SETTINGS_PROFILE_URL).param("bio", bio).with(csrf()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl(SettingController.SETTINGS_PROFILE_URL))
				.andExpect(flash().attributeExists("message"));

		Account testUser = accountRepository.findByNickname("testUser");
		assertEquals(bio, testUser.getBio());
	}

	@WithAccount("testUser")
	@DisplayName("Edit profile - fail")
	@Test
	void updateProfile_fail() throws Exception {
		String bio = "Change bio should not be too long. It is limited max 35 characters. but this is too long text for bio.";
		mockMvc.perform(post(SettingController.SETTINGS_PROFILE_URL).param("bio", bio).with(csrf()))
				.andExpect(status().isOk()).andExpect(view().name(SettingController.SETTINGS_PROFILE_VIEW_NAME))
				.andExpect(model().attributeExists("account")).andExpect(model().attributeExists("profile"))
				.andExpect(model().hasErrors());

		Account testUser = accountRepository.findByNickname("testUser");
		assertNull(testUser.getBio());
	}

	@WithAccount("testUser")
	@DisplayName("Nickname edit view test")
	@Test
	void show_accountForm() throws Exception {
		mockMvc.perform(get(SettingController.SETTINGS_ACCOUNT_URL)).andExpect(status().isOk())
				.andExpect(model().attributeExists("account")).andExpect(model().attributeExists("nicknameForm"));
	}

	@WithAccount("testUser")
	@DisplayName("Edit nickname - success")
	@Test
	void updateNickname_success() throws Exception {

		mockMvc.perform(post(SettingController.SETTINGS_ACCOUNT_URL)
				.param("nickname", "testNickname")
				.with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(SettingController.SETTINGS_ACCOUNT_URL))
				.andExpect(flash().attributeExists("message"));

		assertNotNull(accountRepository.findByNickname("testNickname"));
	}
	
	@WithAccount("testUser")
	@DisplayName("Edit nickname - fail")
	@Test
	void updateNickname_fail() throws Exception {

		mockMvc.perform(post(SettingController.SETTINGS_ACCOUNT_URL)
				.param("nickname", "")
				.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(view().name(SettingController.SETTINGS_ACCOUNT_VIEW_NAME))
				.andExpect(model().hasErrors())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("nicknameForm"));
	}
	
	
	
	@WithAccount("testUser")
	@DisplayName("Password edit view test")
	@Test
	void show_updatePasswordForm() throws Exception {

		mockMvc.perform(get(SettingController.SETTINGS_PASSWORD_URL)).andExpect(status().isOk())
				.andExpect(model().attributeExists("account")).andExpect(model().attributeExists("passwordForm"));
	}

	@WithAccount("testUser")
	@DisplayName("Edit password - success")
	@Test
	void updatePassword_success() throws Exception {

		mockMvc.perform(post(SettingController.SETTINGS_PASSWORD_URL).param("newPassword", "12345678")
				.param("newPasswordConfirm", "12345678").with(csrf())).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(SettingController.SETTINGS_PASSWORD_URL))
				.andExpect(flash().attributeExists("message"));

		Account testUser = accountRepository.findByNickname("testUser");
		assertTrue(passwordEncoder.matches("12345678", testUser.getPassword()));
	}

	@WithAccount("testUser")
	@DisplayName("Edit password - fail")
	@Test
	void updatePassword_fail() throws Exception {

		mockMvc.perform(post(SettingController.SETTINGS_PASSWORD_URL).param("newPassword", "12345678")
				.param("newPasswordConfirm", "123456789").with(csrf())).andExpect(status().isOk())
				.andExpect(view().name(SettingController.SETTINGS_PASSWORD_VIEW_NAME)).andExpect(model().hasErrors())
				.andExpect(model().attributeExists("account")).andExpect(model().attributeExists("passwordForm"));
	}

	@WithAccount("testUser")
	@DisplayName("Tag edit view test")
	@Test
	void show_tagForm() throws Exception {
		mockMvc.perform(get(SettingController.SETTINGS_TAGS_URL))
				.andExpect(view().name(SettingController.SETTINGS_TAGS_VIEW_NAME))
				.andExpect(model().attributeExists("account")).andExpect(model().attributeExists("whiteList"))
				.andExpect(model().attributeExists("tags"));
	}

	@WithAccount("testUser")
	@DisplayName("Add tag")
	@Test
	void addTag() throws Exception {
		TagForm tagForm = new TagForm();
		tagForm.setTitle("newTag");

		mockMvc.perform(post(SettingController.SETTINGS_TAGS_URL + "/add").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(tagForm)).with(csrf())).andExpect(status().isOk());

		Tag newTag = tagRepository.findByTitle("newTag");
		assertNotNull(newTag);
		Account account = accountRepository.findByNickname("testUser");
		assertTrue(account.getTags().contains(newTag));

	}

	@WithAccount("testUser")
	@DisplayName("Remove tag")
	@Test
	void removeTag() throws Exception {
		Account account = accountRepository.findByNickname("testUser");
		Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
		accountService.addTag(account, newTag);

		assertTrue(account.getTags().contains(newTag));

		TagForm tagForm = new TagForm();
		tagForm.setTitle("newTag");

		mockMvc.perform(post(SettingController.SETTINGS_TAGS_URL + "/remove").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(tagForm)).with(csrf())).andExpect(status().isOk());

		assertFalse(account.getTags().contains(newTag));

	}

	@WithAccount("testUser")
	@DisplayName("Zone edit view test")
	@Test
	void show_zoneForm() throws Exception {
		mockMvc.perform(get(SettingController.SETTINGS_ZONES_URL))
				.andExpect(view().name(SettingController.SETTINGS_ZONES_VIEW_NAME))
				.andExpect(model().attributeExists("account")).andExpect(model().attributeExists("whiteList"))
				.andExpect(model().attributeExists("zones"));
	}

	@WithAccount("testUser")
	@DisplayName("Add new zone")
	@Test
	void addZone() throws Exception {
		ZoneForm zoneForm = new ZoneForm();
		zoneForm.setZoneName(testZone.toString());

		mockMvc.perform(post(SettingController.SETTINGS_ZONES_URL + "/add").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(zoneForm)).with(csrf())).andExpect(status().isOk());

		Account account = accountRepository.findByNickname("testUser");
		Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
		assertTrue(account.getZones().contains(zone));

	}

	@WithAccount("testUser")
	@DisplayName("Remove zone")
	@Test
	void removeZone() throws Exception {
		Account account = accountRepository.findByNickname("testUser");
		Zone zone = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
		accountService.addZone(account, zone);

		ZoneForm zoneForm = new ZoneForm();
		zoneForm.setZoneName(testZone.toString());

		mockMvc.perform(post(SettingController.SETTINGS_ZONES_URL + "/remove").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(zoneForm)).with(csrf())).andExpect(status().isOk());

		assertFalse(account.getZones().contains(zone));

	}
}
