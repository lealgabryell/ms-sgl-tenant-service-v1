package com.sgl.tenant.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateTenantRequest(

        @NotBlank(message = "O nome do tenant é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,

        @NotBlank(message = "O CNPJ é obrigatório")
        @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter exatamente 14 dígitos numéricos")
        String cnpj,

        @NotBlank(message = "O schemaName é obrigatório")
        @Pattern(regexp = "^[a-z][a-z0-9_]{2,49}$",
                message = "O schemaName deve iniciar com letra minúscula e conter apenas letras minúsculas, números e underscore (3-50 caracteres)")
        String schemaName
) {
}

