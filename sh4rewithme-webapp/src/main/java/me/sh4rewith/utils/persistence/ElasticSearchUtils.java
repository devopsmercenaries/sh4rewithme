package me.sh4rewith.utils.persistence;

import java.util.Date;
import java.util.List;

import me.sh4rewith.persistence.keys.Key;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat;
import org.elasticsearch.search.SearchHit;

public final class ElasticSearchUtils {
	public static final String GLOBAL_INDEX = "sh4rewithme";

	private ElasticSearchUtils() {
	}

	public static String getValueAsString(GetResponse response, Key key) {
		return (String) response.field(key.keyName()).getValue();
	}

	public static String getValueAsString(SearchHit searchHit, Key key) {
		return (String) searchHit.field(key.keyName()).getValue();
	}

	public static Date getValueAsDate(GetResponse response, Key key) {
		Object value = response.field(key.keyName()).getValue();
		return ISODateTimeFormat.dateOptionalTimeParser().parseDateTime((String)value).toDate();
	}

	public static Date getValueAsDate(SearchHit searchHit, Key key) {
		Object value = searchHit.field(key.keyName()).getValue();
		return ISODateTimeFormat.dateOptionalTimeParser().parseDateTime((String)value).toDate();
	}

	public static byte[] getValueAsBytes(GetResponse response, Key key) {
		return (byte[]) response.field(key.keyName()).getValue();
	}

	public static byte[] getValueAsBytes(SearchHit searchHit, Key key) {
		return (byte[]) searchHit.field(key.keyName()).getValue();
	}

	public static Long getValueAsLong(SearchHit searchHit, Key key) {
		return ((Integer) searchHit.field(key.keyName()).getValue()).longValue();
	}

	public static Long getValueAsLong(GetResponse response, Key key) {
		return (Long) response.field(key.keyName()).getValue();
	}

	@SuppressWarnings("unchecked")
	public static List<String> getValueAsListOfStrings(GetResponse response,
			Key key) {
		return (List<String>) response.field(key.keyName()).getValue();
	}

	@SuppressWarnings("unchecked")
	public static List<String> getValueAsListOfStrings(SearchHit searchHit,
			Key key) {
		return (List<String>) searchHit.field(key.keyName()).getValue();
	}
}
