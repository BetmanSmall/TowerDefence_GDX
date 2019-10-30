package com.betmansmall.game;

import java.io.Serializable;

/**
 * TODO mb should be only PLAYER and SPECTATOR
 * @author Alexander on 28.10.2019.
 */
public enum PlayerType implements Serializable {
    SERVER,
    CLIENT,
    SPECTATOR
}
