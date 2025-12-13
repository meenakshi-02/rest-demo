package com.example.demo.repo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.demo.model.Book;

@Repository
public class BookRepository {
	
	private static final List<Book> bookList = new ArrayList<>();
	
	static{
		bookList.add(new Book(1,"A",30));
		bookList.add(new Book(2,"B",30));
		bookList.add(new Book(3,"C",30));
		bookList.add(new Book(4,"D",30));
		bookList.add(new Book(5,"E",30));
	}
	
	public List<Book> fetchAllBook(){
		return bookList;
	}
	
	public void addBook(Book book) {
		bookList.add(book);
	}
	
	public void updateBookById(int id, Book bk) {
		Book b = bookList.stream().filter(book -> book.getId()==id).findFirst().orElse(null);
		if(null!=b) {
			b.setName(bk.getName());
			b.setTotalPage(bk.getTotalPage());
		}
	}

	public void deleteBookById(int id) {
		Book b = bookList.stream().filter(book -> book.getId()==id).findFirst().orElse(null);
		if(null!=b) {
			bookList.remove(b);
		}
	}
}
