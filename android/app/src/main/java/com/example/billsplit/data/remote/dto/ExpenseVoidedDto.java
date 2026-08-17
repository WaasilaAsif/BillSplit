package com.example.billsplit.data.remote.dto;

/** Response from DELETE /expenses/:id — {id, voided}. Not read for anything today; presence of a 2xx is enough. */
public class ExpenseVoidedDto {
    private String id;
    private boolean voided;
}
