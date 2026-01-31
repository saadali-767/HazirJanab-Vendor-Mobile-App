package com.example.hazirjanabvendorportal;

public class Class_ServiceRegistration {
    private String name;
    private String Category;
    private String Type;
    private boolean isSelected;

    public Class_ServiceRegistration(String name, String category, String type, boolean isSelected) {
        this.name = name;
        Category = category;
        Type = type;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
