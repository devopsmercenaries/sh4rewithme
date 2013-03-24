package me.sh4rewith.webtests.pages;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@DefaultUrl("http://localhost:9090/sh4rewithme-webapp/user-registration")
public class RegistrationPage extends PageObject {

	@FindBy(name = "id")
	private WebElement useridField;

	@FindBy(name = "email")
	private WebElement emailField;

	@FindBy(name = "firstname")
	private WebElement firstnameField;

	@FindBy(name = "lastname")
	private WebElement lastnameField;

	@FindBy(name = "password")
	private WebElement passwordField;

	@FindBy(name = "confirmedPassword")
	private WebElement confirmedPasswordField;

	@FindBy(id = "form_registration_submit")
	private WebElement submit_button;

	public RegistrationPage(WebDriver driver) {
		super(driver);
	}

	public void enter_userid(String userId) {
		typeInto(useridField, userId);
	}

	public void enter_email(String email) {
		typeInto(emailField, email);
	}

	public void enter_firstname(String firstName) {
		typeInto(firstnameField, firstName);
	}

	public void enter_lastname(String lastName) {
		typeInto(lastnameField, lastName);
	}

	public void enter_password(String password) {
		typeInto(passwordField, password);
	}

	public void enter_confirmed_password(String password) {
		typeInto(confirmedPasswordField, password);
	}

	public void submit_registration() {
		clickOn(submit_button);
	}
}
