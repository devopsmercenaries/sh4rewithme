package basic

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import com.excilys.ebi.gatling.jdbc.Predef._
import com.excilys.ebi.gatling.http.Headers.Names._
import akka.util.duration._
import bootstrap._

class BasicPerfSimulation extends Simulation {

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

	val headers = Map(
		"Keep-Alive" -> "115")

	val scn = scenario("Minimal Perf Benchmark")
		.repeat(extLoop) {
		exec(
			http("HomePage")
				.get(extWebapp)
				.headers(headers)
				.check(status.is(200)))
		}
		
	setUp(scn.users(extUsers).ramp(extRampup).protocolConfig(httpConf))
}
