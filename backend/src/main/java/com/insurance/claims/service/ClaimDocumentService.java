package com.insurance.claims.service;

import com.insurance.claims.exception.ClaimException;
import com.insurance.claims.model.Claim;
import com.insurance.claims.model.ClaimDocument;
import com.insurance.claims.model.User;
import com.insurance.claims.repository.ClaimDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ClaimDocumentService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Autowired private ClaimDocumentRepository documentRepository;
    @Autowired private ClaimService claimService;

    public ClaimDocument uploadDocument(Long claimId, MultipartFile file,
                                         String documentType, User uploader) {
        Claim claim = claimService.getById(claimId);

        String originalName = file.getOriginalFilename();
        String ext = originalName != null && originalName.contains(".")
            ? originalName.substring(originalName.lastIndexOf('.')) : "";
        String stored = UUID.randomUUID().toString() + ext;

        try {
            Path dir = Paths.get(uploadDir, "claims", claimId.toString());
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), dir.resolve(stored));
        } catch (IOException e) {
            throw new ClaimException("Failed to store document: " + e.getMessage());
        }

        ClaimDocument doc = new ClaimDocument();
        doc.setClaim(claim);
        doc.setUploadedBy(uploader);
        doc.setDocumentType(ClaimDocument.DocumentType.valueOf(documentType));
        doc.setFileName(originalName);
        doc.setFilePath(uploadDir + "/claims/" + claimId + "/" + stored);
        doc.setMimeType(file.getContentType());
        doc.setFileSize(file.getSize());
        doc.setVerificationStatus(ClaimDocument.VerificationStatus.PENDING);

        return documentRepository.save(doc);
    }

    public List<ClaimDocument> getDocumentsForClaim(Long claimId) {
        Claim claim = claimService.getById(claimId);
        return documentRepository.findByClaim(claim);
    }

    public ClaimDocument verifyDocument(Long documentId, String status, String remarks) {
        ClaimDocument doc = documentRepository.findById(documentId)
            .orElseThrow(() -> new ClaimException("Document not found"));
        doc.setVerificationStatus(ClaimDocument.VerificationStatus.valueOf(status));
        doc.setVerificationRemarks(remarks);
        return documentRepository.save(doc);
    }
}
