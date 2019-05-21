package com.db.awmd.challenge.web;

import java.math.BigDecimal;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InvalidTransferActionException;
import com.db.awmd.challenge.service.AccountsService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

	Logger log = LoggerFactory.getLogger(AccountsController.class);
	private final AccountsService accountsService;

	@Autowired
	public AccountsController(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
		log.info("Creating account {}", account);

		try {
			this.accountsService.createAccount(account);
		} catch (DuplicateAccountIdException daie) {
			return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping(path = "/{accountId}")
	public Account getAccount(@PathVariable String accountId) {
		log.info("Retrieving account for id {}", accountId);
		Account existingAccount = this.accountsService.getAccount(accountId);

		if (null != existingAccount) {
			return existingAccount;
		} else {
			log.info("This account " + accountId + " does not exist");
			return null;
		}
	}


	
	@RequestMapping(method = RequestMethod.GET, value = "/transferAmount")
	public ResponseEntity<Object> transferAmount(@RequestParam("fromAccountId") String fromAccountId, @RequestParam("toAccountId") String toAccountId,
			@RequestParam("amount") BigDecimal amount) {

		if (amount.compareTo(BigDecimal.valueOf(0)) < 0) {
			log.info("The amount to be transffered can not be negative");
			return new ResponseEntity<Object>("The amount to be transffered can not be negative",
					HttpStatus.BAD_REQUEST);
		}

		if (this.accountsService.getAccount(fromAccountId) == null) {
			return new ResponseEntity<Object>("From account " + fromAccountId + " does not exist",
					HttpStatus.BAD_REQUEST);
		}

		if (this.accountsService.getAccount(toAccountId) == null) {
			return new ResponseEntity<Object>("To account " + toAccountId + " does not exist",
					HttpStatus.BAD_REQUEST);
		}
		try {
			this.accountsService.transferAmount(fromAccountId, toAccountId, amount);
		} catch (InvalidTransferActionException e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<Object>("Amount transfer complete", HttpStatus.OK);
	}

}
