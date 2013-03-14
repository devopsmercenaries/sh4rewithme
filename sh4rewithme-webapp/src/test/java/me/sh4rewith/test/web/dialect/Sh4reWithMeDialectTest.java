package me.sh4rewith.test.web.dialect;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import junit.framework.Assert;

import me.sh4rewith.test.fixture.StringTemplateResolver;
import me.sh4rewith.test.fixture.TestMessageResolver;
import me.sh4rewith.web.dialect.Sh4reWithMeDialect;

import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class Sh4reWithMeDialectTest {

	@Test
	public void test() {
		final Context ctx = new Context(Locale.getDefault());
		ctx.setVariable("binarySize", new Long(123456789));
		Map<String, String> messages = new HashMap<String, String>();
		messages.put("binary.size", "({0} {1})");
		messages.put("binary.size.unit.B", "Bytes");
		messages.put("binary.size.unit.KB", "KiloBytes");
		messages.put("binary.size.unit.MB", "MegaBytes");
		messages.put("binary.size.unit.GB", "GigaBytes");
		messages.put("binary.size.unit.TB", "TeraBytes");
		TemplateEngine engine = new TemplateEngine();
		engine.setMessageResolver(new TestMessageResolver(messages));
		engine.addDialect(new Sh4reWithMeDialect());
		engine.setTemplateResolver(new StringTemplateResolver(""
				/* 1 */+ "<http>" + "\n"
				/* 2 */+ "<head></head>" + "\n"
				/* 3 */+ "<body>" + "\n"
				/* 3 */+ "<div sh4re:binarySize=\"${binarySize}\"/>" + "\n"
				/* 5 */+ "</body>" + "\n"
				/* 6 */+ "</http>"
				));
		final String result = engine.process("sh4rewithmeDialectExample.html", ctx);
		Assert.assertEquals(""
				/* 1 */+ "<http>" + "\n"
				/* 2 */+ "<head></head>" + "\n"
				/* 3 */+ "<body>" + "\n"
				/* 3 */+ "<div>(123 MegaBytes)</div>" + "\n"
				/* 5 */+ "</body>" + "\n"
				/* 6 */+ "</http>", result);
	}

	@Test
	public void testRelativeDate() {

		Locale.setDefault(Locale.ENGLISH);

		final Context ctx = new Context(Locale.getDefault());
		ctx.setVariable("relativeDate", new Date());
		// Map<String, String> messages = new HashMap<String, String>();

		TemplateEngine engine = new TemplateEngine();
		engine.addDialect(new Sh4reWithMeDialect());
		engine.setTemplateResolver(new StringTemplateResolver(""
				/* 1 */+ "<http>" + "\n"
				/* 2 */+ "<head></head>" + "\n"
				/* 3 */+ "<body>" + "\n"
				/* 3 */+ "<div sh4re:relativeDate=\"${relativeDate}\"/>" + "\n"
				/* 5 */+ "</body>" + "\n"
				/* 6 */+ "</http>"
				));
		final String result = engine.process("sh4rewithmeloadDialectExample.html", ctx);
		Assert.assertEquals(""
				/* 1 */+ "<http>" + "\n"
				/* 2 */+ "<head></head>" + "\n"
				/* 3 */+ "<body>" + "\n"
				/* 3 */+ "<div>moments ago</div>" + "\n"
				/* 5 */+ "</body>" + "\n"
				/* 6 */+ "</http>", result);
	}

}
