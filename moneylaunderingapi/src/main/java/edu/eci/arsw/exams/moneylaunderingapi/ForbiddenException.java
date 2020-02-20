package edu.eci.arsw.exams.moneylaunderingapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -2180448574726521787L;
    
}