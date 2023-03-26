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

	//Method for amount transfer between two accounts.
	public void transfer(Account accountFrom, Account accountTo ,BigDecimal amount) {
		
		String minId = null;
		String maxId = null;
		
		if(accountFrom.getAccountId().compareTo(accountTo.getAccountId())<0) {
			minId = accountFrom.getAccountId();
			maxId = accountTo.getAccountId();
		}
		
		if(accountFrom.getAccountId().compareTo(accountTo.getAccountId())>0) {
			minId = accountTo.getAccountId();
			maxId = accountFrom.getAccountId();
		}
		
		//Using synchronization on minimum and maximum ids so that a deadlock never occurs in a situation like:
		//transfer from Account A to B and Account B to A in parallel execution.
		
		if(accountFrom.getBalance().compareTo(amount)>0) {
			synchronized (minId) {
				log.info("thread {} begins: inside fromAccount {}:" , Thread.currentThread().getName() ,minId);
				synchronized (maxId) {
					log.info("thread {} inside toAccount {}:" ,Thread.currentThread().getName(), maxId);
					accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
					accountTo.setBalance(accountTo.getBalance().add(amount));
					notificationService.notifyAboutTransfer(accountFrom, "Money deducted from your account:" + amount);
					notificationService.notifyAboutTransfer(accountTo, "Money deposited in your account:" + amount);
					log.info("thread ends {}:" , Thread.currentThread().getName());
				}
			}
		}
		else {
			throw new InsufficientFundsException("Not enough balance in your account");
		}
	}

	
}
