package me.sh4rewith.web.dialect.processors;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

public class BinarySizeProcessor extends AbstractTextChildModifierAttrProcessor {
	private static String[] SIZE_SUFFIX = { "B", "KB", "MB", "GB", "TB" };

	public BinarySizeProcessor() {
		super("binarySize");
	}

	@Override
	protected String getText(Arguments arguments, Element element, String attributeName) {
		/*
		 * Obtain the attribute value
		 */
		final String attributeValue = element.getAttributeValue(attributeName);
		Object processedExpression = StandardExpressionProcessor.processExpression(arguments, attributeValue);
		Long size = Long.valueOf("" + processedExpression);
		int i = 0;
		// long reste = 0;
		while (size > 1000) {
			i++;
			size = size / 1000;
		}
		final String sizeSuffix = SIZE_SUFFIX[i];
		final String unit = getMessage(arguments, "binary.size.unit." + sizeSuffix, new Object[0]);
		return getMessage(arguments, "binary.size", new Object[] { size, unit });
	}

	@Override
	public int getPrecedence() {
		return 12000;
	}

}
