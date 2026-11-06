package br.com.scriptum.dto;

import jakarta.validation.constraints.NotBlank;

public class NoteRequest {
    public Long categoryId; 
    
    @NotBlank(message = "Title cannot be blank")
    public String titulo;
    
    public String imageBase64;
    
    public String language = "por"; 
}