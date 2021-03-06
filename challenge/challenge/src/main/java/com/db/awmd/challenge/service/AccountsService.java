package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.InvalidTransferActionException;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {

	@Getter
	private final AccountsRepository accountsRepository;

	@Autowired
	public AccountsService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountId) {

		return this.accountsRepository.getAccount(accountId);
	}

	public AccountsRepository getAccountsRepository() {
		return accountsRepository;
	}

	public void transferAmount(String fromAccountId, String toAccountId, BigDecimal amount) throws InvalidTransferActionException {
		this.accountsRepository.transferAmount(fromAccountId, toAccountId, amount);
	}

}
