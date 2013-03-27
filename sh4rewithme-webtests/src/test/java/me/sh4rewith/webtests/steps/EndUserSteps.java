package me.sh4rewith.webtests.steps;

import me.sh4rewith.webtests.pages.ErrorPage;
import me.sh4rewith.webtests.pages.LoginPage;
import me.sh4rewith.webtests.pages.RegistrationPage;
import me.sh4rewith.webtests.pages.RegistrationSuccessPage;
import me.sh4rewith.webtests.pages.SharedFilesPage;
import me.sh4rewith.webtests.pages.UploadPage;
import me.sh4rewith.webtests.pages.WelcomePage;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.StepGroup;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

import org.hamcrest.MatcherAssert;

public class EndUserSteps extends ScenarioSteps {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6114737343307328538L;

	public EndUserSteps(Pages pages) {
		super(pages);
	}

	@Step
	public void logs_in_with(String username, String password) {
		LoginPage loginPage = getPages().currentPageAt(LoginPage.class);
		loginPage.open();
		loginPage.enter_username(username);
		loginPage.enter_password(password);
		loginPage.submit_login();
	}

	@Step
	public void should_have_access_to_user_info() {
		WelcomePage welcomePage = getPages().currentPageAt(WelcomePage.class);
		welcomePage.open();
		MatcherAssert.assertThat("User info must be available",
				welcomePage.user_info_is_available());
	}

	@Step
	public void should_still_be_on_login_page() {
		getPages().isCurrentPageAt(LoginPage.class);
	}

	@Step
	public void should_see_an_error_message() {
		LoginPage loginPage = getPages().currentPageAt(LoginPage.class);
		loginPage.login_error_field_displayed();
	}

	@Step
	public void registersAs(String userId, String email, String firstName,
			String lastName, String password) {
		RegistrationPage registrationPage = getPages().currentPageAt(
				RegistrationPage.class);
		registrationPage.open();
		registrationPage.enter_userid(userId);
		registrationPage.enter_email(email);
		registrationPage.enter_firstname(firstName);
		registrationPage.enter_lastname(lastName);
		registrationPage.enter_password(password);
		registrationPage.enter_confirmed_password(password);
		registrationPage.submit_registration();
	}

	@Step
	public void should_land_on_registration_success() {
		getPages().isCurrentPageAt(RegistrationSuccessPage.class);
	}

	@StepGroup
	public void should_not_be_able_to_login(String userId, String password) {
		logs_in_with(userId, password);
		should_still_be_on_login_page();
		should_see_an_error_message();
	}

	@StepGroup
	public void should_be_able_to_login(String userId, String password) {
		logs_in_with(userId, password);
		should_have_access_to_user_info();
	}

	@Step
	public void clicks_on_confirmation_link() {
		RegistrationSuccessPage registrationSuccessPage = getPages()
				.currentPageAt(RegistrationSuccessPage.class);
		registrationSuccessPage.click_on_confirmation_link();
	}

	@Step
	public void upload_file(String description, String filename,
			String expirationInMinutes) {
		UploadPage uploadPage = getPages().currentPageAt(UploadPage.class);
		uploadPage.open();
		uploadPage.enter_description(description);
		uploadPage.enter_file(filename);
		uploadPage.enter_expiration(expirationInMinutes);
		uploadPage.submit_upload();
	}

	@Step
	public void has_file_in_his_private_stream(String description) {
		SharedFilesPage sharedFilesPage = getPages().currentPageAt(SharedFilesPage.class);
		sharedFilesPage.open();
		sharedFilesPage.has_file_in_private_stream(description);
	}

	@Step
	public void downloads_file_from_his_private_stream(String description) {
		SharedFilesPage sharedFilesPage = getPages().currentPageAt(SharedFilesPage.class);
		sharedFilesPage.open();
		sharedFilesPage.downloads_file_from_private_stream(description);
		should_not_land_on_error_page(description);
	}

	@Step
	private void should_not_land_on_error_page(String description) {
		getPages().isCurrentPageAt(ErrorPage.class);
	}

}
