package me.sh4rewith.webtests.pages;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@DefaultUrl("http://localhost:9090/sh4rewithme-webapp/login")
public class LoginPage extends PageObject {

	@FindBy(name = "j_username")
	private WebElement usernameField;

	@FindBy(name = "j_password")
	private WebElement passwordField;

	@FindBy(id = "submit_login")
	private WebElement submitButton;

	@FindBy(id = "loginError")
	private WebElement loginError;

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public void enter_username(String username) {
		typeInto(usernameField, username);
	}

	public void enter_password(String password) {
		typeInto(passwordField, password);
	}

	public void submit_login() {
		clickOn(submitButton);
	}

	public void login_error_field_displayed() {
		loginError.isDisplayed();
	}

}
