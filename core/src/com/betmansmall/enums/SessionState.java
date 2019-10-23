package com.betmansmall.enums;

public enum SessionState {
    // for server
    INITIALIZATION, // for server
    WAIT_CONNECTIONS, // for server
    PLAYER_CONNECTED,
    // for client
    CONNECTED,
    RECEIVED_SERVER_INFO_DATA,
    INITIALIZED,
}
