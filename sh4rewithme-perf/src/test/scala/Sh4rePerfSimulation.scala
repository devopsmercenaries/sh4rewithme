
import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import com.excilys.ebi.gatling.jdbc.Predef._
import com.excilys.ebi.gatling.http.Headers.Names._
import akka.util.duration._
import bootstrap._
import assertions._

class Sh4rePerfSimulation extends Simulation {

	val extHost = Option(System.getProperty("extHost")).getOrElse("localhost")
	val extPort = Integer.getInteger("extPort", 9090)
	val extUsers = Integer.getInteger("extUsers", 1)
	val extRampup = java.lang.Long.getLong("extRampup", 0L)
	val extPause = java.lang.Long.getLong("extPause", 1L)
	val extLoop = Integer.getInteger("extLoop", 100)

	val extBaseUrl = if (extPort == 443)
		"https://" + extHost
	else if (extPort != 80)
		"http://" + extHost + ":" + extPort
	else
		"http://" + extHost

	val extWebapp = Option(System.getProperty("extWebapp")).getOrElse("/sh4rewithme-webapp/")

	val httpConf = httpConfig
		.baseURL(extBaseUrl)
		.acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.7")
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
		.disableFollowRedirect

	val headers = Map("Keep-Alive" -> "115")

	val userFeeder = new Feeder[String] {
	  // always return true as this feeder can be polled infinitively
	  override def hasNext = true
	
	  override def next: Map[String, String] = {
	    val username = "test_" + scala.math.abs(java.util.UUID.randomUUID.getMostSignificantBits)
	    Map("username" -> username)
	    }
	}
	
	val scnLoadFile = scenario("Scenario Load Files")
		.repeat(extLoop) {
			exec(http("request_login")
						.post("/sh4rewithme-webapp/j_spring_security_check")
						.param("j_username", "gatling")
						.param("j_password", "gatling")
						.headers(headers)
						.check(status.is(302))
				)
			.pause(10 milliseconds)
			.exec(http("request_upload_page")
						.get("/sh4rewithme-webapp/upload")
						.headers(headers)
						.check(status.is(200))
				)
			.exec(http("request_upload")
						.post("/sh4rewithme-webapp/upload")
	  					.param("description", "upload" + scala.math.abs(java.util.UUID.randomUUID.getMostSignificantBits))
	  					.param("expiration", "1" )
	  					.upload("file", "myAttachment.txt")
	  					.headers(headers)
						.check(status.is(302))
				)
			.pause(10 milliseconds)
			.exec(http("request_shared-files")
						.get("/sh4rewithme-webapp/shared-files")
						.headers(headers)
						.check(status.is(200))
						//Check uploaded file exists
				)
		}
	
	val scnNewUser = scenario("NewUser")
		.repeat(extLoop) {
			exec(http("request_home")
				.get(extWebapp)
				.headers(headers)
				)
     	                .pause(100 milliseconds)
                        .feed(userFeeder)
			.exec(http("request_reg")
						.get(extWebapp+"/user-registration")
						.headers(headers)
				)
     	                .pause(100 milliseconds)
			.exec(http("request_new")
						.post(extWebapp+"/user-registration")
						.param("id", "${username}")
						.param("email", "${username}@localhost.fr")
						.param("firstname", "${username}")
						.param("lastname", "${username}")
						.param("password", "${username}")
						.param("confirmedPassword", "${username}")
						.headers(headers)
						.check(status.is(200))
				)
		}

	val scnHomePage = scenario("Home Page")
		.repeat(extLoop) {
		exec(
			http("HomePage")
				.get(extWebapp)
				.headers(headers)
				.check(status.is(200)))
		}
		

	//setUp(scnHomePage.users(extUsers).protocolConfig(httpConf))
	setUp(scnNewUser.users(extUsers).protocolConfig(httpConf))
	//setUp(scnLoadFile.users(extUsers).protocolConfig(httpConf))
}
