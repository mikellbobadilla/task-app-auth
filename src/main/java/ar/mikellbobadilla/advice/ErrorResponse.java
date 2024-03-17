package ar.mikellbobadilla.advice;

import lombok.Builder;

@Builder
public record ErrorResponse(int status, String error) {

}
