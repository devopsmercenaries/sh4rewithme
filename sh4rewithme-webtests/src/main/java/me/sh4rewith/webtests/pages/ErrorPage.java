package me.sh4rewith.webtests.pages;

import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.pages.PageObject;

import org.openqa.selenium.WebDriver;

@DefaultUrl("http://localhost:9090/sh4rewithme-webapp/blameUs")
public class ErrorPage extends PageObject {

	public ErrorPage(WebDriver driver) {
		super(driver);
	}
}