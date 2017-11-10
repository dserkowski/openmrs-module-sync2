package org.openmrs.module.sync2.api.clients.impl;

import java.net.URI;

import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.sync2.api.exceptions.Sync2Exception;
import org.powermock.api.mockito.PowerMockito;

@RunWith(MockitoJUnitRunner.class)
public class Sync2AtomFeedClientTest {
	
	@Mock
	private AtomFeedClient atomFeedClient;
	
	@InjectMocks
	private Sync2AtomFeedClient sync2AtomFeedClient;
	
	@Test
	public void process_shouldProcessBothEventsAndFailedEvents() {
		sync2AtomFeedClient.setUri(PowerMockito.mock(URI.class));
		sync2AtomFeedClient.process();
		
		Mockito.verify(atomFeedClient).processEvents();
		Mockito.verify(atomFeedClient).processFailedEvents();
	}
	
	@Test(expected = Sync2Exception.class)
	public void process_shouldCheckIfUriIsNotNull() {
		sync2AtomFeedClient.process();
	}
}