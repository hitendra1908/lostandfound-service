package com.lostandfound.exception.claim;

public sealed class ClaimException extends RuntimeException permits ClaimQuantityException,
        ClaimingUserNotFoundException, ClaimItemNotFoundException{
    public ClaimException(final String message) {
        super(message);
    }
}
