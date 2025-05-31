package com.generadordecodigo.model;

import lombok.Data;

@Data
public class CodigoRequest {
    private String texto;
    private String tipo; // QR, CODE_128, EAN_13, etc.
}
