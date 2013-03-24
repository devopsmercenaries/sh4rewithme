package me.sh4rewith.webtests;

import java.util.UUID;

import me.sh4rewith.webtests.requirements.Application;
import me.sh4rewith.webtests.steps.EndUserSteps;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.ManagedPages;
import net.thucydides.core.annotations.Steps;
import net.thucydides.core.annotations.Story;
import net.thucydides.core.pages.Pages;
import net.thucydides.junit.runners.ThucydidesRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

@Story(Application.LoginAndRegistration.Registration.class)
@RunWith(ThucydidesRunner.class)
public class RegistrationStoryTest {

	private static final String USER_ID_PREFIX = "hades";

	@Managed(uniqueSession = true)
	public WebDriver webdriver;

	@ManagedPages(defaultUrl = "http://localhost:9090/")
	public Pages pages;

	@Steps
	public EndUserSteps endUser;

	@Test
	public void registration_should_not_enable_login_without_confirmation() {
		String userId = (USER_ID_PREFIX + UUID.randomUUID()).substring(0, 20);
		endUser.registersAs(userId, "hades@sh4rewith.me", "Roger", "Hades",
				userId);
		endUser.should_land_on_registration_success();
		endUser.should_not_be_able_to_login(userId, userId);
	}

	@Test
	public void registration_should_enable_login_with_confirmation() {
		String userId = (USER_ID_PREFIX + UUID.randomUUID()).substring(0, 20);
		endUser.registersAs(userId, "hades@sh4rewith.me", "Roger",
				"Hades",
				userId);
		endUser.should_land_on_registration_success();
		// Registration link is sent by email in production, though available in
		// local and qual.
		endUser.clicks_on_confirmation_link();
		endUser.should_be_able_to_login(userId, userId);
	}

}