package edu.eci.arsw.exams.moneylaunderingapi;


import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;
import edu.eci.arsw.exams.moneylaunderingapi.service.MoneyLaunderingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;

@RestController
public class MoneyLaunderingController
{
    @Autowired
    @Qualifier("moneyLaunderingServiceStub")
    MoneyLaunderingService moneyLaunderingService;

    @RequestMapping( value = "/fraud-bank-accounts", method = GET)
    public ResponseEntity<?> offendingAccounts() {
        return new ResponseEntity<>(moneyLaunderingService.getSuspectAccounts(),HttpStatus.ACCEPTED);
    }

    @RequestMapping( value = "/fraud-bank-accounts", method = POST)
    public ResponseEntity<?> addOffendingAccounts(@RequestBody SuspectAccount suspectAccount) {
        try{
            moneyLaunderingService.addSuspectAccount(suspectAccount);
            return new ResponseEntity<>("Created",HttpStatus.CREATED);
        }catch(ForbiddenException fe){
            return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping( value = "/fraud-bank-accounts/{accountId}", method = GET)
    public ResponseEntity<?> offendingAccount(@PathVariable String accountId) {
        try{
            SuspectAccount suspectAccount = moneyLaunderingService.getAccountStatus(accountId);
            return new ResponseEntity<>(suspectAccount, HttpStatus.ACCEPTED);
        }catch(NotFoundException ne){
            return new ResponseEntity<>("Not found - 404", HttpStatus.NOT_FOUND);
        }
        
    }


    @RequestMapping( value = "/fraud-bank-accounts/{accountId}", method = PUT)
    public ResponseEntity<?> updateOffendingAccount(@PathVariable String accountId, @RequestBody SuspectAccount suspectAccount) {
        try{
            moneyLaunderingService.updateAccountStatus(suspectAccount);
            return new ResponseEntity<>(suspectAccount, HttpStatus.ACCEPTED);
        }catch(NotFoundException ne){
            ne.printStackTrace();
            return new ResponseEntity<>("Not found - 404", HttpStatus.NOT_FOUND);
        }catch(ForbiddenException fe){
            fe.printStackTrace();
            return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        
    }





    //TODO
}
