package me.sh4rewith.config.jmx;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.io.output.WriterOutputStream;
import org.codehaus.jackson.map.ObjectMapper;

public class Stat implements Serializable {
	private static final long serialVersionUID = 3832172162689202705L;

	private Long value;
	private String label;

	public Stat(String label, Long value) {
		this.label = label;
		this.value = value;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		String result = "{\"error\":\"Impossible to Serialize\"}";
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
			objectMapper.writeValue(new WriterOutputStream(stringBuilderWriter), this);
			result = stringBuilderWriter.getBuilder().toString();
		} catch (IOException e) {
			// Impossible to serialize
		}
		return result;
	}
}
