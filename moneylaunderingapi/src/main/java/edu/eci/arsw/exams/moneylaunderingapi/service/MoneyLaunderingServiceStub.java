package edu.eci.arsw.exams.moneylaunderingapi.service;

import edu.eci.arsw.exams.moneylaunderingapi.ForbiddenException;
import edu.eci.arsw.exams.moneylaunderingapi.NotFoundException;
import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component("moneyLaunderingServiceStub")
public class MoneyLaunderingServiceStub implements MoneyLaunderingService {

    Map<String, SuspectAccount> suspectAccounts = new HashMap<>();

    @Override
    public synchronized void updateAccountStatus(SuspectAccount suspectAccount) throws ForbiddenException, NotFoundException{
        if(!suspectAccounts.containsKey(suspectAccount.accountId)){
            throw new NotFoundException();
        }else{
            suspectAccounts.replace(suspectAccount.accountId, suspectAccount);
        }
    }

    @Override
    public SuspectAccount getAccountStatus(String accountId) throws NotFoundException{
        SuspectAccount sa;
        if(!suspectAccounts.containsKey(accountId)){
            throw new NotFoundException();
        }else{
            sa = suspectAccounts.get(accountId);
        }
        return sa;
    }

    @Override
    public List<SuspectAccount> getSuspectAccounts() {
        List<SuspectAccount> list = new ArrayList<>();
        for (SuspectAccount suspectAccount : suspectAccounts.values()) {
            list.add(suspectAccount);
        }
        return list;
    }

    @Override
    public void addSuspectAccount(SuspectAccount suspectAccount) throws ForbiddenException {
        if(suspectAccounts.containsKey(suspectAccount.accountId)){
            throw new ForbiddenException();
        }else{
            suspectAccounts.put(suspectAccount.accountId, suspectAccount);
        }
    }
}
