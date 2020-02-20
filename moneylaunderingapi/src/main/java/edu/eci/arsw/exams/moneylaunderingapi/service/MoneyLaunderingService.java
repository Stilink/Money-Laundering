package edu.eci.arsw.exams.moneylaunderingapi.service;

import edu.eci.arsw.exams.moneylaunderingapi.ForbiddenException;
import edu.eci.arsw.exams.moneylaunderingapi.NotFoundException;
import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface MoneyLaunderingService {
    void updateAccountStatus(SuspectAccount suspectAccount) throws ForbiddenException, NotFoundException;
    SuspectAccount getAccountStatus(String accountId) throws NotFoundException;
    List<SuspectAccount> getSuspectAccounts();
    void addSuspectAccount(SuspectAccount suspectAccount) throws ForbiddenException;
}
