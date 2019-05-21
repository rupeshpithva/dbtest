package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InvalidTransferActionException;
import com.db.awmd.challenge.service.AccountsService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

	@Autowired
	private AccountsService accountsService;

	@Test
	public void addAccount() throws Exception {
		Account account = new Account("Id-123");
		account.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(account);

		assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
	}

	@Test
	public void addAccount_failsOnDuplicateId() throws Exception {
		String uniqueId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueId);
		this.accountsService.createAccount(account);

		try {
			this.accountsService.createAccount(account);
			fail("Should have failed when adding duplicate account");
		} catch (DuplicateAccountIdException ex) {
			assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
		}

	}

	@Test
	public void transferAmount() throws InvalidTransferActionException {
		Account fromAccount = new Account("123", new BigDecimal("10000"));
		Account toAccount = new Account("xyz", new BigDecimal("20000"));
		BigDecimal amount = new BigDecimal("1100");
		this.accountsService.getAccountsRepository().clearAccounts();
		this.accountsService.createAccount(fromAccount);
		this.accountsService.createAccount(toAccount);

		try {
			this.accountsService.transferAmount(fromAccount.getAccountId(), toAccount.getAccountId(), amount);
			assertThat(this.accountsService.getAccount(fromAccount.getAccountId()).getBalance())
					.isEqualTo(new BigDecimal("8900"));
		} catch (InvalidTransferActionException e) {
			fail("The line should not be executed");
		}

	}

	@Test
	public void transferAmount_insufficientBalance() throws InvalidTransferActionException {
		Account fromAccount = new Account("123", new BigDecimal("10000"));
		Account toAccount = new Account("xyz", new BigDecimal("20000"));
		BigDecimal amount = new BigDecimal("11000");

		this.accountsService.getAccountsRepository().clearAccounts();
		this.accountsService.createAccount(fromAccount);
		this.accountsService.createAccount(toAccount);

		try {
			this.accountsService.transferAmount(fromAccount.getAccountId(), toAccount.getAccountId(), amount);
			fail("The line should not be executed");
		} catch (InvalidTransferActionException e) {
			assertThat(e.getMessage())
					.isEqualTo("The account " + fromAccount.getAccountId() + " has not sufficient balance");

		}

	}

}
