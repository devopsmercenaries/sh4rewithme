package me.sh4rewith.webtests.pages;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.pages.WebElementFacade;

import org.hamcrest.MatcherAssert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@DefaultUrl("http://localhost:9090/sh4rewithme-webapp/shared-files")
public class SharedFilesPage extends PageObject {

	public SharedFilesPage(WebDriver driver) {
		super(driver);
	}

	public void has_file_in_private_stream(String description) {
		WebElementFacade element = element(By.linkText(description));
		MatcherAssert.assertThat("'" + description + "' is in private stream",
				element != null && element.isVisible()
						&& element.containsText(description));
	}

}
