package com.dws.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.repository.AccountsRepository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Tanu Gupta
 *
 */
@Service
@Slf4j
public class AccountsService {

	@Getter
	private final AccountsRepository accountsRepository;


	@Getter
	private final NotificationService notificationService;

	@Autowired
	public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
		this.accountsRepository = accountsRepository;
		this.notificationService = notificationService;
	}

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	//Here , synchronized transfer takes place between the accounts.One thread operating on the account at a time.
	public void transfer(Account accountFrom, Account accountTo ,BigDecimal amount) {
		
		synchronized (Account.class) {
		
			log.info("Thread for transfer begins {}:" , Thread.currentThread().getName());
			if(accountFrom.getBalance().compareTo(amount)>0) {
				accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
				accountTo.setBalance(accountTo.getBalance().add(amount));
				notificationService.notifyAboutTransfer(accountFrom, "Money deducted from your account:" + amount);
				notificationService.notifyAboutTransfer(accountTo, "Money deposited in your account:" + amount);
				log.info("Thread for transfer ends here {}:" , Thread.currentThread().getName());
		}
		 else {
			throw new InsufficientFundsException("Not enough balance in your account");
		}
	
		}
	}
	
}
