package me.sh4rewith.persistence.elasticsearch;

import static me.sh4rewith.persistence.keys.SharedFileInfoKeys.PRIVACY_TYPE;
import static me.sh4rewith.persistence.keys.UserInfoKeys.CREDENTIALS;
import static me.sh4rewith.persistence.keys.UserInfoKeys.EMAIL;
import static me.sh4rewith.persistence.keys.UserInfoKeys.FIRST_NAME;
import static me.sh4rewith.persistence.keys.UserInfoKeys.HASH;
import static me.sh4rewith.persistence.keys.UserInfoKeys.LAST_NAME;
import static me.sh4rewith.persistence.keys.UserInfoKeys.REGISTRATION_STATUS;
import static me.sh4rewith.persistence.keys.UserInfoKeys.USER_INFO_STORENAME;

import java.util.ArrayList;
import java.util.List;

import me.sh4rewith.domain.RegistrationStatus;
import me.sh4rewith.domain.UserInfo;
import me.sh4rewith.persistence.UsersRepository;
import me.sh4rewith.persistence.keys.UserInfoKeys;
import me.sh4rewith.utils.persistence.ElasticSearchUtils;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FieldQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.NotFilterBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("elasticsearch")
public class ElasticSearchUsersRepository implements UsersRepository {

	@Autowired
	private Client esClient;

	@Override
	public void store(UserInfo userInfo) {
		try {
			XContentBuilder source = XContentFactory
					.jsonBuilder()
					.startObject()
					.field(EMAIL.keyName(),
							userInfo.getEmail())
					.field(FIRST_NAME.keyName(),
							userInfo.getFirstname())
					.field(LAST_NAME.keyName(),
							userInfo.getLastname())
					.field(CREDENTIALS.keyName(),
							userInfo.getCredentials())
					.field(REGISTRATION_STATUS.keyName(),
							userInfo.getRegistrationStatus())
					.field(HASH.keyName(),
							userInfo.getUserHash())
					.endObject();
			esClient.prepareIndex("sh4rewithme", USER_INFO_STORENAME.keyName(),
					userInfo.getId())
					.setSource(source)
					.execute().actionGet();
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while storing User information", e);
		}

	}

	@Override
	public UserInfo getUserInfoById(String userId) {
		UserInfo result = null;
		try {
			GetResponse response = esClient
					.prepareGet(
							"sh4rewithme",
							USER_INFO_STORENAME.keyName(),
							userId)
					.setFields(UserInfoKeys.keyNamesArray())
					.execute()
					.actionGet();
			if (response.exists()) {
				result = new UserInfo.Builder(userId)
						.setEmail(
								ElasticSearchUtils
										.getValueAsString(response, EMAIL))
						.setFirstname(
								ElasticSearchUtils.getValueAsString(response,
										FIRST_NAME))
						.setLastname(
								ElasticSearchUtils.getValueAsString(response,
										LAST_NAME))
						.setCredentials(
								ElasticSearchUtils.getValueAsString(response,
										CREDENTIALS))
						.setRegistrationStatus(
								RegistrationStatus.valueOf(
										ElasticSearchUtils.getValueAsString(
												response,
												REGISTRATION_STATUS)))
						.setUserHash(
								ElasticSearchUtils.getValueAsString(response,
										HASH))
						.build();
			}
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while storing User information", e);
		}
		return result;
	}

	@Override
	public List<UserInfo> findAllUserInfoBut(String userId) {
		List<UserInfo> result = new ArrayList<UserInfo>();
		try {
			NotFilterBuilder allButOneFilterBuilder = FilterBuilders.notFilter(
					FilterBuilders.idsFilter(
							USER_INFO_STORENAME.keyName()).ids(
							userId)
					);
			SearchResponse response = esClient
					.prepareSearch("sh4rewithme")
					.setTypes(USER_INFO_STORENAME.keyName())
					.addFields(UserInfoKeys.keyNamesArray())
					.setFilter(allButOneFilterBuilder)
					.execute().actionGet();
			SearchHit[] searchHits = response.getHits().getHits();
			for (SearchHit searchHit : searchHits) {
				UserInfo userInfo = new UserInfo.Builder(searchHit.getId())
						.setEmail(
								ElasticSearchUtils.getValueAsString(searchHit,
										EMAIL))
						.setFirstname(
								ElasticSearchUtils.getValueAsString(searchHit,
										FIRST_NAME))
						.setLastname(
								ElasticSearchUtils.getValueAsString(searchHit,
										LAST_NAME))
						.setCredentials(
								ElasticSearchUtils.getValueAsString(searchHit,
										CREDENTIALS))
						.setRegistrationStatus(
								RegistrationStatus
										.valueOf(ElasticSearchUtils
												.getValueAsString(
														searchHit,
														REGISTRATION_STATUS)))
						.setUserHash(
								ElasticSearchUtils.getValueAsString(searchHit,
										HASH))
						.build();
				result.add(userInfo);
			}
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while storing User information", e);
		}
		return result;
	}

	@Override
	public Long countAll() {
		CountResponse response = esClient.prepareCount("sh4rewithme")
				.setTypes(USER_INFO_STORENAME.keyName())
				.execute()
				.actionGet();
		return response.count();
	}

	@Override
	public UserInfo getUserInfoByHash(String userHash) {
		UserInfo result = null;
		try {
			FieldQueryBuilder byHashQueryBuilder = QueryBuilders.fieldQuery(
					HASH.keyName(),
					userHash);
			SearchResponse response = esClient.prepareSearch("sh4rewithme")
					.setTypes(USER_INFO_STORENAME.keyName())
					.addFields(UserInfoKeys.keyNamesArray())
					.setQuery(byHashQueryBuilder)
					.execute().actionGet();
			SearchHit searchHit = response.getHits().getHits()[0];
			result = new UserInfo.Builder(searchHit.getId())
					.setEmail(
							ElasticSearchUtils.getValueAsString(searchHit,
									EMAIL))
					.setFirstname(
							ElasticSearchUtils.getValueAsString(searchHit,
									FIRST_NAME))
					.setLastname(
							ElasticSearchUtils.getValueAsString(searchHit,
									LAST_NAME))
					.setCredentials(
							ElasticSearchUtils.getValueAsString(searchHit,
									CREDENTIALS))
					.setRegistrationStatus(
							RegistrationStatus.valueOf(ElasticSearchUtils
									.getValueAsString(
											searchHit, REGISTRATION_STATUS)))
					.setUserHash(
							ElasticSearchUtils
									.getValueAsString(searchHit, HASH))
					.build();
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while storing User information", e);
		}
		return result;
	}

	@Override
	public void changeRegistrationStatusByHash(String userInfoHash,
			RegistrationStatus registrationStatus) {
		String id = getUserInfoByHash(userInfoHash).getId();
		try {
			esClient.prepareUpdate()
					.setIndex("sh4rewithme")
					.setType(USER_INFO_STORENAME.keyName())
					.setId(id)
					.setScript("ctx._source."+REGISTRATION_STATUS.keyName()+" = \""+registrationStatus.name()+"\"")
					.execute().actionGet();
		} catch (Throwable e) {
			throw new ElasticSearchException(
					"A problem occured while updating User registration status",
					e);
		}
	}

}
