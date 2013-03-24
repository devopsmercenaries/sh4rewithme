package me.sh4rewith.webtests;

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

@Story(Application.LoginAndRegistration.Login.class)
@RunWith(ThucydidesRunner.class)
public class LoginStoryTest {

	@Managed(uniqueSession = true)
	public WebDriver webdriver;

	@ManagedPages(defaultUrl = "http://localhost:9090/")
	public Pages pages;

	@Steps
	public EndUserSteps endUser;

	@Test
	public void login_with_valid_user_name_and_password_should_bring_user_to_the_welcome_page_and_give_access_to_user_info() {
		endUser.logs_in_with("zeus", "zeus");
		endUser.should_have_access_to_user_info();
	}

	@Test
	public void login_with_invalid_user_name_or_password_should_bring_back_to_login_silently() {
		endUser.logs_in_with("zeus", "zeus2");
		endUser.should_still_be_on_login_page();
		endUser.should_see_an_error_message();
	}

}