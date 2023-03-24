package com.dws.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Execution(ExecutionMode.CONCURRENT)
@Slf4j
class AccountsServiceTest {

	@Autowired
	private AccountsService accountsService;

	@Autowired
	private AccountsService accountsService1;

	@Autowired
	private NotificationService notificationService;

	private static Account accountA = new Account("Id-123", BigDecimal.valueOf(20000.00));
	private static Account accountB = new Account("Id-345", BigDecimal.valueOf(30000.00));
	private static Account accountC = new Account("Id-555", BigDecimal.valueOf(40000.00));





	void addAccount() {
		Account account = new Account("Id-123");
		account.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(account);

		assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
	}

	@Test
	void addAccount_failsOnDuplicateId() {
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



	//Test for sequential transfer
	@Test
	void sequential_transfer_AtoB() throws InterruptedException {

		notificationService = Mockito.mock(NotificationService.class);
		Mockito.doNothing().when(notificationService).notifyAboutTransfer(Mockito.any
				(), Mockito.anyString());
		accountsService.transfer(accountA, accountB,
				BigDecimal.valueOf(10000.00));
		assertEquals(accountA.getBalance(),BigDecimal.valueOf(10000.00));
		assertThat(accountB.getBalance().compareTo(BigDecimal.ZERO)>0);

	}

	//Test for sequential transfer
	@Test
	void sequential_transfer_BtoC() throws InterruptedException {

		notificationService = Mockito.mock(NotificationService.class);
		Mockito.doNothing().when(notificationService).notifyAboutTransfer(Mockito.any
				(), Mockito.anyString()); accountsService1.transfer(accountB, accountC,
						BigDecimal.valueOf(5000.00)); 
				assertThat(accountB.getBalance().compareTo(BigDecimal.ZERO)>0);
				assertEquals(accountC.getBalance(),BigDecimal.valueOf(45000.00));
	}


	//Test for parallel transfer

	@Test
	void concurrent_transfer_1to2() {

				Account account1 = new Account("Id-888", BigDecimal.valueOf(20000.00)); 
				Account account2 = new Account("Id-888", BigDecimal.valueOf(30000.00)); 
				notificationService = Mockito.mock(NotificationService.class);
				Mockito.doNothing().when(notificationService).notifyAboutTransfer(Mockito.any
						(), Mockito.anyString());
				this.accountsService.transfer(account1, account2,
								BigDecimal.valueOf(5000.00));
				assertEquals(account1.getBalance(),BigDecimal.valueOf(15000.00));
				assertEquals(account2.getBalance(),BigDecimal.valueOf(35000.00));

	}

	//Test for parallel transfer

	@Test
	void concurrent_transfer_3to4() { 
		
		Account account3 = new Account("Id-888",
				BigDecimal.valueOf(10000.00));
		Account account4 = new Account("Id-888",
						BigDecimal.valueOf(20000.00)); 
				notificationService = Mockito.mock(NotificationService.class);
				Mockito.doNothing().when(notificationService).notifyAboutTransfer(Mockito.any
						(), Mockito.anyString()); 
				this.accountsService.transfer(account3, account4,
								BigDecimal.valueOf(5000.00));
				assertEquals(account3.getBalance(),BigDecimal.valueOf(5000.00));
				assertEquals(account4.getBalance(),BigDecimal.valueOf(25000.00)); }

	//Negative test case with the transfer failing due to insufficient funds and throwing exception
	@Test 
	void transfer_fail() {

		Exception exception = assertThrows(RuntimeException.class, () -> {
			this.accountsService.transfer(accountA, accountB,
					BigDecimal.valueOf(20000.00)); });

		String expectedMessage = "Not enough balance in your account"; String
		actualMessage = exception.getMessage(); assertEquals(expectedMessage,
				actualMessage);


	}


}
