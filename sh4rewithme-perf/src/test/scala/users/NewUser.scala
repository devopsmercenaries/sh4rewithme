package users

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import com.excilys.ebi.gatling.jdbc.Predef._
import com.excilys.ebi.gatling.http.Headers.Names._
import akka.util.duration._
import bootstrap._
import assertions._

class NewUser extends Simulation {

	val extHost = Option(System.getProperty("extHost")).getOrElse("localhost")
	val extPort = Integer.getInteger("extPort", 9090)
	val extUsers = Integer.getInteger("extUsers", 1)
	val extRampup = Integer.getInteger("extRampup", 0).toLong
	val extPause = Integer.getInteger("extPause", 1).toLong
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

	val headers = Map(
		"Keep-Alive" -> "115")

	val userFeeder = new Feeder[String] {
	  // always return true as this feeder can be polled infinitively
	  override def hasNext = true
	
	  override def next: Map[String, String] = {
	    val username = "test_" + scala.math.abs(java.util.UUID.randomUUID.getMostSignificantBits)
	    Map("username" -> username)
	    }
	}
	
	val scn = scenario("NewUser")
		.repeat(extLoop) {
			exec(http("request_home")
						.get(extWebapp)
						.headers(headers)
				)
     	    .feed(userFeeder)
			.exec(http("request_reg")
						.get(extWebapp+"/user-registration")
						.headers(headers)
				)
			.exec(http("request_new")
						.post(extWebapp+"/user-registration")
						.param("id", "${username}")
						.param("email", "${username}@localhost.fr")
						.param("firstname", "${username}")
						.param("lastname", "${username}")
						.param("password", "${username}")
						.param("confirmedPassword", "${username}")
						.check(status.is(200))
				)
		}

	setUp(scn.users(1).protocolConfig(httpConf))
}