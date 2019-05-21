package com.db.awmd.challenge.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db.awmd.challenge.domain.Account;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailNotificationService implements NotificationService {

	@Override
	public void notifyAboutTransfer(Account account, String transferDescription) {
		// THIS METHOD SHOULD NOT BE CHANGED - ASSUME YOUR COLLEAGUE WILL IMPLEMENT IT
		Logger LOG = LoggerFactory.getLogger(EmailNotificationService.class);
		LOG.info("Sending notification to owner of {}: {}", account.getAccountId(), transferDescription);
	}

}
