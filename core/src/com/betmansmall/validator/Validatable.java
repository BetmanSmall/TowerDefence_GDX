package com.betmansmall.validator;

import android.support.annotation.Nullable;

public interface Validatable {
    boolean isValid(@Nullable Object obj);
}

