package me.sh4rewith.test.fixture;

import java.text.MessageFormat;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.messageresolver.MessageResolution;

/**
 * 
 * @since 1.1
 * 
 */
public class TestMessageResolver implements IMessageResolver {

	private final Map<String, String> properties;

	public TestMessageResolver(final Map<String, String> properties) {
		super();
		this.properties = properties;
	}

	public String getName() {
		return "TEST MESSAGE RESOLVER";
	}

	public Integer getOrder() {
		return Integer.valueOf(1);
	}

	public MessageResolution resolveMessage(Arguments arguments, String key, Object[] messageParameters) {

		final String messageValue = this.properties.get(key);
		if (messageValue == null) {
			return null;
		}
		if (messageParameters == null || messageParameters.length == 0) {
			return new MessageResolution(messageValue);
		}

		final MessageFormat messageFormat = new MessageFormat(messageValue, arguments.getContext().getLocale());
		return new MessageResolution(messageFormat.format(messageParameters));

	}

	public void initialize() {
		// Nothing to initialize
	}

}