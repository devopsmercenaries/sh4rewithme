package me.sh4rewith.web.dialect;


import java.util.HashSet;
import java.util.Set;

import me.sh4rewith.web.dialect.processors.BinarySizeProcessor;
import me.sh4rewith.web.dialect.processors.RelativeDateProcessor;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

public class Sh4reWithMeDialect extends AbstractDialect {

	/**
	 * Default prefix: this is the prefix that will be used for this dialect unless a different one is specified when adding the dialect to the Template Engine.
	 */
	public String getPrefix() {
		return "sh4re";
	}

	/**
	 * Non-lenient: if a tag or attribute with its prefix ('sh4re') appears on the template and there is no valuetag/attribute processor associated with it, an exception is thrown.
	 */
	public boolean isLenient() {
		return false;
	}

	/**
	 * Where attribute processors are declared.
	 */
	@Override
	public Set<IProcessor> getProcessors() {
		final Set<IProcessor> attrProcessors = new HashSet<IProcessor>();
		attrProcessors.add(new BinarySizeProcessor());
		attrProcessors.add(new RelativeDateProcessor());
		return attrProcessors;
	}
}
