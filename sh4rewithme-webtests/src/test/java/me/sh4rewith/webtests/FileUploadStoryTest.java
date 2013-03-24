package me.sh4rewith.webtests;

import java.util.UUID;

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

@Story(Application.FileManagement.FileUpload.class)
@RunWith(ThucydidesRunner.class)
public class FileUploadStoryTest {

	@Managed(uniqueSession = true)
	public WebDriver webdriver;

	@ManagedPages(defaultUrl = "http://localhost:9090/")
	public Pages pages;

	@Steps
	public EndUserSteps endUser;

	@Test
	public void file_upload_through_regular_form_should_appear_in_the_private_stream() {
		endUser.logs_in_with("zeus", "zeus");
		String description = "Fichier texte de test" + UUID.randomUUID();
		endUser.upload_file(description, "hello.txt", "1");
		endUser.has_file_in_his_private_stream(description);
	}

}