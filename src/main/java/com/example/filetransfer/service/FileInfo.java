package com.example.filetransfer.service;

// A record to store the original filename and the new unique name on disk
public record FileInfo(String originalFilename, String uniqueFilename) {
}