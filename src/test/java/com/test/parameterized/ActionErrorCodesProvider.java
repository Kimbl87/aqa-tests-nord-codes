package com.test.parameterized;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class ActionErrorCodesProvider {

    public static Stream<Arguments> provideActionErrorCodes() {
        return Stream.of(
                Arguments.of(400, "{\"error\":\"bad request\"}"),
                Arguments.of(401, "{\"error\":\"unauthorized\"}"),
                Arguments.of(403, "{\"error\":\"forbidden\"}"),
                Arguments.of(404, "{\"error\":\"not found\"}"),
                Arguments.of(500, "{\"error\":\"internal server error\"}")
        );
    }
}