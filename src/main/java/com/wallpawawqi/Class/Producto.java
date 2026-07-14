package com.wallpawawqi.Class;

public class Producto {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer categoryId;
    private String img;

    // Constructor vacío (para JSON parsing)
    public Producto() {}

    // Constructor con parámetros
    public Producto(String name, String description, Double price, String img) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.img = img;  // ← URL de Cloudinary
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }
}
