package me.sh4rewith.webtests.pages;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@DefaultUrl("http://localhost:9090/sh4rewithme-webapp/upload")
public class UploadPage extends PageObject {

	@FindBy(id = "description")
	private WebElement descriptionField;

	@FindBy(name = "file")
	private WebElement fileField;

	@FindBy(name = "expiration")
	private WebElement expirationField;

	@FindBy(id = "submit_upload")
	private WebElement submitButton;

	public UploadPage(WebDriver driver) {
		super(driver);
	}

	public void enter_description(String description) {
		element(descriptionField).waitUntilPresent();
		typeInto(descriptionField, description);
	}

	public void enter_file(String filename) {
		upload(filename);
	}

	public void enter_expiration(String expirationInMinutes) {
		element(expirationField).selectByValue(expirationInMinutes);
	}

	public void submit_upload() {
		clickOn(submitButton);
	}

}
