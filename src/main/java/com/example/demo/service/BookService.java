package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Book;
import com.example.demo.repo.BookRepository;

@Service
public class BookService {
	
	@Autowired
	private BookRepository bookRepository;
	
	public List<Book> fetchAllBook(){
		return bookRepository.fetchAllBook();
	}
	
	public void addBook(Book bk) {
		bookRepository.addBook(bk);
	}
	
	public void updateBookById(int id, Book bk) {
		bookRepository.updateBookById(id, bk);
	}
	
	public void deleteBookById(int id) {
		bookRepository.deleteBookById(id);
	}
	

}
