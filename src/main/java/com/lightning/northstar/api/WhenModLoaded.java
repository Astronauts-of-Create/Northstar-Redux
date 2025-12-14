package com.lightning.northstar.api;

import com.lightning.northstar.data.ModCompat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WhenModLoaded {

    ModCompat[] value();

}
