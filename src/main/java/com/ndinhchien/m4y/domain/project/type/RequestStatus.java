package com.ndinhchien.m4y.domain.project.type;

public enum RequestStatus {
    ACCEPTED(1),
    REJECTED(-1),
    PENDING(0);

    public final Integer value;

    private RequestStatus(int value) {
        this.value = value;
    }

}
