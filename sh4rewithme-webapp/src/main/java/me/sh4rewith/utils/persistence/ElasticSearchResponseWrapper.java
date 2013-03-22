package me.sh4rewith.utils.persistence;

import java.util.Date;
import java.util.List;

import me.sh4rewith.persistence.keys.Key;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.search.SearchHit;

public class ElasticSearchResponseWrapper {

	private SearchHit searchHit;
	private GetResponse getResponse;

	public ElasticSearchResponseWrapper(SearchHit searchHit) {
		this.searchHit = searchHit;
	}

	public ElasticSearchResponseWrapper(GetResponse getResponse) {
		this.getResponse = getResponse;
	}

	public String getId() {
		String result = null;
		if (searchHit != null) {
			result = searchHit.getId();
		} else if (getResponse != null) {
			result = getResponse.getId();
		}
		return result;
	}

	public String getStringField(Key key) {
		String result = null;
		if (searchHit != null) {
			result = ElasticSearchUtils.getValueAsString(searchHit, key);
		} else if (getResponse != null) {
			result = ElasticSearchUtils.getValueAsString(getResponse, key);
		}
		return result;
	}

	public Date getDateField(Key key) {
		Date result = null;
		if (searchHit != null) {
			result = ElasticSearchUtils.getValueAsDate(searchHit, key);
		} else if (getResponse != null) {
			result = ElasticSearchUtils.getValueAsDate(getResponse, key);
		}
		return result;
	}

	public List<String> getListOfStringsField(Key key) {
		List<String> result = null;
		if (searchHit != null) {
			result = ElasticSearchUtils.getValueAsListOfStrings(searchHit, key);
		} else if (getResponse != null) {
			result = ElasticSearchUtils.getValueAsListOfStrings(getResponse,
					key);
		}
		return result;
	}

	public Long getLongField(Key key) {
		Long result = null;
		if (searchHit != null) {
			result = ElasticSearchUtils.getValueAsLong(searchHit, key);
		} else if (getResponse != null) {
			result = ElasticSearchUtils.getValueAsLong(getResponse, key);
		}
		return result;
	}

}
