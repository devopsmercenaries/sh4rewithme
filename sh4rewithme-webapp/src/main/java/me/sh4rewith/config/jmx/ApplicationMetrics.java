package me.sh4rewith.config.jmx;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import me.sh4rewith.persistence.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@Component
@ManagedResource(objectName = "sh4rewith.me:name=ApplicationMetrics", description = "Sh4reWith.me Application Metrics", log = true, logFile = "jmx.log", currencyTimeLimit = 15, persistPolicy = "OnUpdate", persistPeriod = 200, persistLocation = "sh4reWith.me", persistName = "metrics")
public class ApplicationMetrics {

	@Autowired
	private Repositories repositories;

	private AtomicReference<Stat> numberOfRegisteredUsers = new AtomicReference<Stat>();
	private AtomicReference<Stat> numberOfSharedFiles = new AtomicReference<Stat>();
	private AtomicReference<Long> updateRateTimeMillis = new AtomicReference<Long>();
	private AtomicReference<Date> lastUpdate = new AtomicReference<Date>();

	@ManagedAttribute(description = "Update Rate in Milliseconds")
	public Long getUpdateRateTimeMillis() {
		return updateRateTimeMillis.get();
	}

	@ManagedAttribute(description = "Update Rate in Milliseconds")
	public void setUpdateRateTimeMillis(Long updateRateTimeMillis) {
		this.updateRateTimeMillis.lazySet(updateRateTimeMillis);
	}

	@ManagedAttribute(description = "Last Update time")
	public Date getLastUpdate() {
		return lastUpdate.get();
	}

	@ManagedAttribute(description = "Last Update time")
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate.lazySet(lastUpdate);
	}

	@ManagedAttribute(description = "Number of Registered Users")
	public String getNumberOfRegisteredUsers() {
		getStats();
		return numberOfRegisteredUsers.get().toString();
	}

	@ManagedAttribute(description = "Number of Shared Files")
	public String getNumberOfSharedFiles() {
		getStats();
		return numberOfSharedFiles.get().toString();
	}

	private synchronized void getStats() {
		Date date = getCurrentDateOffsetByUpdateRate();
		// if lastUpdate time is inferior to current time - updateRate
		// Then update the stat from source
		if (lastUpdate.get().compareTo(date) < 0) {
			numberOfRegisteredUsers.set(new Stat("NbSharedFiles", repositories
					.sharedFilesRepository().countAll()));
			numberOfSharedFiles.set(new Stat("NbRegisteredUsers", repositories
					.usersRepository().countAll()));
			lastUpdate.set(new Date());
		}
	}

	private Date getCurrentDateOffsetByUpdateRate() {
		Date date = new Date(new Date().getTime() - updateRateTimeMillis.get());
		return date;
	}

	public ApplicationMetrics() {
	}

}
