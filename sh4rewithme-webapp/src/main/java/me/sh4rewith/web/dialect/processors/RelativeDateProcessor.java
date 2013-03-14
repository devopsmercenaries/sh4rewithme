package me.sh4rewith.web.dialect.processors;

import java.util.Date;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import com.ocpsoft.pretty.time.PrettyTime;

public class RelativeDateProcessor extends AbstractTextChildModifierAttrProcessor {

    public RelativeDateProcessor() {
        super("relativeDate");
    }

    @Override
    protected String getText(Arguments arguments, Element element, String attributeName) {
        /*
           * Obtain the attribute value
           */
        final String attributeValue = element.getAttributeValue(attributeName);
        Object processedExpression = StandardExpressionProcessor.processExpression(arguments, attributeValue);

        if (processedExpression instanceof Date) {
            PrettyTime p = new PrettyTime();
            return p.format((Date) processedExpression);

        }


        return "TEST";
    }

    @Override
    public int getPrecedence() {
        return 12000;
    }

}
