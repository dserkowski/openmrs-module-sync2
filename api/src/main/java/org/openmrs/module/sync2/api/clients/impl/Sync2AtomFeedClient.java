package org.openmrs.module.sync2.api.clients.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.ict4h.atomfeed.client.repository.AllFailedEvents;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.AllMarkers;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;
import org.ict4h.atomfeed.client.service.AtomFeedClient;

import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.openmrs.module.sync2.api.clients.Client;
import org.openmrs.module.sync2.api.exceptions.Sync2Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;

public class Sync2AtomFeedClient implements Client {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Sync2AtomFeedClient.class);
	
	private final AllMarkers allMarkers;
	
	private final AllFailedEvents allFailedEvents;
	
	private final HashMap<String, String> clientCookies;
	
	private AtomFeedProperties atomFeedProperties;
	
	private URI uri;
	
	@Autowired
	private PlatformTransactionManager springPlatformTransactionManager;
	
	public Sync2AtomFeedClient() {
		// TODO: change it to constructor with params - it is only for tests
		String feedUri = "http://localhost:8080/feed/recent";
		try {
			this.uri = new URI(feedUri);
		} catch (URISyntaxException e) {
			throw new Sync2Exception("Provided incorrect feed URI:" + feedUri, e);
		}
		
		AtomFeedSpringTransactionManager atomFeedSpringTransactionManager = getAtomFeedSpringTransactionManager();
		allMarkers = new AllMarkersJdbcImpl(atomFeedSpringTransactionManager);
		allFailedEvents = new AllFailedEventsJdbcImpl(atomFeedSpringTransactionManager);
		clientCookies = new HashMap<>();
		atomFeedProperties =  new AtomFeedProperties(); // TODO: make it more generic - create getters and setters
	}
	
	private AtomFeedClient createAtomFeedClient() {
		return new AtomFeedClient(
			new AllFeeds(atomFeedProperties, clientCookies),
			allMarkers,
			allFailedEvents,
			atomFeedProperties,
			getAtomFeedSpringTransactionManager(),
			uri,
			new Sync2FeedWorker()
		);
	}
	
	@Override
	public void process() {
		LOGGER.info("{} started processing", getClass().getName());
		AtomFeedClient atomFeedClient = createAtomFeedClient();
		atomFeedClient.processEvents();
		atomFeedClient.processFailedEvents();
	}
	
	private AtomFeedSpringTransactionManager getAtomFeedSpringTransactionManager() {
		return new AtomFeedSpringTransactionManager(springPlatformTransactionManager);
	}
	
	public URI getUri() {
		return uri;
	}
	
	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	public AtomFeedProperties getAtomFeedProperties() {
		return atomFeedProperties;
	}
	
	public void setAtomFeedProperties(AtomFeedProperties atomFeedProperties) {
		this.atomFeedProperties = atomFeedProperties;
	}
}
	
