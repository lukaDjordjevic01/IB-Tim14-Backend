package com.example.demo.controller.certificate;

import com.example.demo.dto.ErrorDTO;
import com.example.demo.dto.certificate.CertificateDTO;
import com.example.demo.dto.certificate.CertificateRequestDTO;
import com.example.demo.model.certificate.Certificate;
import com.example.demo.model.certificate.CertificateRequest;
import com.example.demo.service.certificate.CertificateRequestService;
import com.example.demo.service.certificate.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping(value = "api/certificate")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CertificateRequestService certificateRequestService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping(value = "/create-request", consumes = "application/json")
    public ResponseEntity<?> createRequest(@RequestBody CertificateRequestDTO certificateRequestDTO) {
        try {
            return new ResponseEntity<>(certificateService.createRequest(certificateRequestDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping(value = "/create-certificate/{id}")
    public ResponseEntity<?> createCertificate(@PathVariable Integer id) {
        try {
            CertificateRequest certificateRequest = certificateRequestService.findById(id);
            Certificate certificate = certificateService.issueCertificate(certificateRequest);
            return new ResponseEntity<>(new CertificateDTO(certificate), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping(value = "/reject-certificate/{id}")
    public ResponseEntity<?> rejectCertificateRequest(@PathVariable Integer id) {
        try {
            CertificateRequestDTO certificateRequestDTO = certificateRequestService.rejectCertificateRequest(id);
            return new ResponseEntity<>(certificateRequestDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping(value = "/certificates")
    public ResponseEntity<?> getCertificates() {
        try {
            List<CertificateDTO> certificateDTOS = certificateService.getAllCertificates();
            return new ResponseEntity<>(certificateDTOS, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping(value = "/certificate-requests/{id}")
    public ResponseEntity<?> getCertificateRequests(@PathVariable Integer id) {
        try {
            List<CertificateRequestDTO> certificateRequestDTOS = certificateRequestService.getAllUserRequests(id);
            return new ResponseEntity<>(certificateRequestDTOS, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping(value = "/validity/{id}")
    public ResponseEntity<?> getValidity(@PathVariable Integer id) {
        try {
            certificateService.checkValidity(id);
            return new ResponseEntity<>("Certificate is valid.", HttpStatus.OK);
        } catch (CertificateExpiredException e) {
            return new ResponseEntity<>("Certificate is expired.", HttpStatus.NO_CONTENT);
        } catch (CertificateNotYetValidException e) {
            return new ResponseEntity<>("Certificate is not yet valid.", HttpStatus.NO_CONTENT);
        } catch (Exception e){
            return new ResponseEntity<>(new ErrorDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

}