package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Book;
import com.example.demo.service.BookService;

@RestController
@RequestMapping("/book")
public class BookController {
	
	@Autowired
	private BookService bookService;
	
	@GetMapping("/all")
	public ResponseEntity<List<Book>> fetchAllBook() {
		return new ResponseEntity<>(bookService.fetchAllBook(),HttpStatus.OK);
	}
	
	@PostMapping("/create")
	public ResponseEntity createBook(@RequestBody Book book) {
		bookService.addBook(book);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity updateBookById(@PathVariable int id, @RequestBody Book book) {
		bookService.updateBookById(id, book);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity deleteBookById(@PathVariable int id) {
		bookService.deleteBookById(id);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}
	

}
