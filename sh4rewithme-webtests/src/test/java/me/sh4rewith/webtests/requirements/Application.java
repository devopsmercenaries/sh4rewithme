package me.sh4rewith.webtests.requirements;

import net.thucydides.core.annotations.Feature;

public class Application {
    @Feature
    public class LoginAndRegistration {
        public class Login {}
        public class Registration {}
    }
    
    @Feature
    public class FileManagement {
    	public class FileUpload {}
    }
}