package com.dws.challenge.web;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.service.AccountsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
@Validated
public class AccountsController {

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
    return this.accountsService.getAccount(accountId);
  }
  
  
  @GetMapping(path = "/{accountId1}/{accountId2}/{amount}")
  public ResponseEntity<Object> transferBetweenAccount(@PathVariable @NotNull String accountId1 ,@PathVariable @NotNull String accountId2, 
		@PathVariable @Min(value = 0, message = "Initial balance must be positive.") BigDecimal amount) {

	  if(accountsService.getAccount(accountId1)!=null && accountsService.getAccount(accountId2)!=null) {
    	this.accountsService.transfer(accountsService.getAccount(accountId1), accountsService.getAccount(accountId2), amount);
    } else {
    	 return new ResponseEntity<>("Accounts dont exist in database", HttpStatus.BAD_REQUEST);
    }
    
    return new ResponseEntity<>(HttpStatus.OK);
  }


}
