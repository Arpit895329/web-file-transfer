package com.example.filetransfer;
// import com.example.filetransfer.service.FileMapService;
import com.example.filetransfer.service.FileStorageService;
import com.example.filetransfer.service.QrCodeService;
import com.example.filetransfer.service.FileInfo;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api")
public class FileTransferController {

    private final FileStorageService fileStorageService;
    private final FileMapService fileMapService;
    private final QrCodeService qrCodeService;

    public FileTransferController(FileStorageService fileStorageService,
                                  FileMapService fileMapService,
                                  QrCodeService qrCodeService) {
        this.fileStorageService = fileStorageService;
        this.fileMapService = fileMapService;
        this.qrCodeService = qrCodeService;
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        // 1. Store the file on disk with a unique name
        String uniqueFilename = fileStorageService.store(file);

        // 2. Generate a 6-digit code and map it to the file info
        String code = fileMapService.generateCodeAndMap(file.getOriginalFilename(), uniqueFilename);

        // 3. Create the full download URL
        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/download/")
                .path(code)
                .toUriString();

        // 4. Generate the QR code from the download URL
        String qrCodeBase64 = qrCodeService.generateQrCodeBase64(downloadUrl);

        // 5. Return the code and the Base64 QR image
        return ResponseEntity.ok(new UploadResponse(code, "data:image/png;base64," + qrCodeBase64));
    }

    @GetMapping("/download/{code}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String code) {
        // 1. Look up the file info using the code
        FileInfo fileInfo = fileMapService.getFileInfo(code);
        if (fileInfo == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. Load the file from disk
        Resource resource = fileStorageService.loadAsResource(fileInfo.uniqueFilename());

        // 3. Build the response, forcing a "download" dialog
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileInfo.originalFilename() + "\"")
                .body(resource);
    }
}