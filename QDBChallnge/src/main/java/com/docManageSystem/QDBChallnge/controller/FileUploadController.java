package com.docManageSystem.QDBChallnge.controller;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.docManageSystem.QDBChallnge.entity.DocumentWithMetadata;
import com.docManageSystem.QDBChallnge.service.DocumentService;

@RestController
public class FileUploadController {
	@Autowired
	public DocumentService documentService;
	
//	@ResponseStatus(value = HttpStatus.OK)
//	@PostMapping("/upload")
//	public HttpStatus uploadImage(@RequestParam("pdfFile")MultipartFile file) throws IOException{
//		if(!file.getOriginalFilename().contains(".pdf")) {
//			return HttpStatus.NOT_ACCEPTABLE;
//		}
//		documentService.uploadDocument(file);
//		return HttpStatus.CREATED;
//	}
	@PostMapping("/upload")
//	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<?> uploadImage(@RequestParam("pdfFile")MultipartFile file,@RequestParam("userId")String userId, @RequestParam("userName")String userName
			,@RequestParam("library")String library,@RequestParam("description")String description) throws IOException{
		try {
		
		if(!file.getOriginalFilename().contains(".pdf")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only PDF file is allowed");
		}
		DocumentWithMetadata dWMD = new DocumentWithMetadata();
		dWMD.setUserId(userId);
		dWMD.setUserName(userName);
		dWMD.setLibrary(library);
		dWMD.setDescription(description);
		documentService.uploadDocument(file,dWMD);
		System.out.println(file.getOriginalFilename());
		return ResponseEntity.status(HttpStatus.CREATED).body("Entry is saved in DB");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
		
	}
	@GetMapping("/download/{fileName}")
//	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<?> downloadImage(@PathVariable String fileName) {
		try {
			byte[] pdf = documentService.downloadDocument(fileName);
			if(pdf.equals(null)) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Entry is not found in DB");
			}
			else {
				return ResponseEntity.status(HttpStatus.OK).body(pdf);
			}
		}catch(NoSuchElementException ex) {
			 ex.printStackTrace();
			 return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Entry is not found in DB");
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	@DeleteMapping("/delete/{docName}")
	public ResponseEntity<?>  deleteDocumentByName(@PathVariable String docName) {
		try {
			if(documentService.deleteDocument(docName)) {
				return ResponseEntity.status(HttpStatus.CREATED).body("Entry is deleted in DB");
			} else {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Entry is not found in DB");
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	@GetMapping("/getAllDocs/{userName}")
	public ResponseEntity<?> getAlldocByUserName(@PathVariable String userName){
		try {
			Set<Optional<DocumentWithMetadata>> listofDocForUser = documentService.getListofDocForUser(userName);
			if(listofDocForUser.size()<=0) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Entry is not found in DB");
			}else {
				return ResponseEntity.status(HttpStatus.OK).body(listofDocForUser);
			}
		}  catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
