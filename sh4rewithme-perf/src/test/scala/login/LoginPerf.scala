package me.sh4rewith.perf.login 
import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import com.excilys.ebi.gatling.jdbc.Predef._
import com.excilys.ebi.gatling.http.Headers.Names._
import akka.util.duration._
import bootstrap._
import assertions._

class RecordedSimulation extends Simulation {

	val httpConf = httpConfig
			.baseURL("http://localhost")
			.proxy("localhost", 9090).httpsPort(9091)
			.acceptHeader("*/*")
			.acceptEncodingHeader("gzip, deflate")
			.acceptLanguageHeader("en-US,en;q=0.5")
			.connection("keep-alive")
			.userAgentHeader("Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:18.0) Gecko/20100101 Firefox/18.0")


	val headers_1 = Map(
			"Accept" -> """text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"""
	)

	val headers_2 = Map(
			"Accept" -> """application/json"""
	)


	val scn = scenario("Scenario Name")
		.exec(http("request_1")
					.get("/sh4rewithme-webapp")
					.headers(headers_1)
			)
		.exec(http("request_20")
					.get("/sh4rewithme-webapp/login")
					.headers(headers_1)
			)
		.exec(http("request_23")
					.post("/sh4rewithme-webapp/j_spring_security_check")
 		            .param("j_username", "zeus")
		            .param("j_password", "zeus")
					.headers(headers_1)
		            .check(regex("<title>Home page</title>"))
			)
		.pause(4)
		.exec(http("request_24")
					.get("/sh4rewithme-webapp/shared-files")
					.headers(headers_1)
			)
		.pause(162 milliseconds)
		.exec(http("request_26")
					.get("/sh4rewithme-webapp/buddies")
					.headers(headers_2)
			)
		.pause(3)
		.exec(http("request_27")
					.get("/sh4rewithme-webapp/upload")
					.headers(headers_1)
			)
		.pause(43 milliseconds)
		.exec(http("request_31")
					.get("/sh4rewithme-webapp/user-info")
					.headers(headers_1)
			)

	setUp(scn.users(1).protocolConfig(httpConf))
}
