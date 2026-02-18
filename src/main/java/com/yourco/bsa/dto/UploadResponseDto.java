package com.yourco.bsa.dto;

public class UploadResponseDto {
    private String docNumber;
    private String status;
    private String originalFilename;
    private String message;

    public UploadResponseDto() {
    }

    public UploadResponseDto(String docNumber, String status, String originalFilename, String message) {
        this.docNumber = docNumber;
        this.status = status;
        this.originalFilename = originalFilename;
        this.message = message;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Builder {
        private String docNumber;
        private String status;
        private String originalFilename;
        private String message;

        public Builder docNumber(String docNumber) {
            this.docNumber = docNumber;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder originalFilename(String originalFilename) {
            this.originalFilename = originalFilename;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public UploadResponseDto build() {
            return new UploadResponseDto(docNumber, status, originalFilename, message);
        }
    }
}
