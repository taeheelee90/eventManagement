package com.taeheelee.eventmanagement.settings;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taeheelee.eventmanagement.WithAccount;
import com.taeheelee.eventmanagement.Account.AccountRepository;
import com.taeheelee.eventmanagement.Account.AccountService;
import com.taeheelee.eventmanagement.domain.Account;
import com.taeheelee.eventmanagement.domain.Tag;
import com.taeheelee.eventmanagement.settings.form.TagForm;
import com.taeheelee.eventmanagement.tag.TagRepository;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class SettingsControllerTest {

	@Autowired MockMvc mockMvc;
	@Autowired AccountRepository accountRepository;
	@Autowired TagRepository tagRepository;
	@Autowired AccountService accountService;
	@Autowired PasswordEncoder passwordEncoder;
	@Autowired ObjectMapper objectMapper;
	
	@AfterEach
	void afterEach() {
		accountRepository.deleteAll();
	}

	@WithAccount("testUser")
	@DisplayName("Show Edit Profile Form")
	@Test
	void updateProfileForm() throws Exception {
		
		mockMvc.perform(get(SettingController.SETTINGS_PROFILE_URL))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("account"))
        .andExpect(model().attributeExists("profile"));
	}
	
	
	
	@WithAccount("testUser")
	@DisplayName("Edit Profile - Correct")
	@Test
	void updateProfile() throws Exception {
		String bio = "Change bio.";
		mockMvc.perform(post(SettingController.SETTINGS_PROFILE_URL).param("bio", bio).with(csrf()))
				.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl(SettingController.SETTINGS_PROFILE_URL))
				.andExpect(flash().attributeExists("message"));

		Account testUser = accountRepository.findByNickname("testUser");
		assertEquals(bio, testUser.getBio());
	}
	
	@WithAccount("testUser")
	@DisplayName("Edit Profile - Incorrect")
	@Test
	void updateProfile_error() throws Exception {
		String bio = "Change bio should not be too long. It is limited max 35 characters. but this is too long text for bio.";
		mockMvc.perform(post(SettingController.SETTINGS_PROFILE_URL).param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account testUser = accountRepository.findByNickname("testUser");
        assertNull(testUser.getBio());
	}
	
	
	@WithAccount("testUser")
	@DisplayName("Show Edit Password Form")
	@Test
	void updatePasswordForm() throws Exception {
		
		mockMvc.perform(get(SettingController.SETTINGS_PASSWORD_URL))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("account"))
        .andExpect(model().attributeExists("passwordForm"));
	}
	
	@WithAccount("testUser")
	@DisplayName("Edit Password - Correct")
	@Test
	void updatePassword() throws Exception {
		
		mockMvc.perform(post(SettingController.SETTINGS_PASSWORD_URL)
				.param("newPassword", "12345678")
                .param("newPasswordConfirm", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl(SettingController.SETTINGS_PASSWORD_URL))
				.andExpect(flash().attributeExists("message"));

		Account testUser = accountRepository.findByNickname("testUser");
		assertTrue(passwordEncoder.matches("12345678", testUser.getPassword()));
	}
	
	
	@WithAccount("testUser")
	@DisplayName("Edit Password - Incorrect")
	@Test
	void updatePassword_error() throws Exception {
		
		mockMvc.perform(post(SettingController.SETTINGS_PASSWORD_URL)
				.param("newPassword", "12345678")
                .param("newPasswordConfirm", "123456789")
                .with(csrf()))
                .andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("passwordForm"));		
	}
	
	@WithAccount("testUser")
	@DisplayName("Show Edit Notification Form")
	@Test
	void updateNotificationForm() throws Exception {
		
		mockMvc.perform(get(SettingController.SETTINGS_NOTIFICATIONS_URL))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("account"))
        .andExpect(model().attributeExists("notifications"));
	}
	
	
	@WithAccount("testUser")
	@DisplayName("Show Edit Tags Form")
	@Test
	void updateTagForm() throws Exception {
		mockMvc.perform(get(SettingController.SETTINGS_TAGS_URL))
				.andExpect(view().name(SettingController.SETTINGS_TAGS_VIEW_NAME))
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("whiteList"))
				.andExpect(model().attributeExists("tags"));
	}
	
	@WithAccount("testUser")
	@DisplayName("Add new tag to account")
	@Test
	void addTag() throws Exception {
		TagForm tagForm = new TagForm();
        tagForm.setTitle("newTag");	
		
		mockMvc.perform(post(SettingController.SETTINGS_TAGS_URL + "/add")
				.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());
	
		Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        Account account = accountRepository.findByNickname("testUser");
        assertTrue(account.getTags().contains(newTag));
		
	}
	
	
	@WithAccount("testUser")
	@DisplayName("Remove tag from account")
	@Test
	void removeTag() throws Exception {
		Account account = accountRepository.findByNickname("testUser");
		Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
		accountService.addTag(account, newTag);
		
		assertTrue(account.getTags().contains(newTag));
		
		TagForm tagForm = new TagForm();
        tagForm.setTitle("newTag");	
		
		mockMvc.perform(post(SettingController.SETTINGS_TAGS_URL + "/remove")
				.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());
	
		assertFalse(account.getTags().contains(newTag));
		
	}
	
}
