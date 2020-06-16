package com.loory.unibook.Model;

public class Post {
    private String postid;
    private String postimage;
    private String publisher;
    private String title;
    private String author;
    private String price;
    private String numOfPages;
    private String edition;

    public Post(String postid, String postimage, String publisher, String title, String author, String price, String numOfPages, String edition) {
        this.postid = postid;
        this.postimage = postimage;
        this.publisher = publisher;
        this.title = title;
        this.author = author;
        this.price = price;
        this.numOfPages = numOfPages;
        this.edition = edition;
    }

    public Post() {
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNumOfPages() {
        return numOfPages;
    }

    public void setNumOfPages(String numOfPages) {
        this.numOfPages = numOfPages;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

}
