package org.openmrs.module.sync2.api.clients.impl;

import java.net.URI;
import java.util.HashMap;

import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;

import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.openmrs.module.sync2.api.clients.AtomFeedClient;
import org.openmrs.module.sync2.api.exceptions.Sync2Exception;
import org.openmrs.module.sync2.api.utils.Sync2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("sync.Sync2AtomFeedClient")
@Scope("request")
public class Sync2AtomFeedClient implements AtomFeedClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(Sync2AtomFeedClient.class);
	
	private final AtomFeedProperties atomFeedProperties;
	
	private URI uri;
	
	public Sync2AtomFeedClient() {
		atomFeedProperties =  new AtomFeedProperties();
	}
	
	@Override
	public void process() {
		LOGGER.info("{} started processing", getClass().getName());
		validateConfiguration();
		
		org.ict4h.atomfeed.client.service.AtomFeedClient atomFeedClient = createAtomFeedClient();
		atomFeedClient.processEvents();
		atomFeedClient.processFailedEvents();
	}

	@Override
	public URI getUri() {
		return uri;
	}

	@Override
	public void setUri(URI uri) {
		this.uri = uri;
	}

	@Override
	public int getReadTimeout() {
		return atomFeedProperties.getReadTimeout();
	}

	@Override
	public void setReadTimeout(int readTimeout) {
		atomFeedProperties.setReadTimeout(readTimeout);
	}

	@Override
	public int getConnectTimeout() {
		return atomFeedProperties.getConnectTimeout();
	}

	@Override
	public void setConnectTimeout(int connectTimeout) {
		atomFeedProperties.setConnectTimeout(connectTimeout);
	}

	@Override
	public boolean isControlEventProcessing() {
		return atomFeedProperties.controlsEventProcessing();
	}

	@Override
	public void setControlsEventProcessing(boolean value) {
		atomFeedProperties.setControlsEventProcessing(value);
	}

	@Override
	public int getMaxFailedEvents() {
		return atomFeedProperties.getMaxFailedEvents();
	}

	@Override
	public void setMaxFailedEvents(int maxFailedEvents) {
		atomFeedProperties.setMaxFailedEvents(maxFailedEvents);
	}

	@Override
	public int getFailedEventMaxRetry() {
		return atomFeedProperties.getFailedEventMaxRetry();
	}

	@Override
	public void setFailedEventMaxRetry(int failedEventMaxRetry) {
		atomFeedProperties.setFailedEventMaxRetry(failedEventMaxRetry);
	}

	@Override
	public int getFailedEventsBatchProcessSize() {
		return atomFeedProperties.getFailedEventsBatchProcessSize();
	}

	@Override
	public void setFailedEventsBatchProcessSize(int failedEventsBatchProcessSize) {
		atomFeedProperties.setFailedEventsBatchProcessSize(failedEventsBatchProcessSize);
	}

	@Override
	public boolean isHandleRedirection() {
		return atomFeedProperties.isHandleRedirection();
	}

	@Override
	public void setHandleRedirection(boolean handleRedirection) {
		atomFeedProperties.setHandleRedirection(handleRedirection);
	}
	
	private org.ict4h.atomfeed.client.service.AtomFeedClient createAtomFeedClient() {
		HashMap<String, String> clientCookies = new HashMap<>();
		AtomFeedSpringTransactionManager atomFeedSpringTransactionManager = getAtomFeedSpringTransactionManager();
		
		return new org.ict4h.atomfeed.client.service.AtomFeedClient(
				new AllFeeds(atomFeedProperties, clientCookies),
				new AllMarkersJdbcImpl(atomFeedSpringTransactionManager),
				new AllFailedEventsJdbcImpl(atomFeedSpringTransactionManager),
				atomFeedProperties,
				atomFeedSpringTransactionManager,
				uri,
				new Sync2FeedWorker()
		);
	}

	private void validateConfiguration() {
		if (uri == null) {
			throw new Sync2Exception("URI is not set");
		}
	}
	
	private AtomFeedSpringTransactionManager getAtomFeedSpringTransactionManager() {
		return new AtomFeedSpringTransactionManager(Sync2Utils.getSpringPlatformTransactionManager());
	}
}
	
