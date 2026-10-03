package com.fintrack.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<ApiResponse> handleException(Exception ex) {
    //     ApiResponse response = new ApiResponse(500, ex.getMessage());
    //     return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    // }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleEmailAlreadyExists(
        EmailAlreadyExistsException ex) {
        ApiResponse response = new ApiResponse(409, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ExpenseNotFoundException.class)
    public ResponseEntity<ApiResponse> handleExpenseNotFound(
        ExpenseNotFoundException ex) {

        ApiResponse response = new ApiResponse(
        HttpStatus.NOT_FOUND.value(),
        ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
  
    @ExceptionHandler(BudgetNotFoundException.class)
    public ResponseEntity<ApiResponse> handleBudgetNotFound(
            BudgetNotFoundException ex) {

        ApiResponse response = new ApiResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage());

        return new ResponseEntity<>(
                response,
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidBudgetException.class)
    public ResponseEntity<ApiResponse> handleInvalidBudget(
            InvalidBudgetException ex) {

        ApiResponse response = new ApiResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage());

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncomeNotFoundException.class)
    public ResponseEntity<ApiResponse> handleIncomeNotFound(
            IncomeNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage()));
    }

    @ExceptionHandler(InvalidIncomeException.class)
    public ResponseEntity<ApiResponse> handleInvalidIncome(
            InvalidIncomeException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage()));
    }

    @ExceptionHandler(FinancialGoalNotFoundException.class)
    public ResponseEntity<ApiResponse> handleFinancialGoalNotFound(
                    FinancialGoalNotFoundException ex) {

            return ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse(
                                            HttpStatus.NOT_FOUND.value(),
                                            ex.getMessage()));
    }

    @ExceptionHandler(InvalidFinancialGoalException.class)
    public ResponseEntity<ApiResponse> handleInvalidFinancialGoal(
                    InvalidFinancialGoalException ex) {

            return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse(
                                            HttpStatus.BAD_REQUEST.value(),
                                            ex.getMessage()));
    }

}