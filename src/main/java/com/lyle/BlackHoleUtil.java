package com.lyle;

import org.openjdk.jmh.infra.Blackhole;

/**
 * @author lyle 2025-04-17 11:03
 */
public class BlackHoleUtil {

    public static void consume(Blackhole bh, Object... args) {
        for (Object arg : args) {
            bh.consume(arg);
        }
    }
}