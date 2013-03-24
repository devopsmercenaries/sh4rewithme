package me.sh4rewith.webtests.pages;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@DefaultUrl("http://localhost:9090/sh4rewithme-webapp/user-registration-success")
public class RegistrationSuccessPage extends PageObject {

	@FindBy(id = "confirmation_link")
	private WebElement confirmationLink;

	public RegistrationSuccessPage(WebDriver driver) {
		super(driver);
	}

	public void click_on_confirmation_link() {
		clickOn(confirmationLink);
	}

}
