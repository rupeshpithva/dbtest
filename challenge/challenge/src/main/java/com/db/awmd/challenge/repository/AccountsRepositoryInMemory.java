package com.db.awmd.challenge.repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InvalidTransferActionException;
import com.db.awmd.challenge.service.EmailNotificationService;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

	private final Map<String, Account> accounts = new ConcurrentHashMap<>();

	public static final EmailNotificationService notificationService = new EmailNotificationService();

	@Override
	public void createAccount(Account account) throws DuplicateAccountIdException {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}
	}

	@Override
	public Account getAccount(String accountId) {

		return accounts.get(accountId);

	}

	@Override
	public void clearAccounts() {
		accounts.clear();
	}

	@Override
	public void transferAmount(String fromAccountId, String toAccountId, BigDecimal amount)
			throws InvalidTransferActionException {
		Account fromAccount = this.getAccount(fromAccountId);
		Account toAccount = this.getAccount(toAccountId);

		synchronized (this) {
			if (fromAccount.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
				throw new InvalidTransferActionException(
						"The account " + fromAccountId + " has not sufficient balance");
			}

			BigDecimal balance = fromAccount.getBalance().subtract(amount);
			BigDecimal toBalance = toAccount.getBalance().add(amount);

			fromAccount.setBalance(balance);
			toAccount.setBalance(toBalance);

			accounts.put(fromAccountId, fromAccount);
			accounts.put(toAccountId, toAccount);

			notificationService.notifyAboutTransfer(fromAccount,
					"amount " + amount.toPlainString() + " debited from acount " + fromAccountId);
			notificationService.notifyAboutTransfer(toAccount,
					"amount " + amount.toPlainString() + " credited from acount " + fromAccountId);

		}

	}

}
