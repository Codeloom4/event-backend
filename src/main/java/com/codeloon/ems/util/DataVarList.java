package com.codeloon.ems.util;


public class DataVarList {
    private DataVarList() {
        throw new UnsupportedOperationException("DataVarList class cannot be instantiated");
    }

    // User roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_EMPLOYEE = "EMPLOYEE";
    public static final String ROLE_CLIENT = "CLIENT";

    // Authentication responses,
    public static final String SUCCESS_AUTH="Authentication success.";
    public static final String AUTH_SUCCESS ="0";
    public static final String AUTH_FAILED_RESET_PASSWORD ="1";
    public static final String AUTH_FAILED_ACCOUNT_ISSUE ="2";

    public static final String FAILED_AUTH_ACC_BLOCKED ="Failed to authenticate: user account is disabled.";

    public static final String FAILED_AUTH_ACC_LOCKED ="Failed to authenticate: user account is locked.";

    public static final String FAILED_AUTH_ACC_EXPIRED ="Failed to authenticate: user account has expired.";

    public static final String FAILED_AUTH_CRED_EXPIRED ="Failed to authenticate: user credentials have expired. Please reset your password.";

    public static final String FAILED_AUTH_FIRST_LOGIN ="Failed to authenticate: Password change required before logging in";

    public static final String FAILED_AUTH_ACC_INVALIED ="Invalid username or password.";

    //Payment Status
    public static final String FULLY_PAID ="FULLY_PAID";
    public static final String PARTIALLY_PAID ="PARTIALLY_PAID";
    public static final String UNPAID ="UNPAID";

    //Order Status
    public static final String ORD_PENDING ="ORD_PENDING";
    public static final String ORD_APPROVED ="ORD_APPROVED";
    public static final String ORD_CANCELED ="ORD_CANCELED";
    public static final String ORD_REJECTED ="ORD_REJECTED";

    public static final String REFUNDABLE_PENDING ="REF_PENDING";

    public static final String REFUNDABLE_RECEIVED ="REF_RECEIVED";

}
