package me.sh4rewith.webtests.steps;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import me.sh4rewith.webtests.pages.LoginPage;
import me.sh4rewith.webtests.pages.WelcomePage;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

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

	public void should_have_access_to_user_info() {
		WelcomePage welcomePage = getPages().currentPageAt(WelcomePage.class);
		welcomePage.user_info_is_available();
	}

	public void should_still_be_on_login_page() {
		String currentUrl = getDriver().getCurrentUrl();
		MatcherAssert.assertThat(currentUrl,
				Matchers.endsWith("/login"));
	}

	public void should_see_an_error_message() {
		
	}
}