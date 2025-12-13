package com.example.demo.model;

public class Book {
	private int id;
	private String name;
	private int totalPage;
	
	public Book(int id, String name, int totalPage) {
		super();
		this.id = id;
		this.name = name;
		this.totalPage = totalPage;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	
	

}
