package me.sh4rewith.webtests.pages;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@DefaultUrl("http://localhost:9090/sh4rewithme-webapp/welcome")
public class WelcomePage extends PageObject {

	@FindBy(id = "userInfoMenu")
	private WebElement userInfoMenu;

	public WelcomePage(WebDriver driver) {
		super(driver);
	}

	public Boolean user_info_is_available() {
		return userInfoMenu.getAttribute("href").contains(
				"./user-info");
	}
}
