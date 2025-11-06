package com.example.filetransfer;

// A record to send back the code and QR image data
public record UploadResponse(String code, String qrCodeBase64) {
}