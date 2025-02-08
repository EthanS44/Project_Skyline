package org.Skyline;

import jakarta.persistence.*;
import java.util.List;


public class Model {

    private Integer id;
    private String name;
    private String user;
    private List<Attributes> AttributesList;

    public Model(String name, String user, List<Attributes> list){
        this.name = name;
        this.user = user;
        AttributesList = list;
    }

    public Model() {}

    public String getName(){
        return name;
    }

    public void setUser(String user){
        this.user = user;
    }

    public List<Attributes> getAttributesList(){
        return AttributesList;
    }

    public String getUser() {
        return user;
    }


    @Override
    public String toString() {return name;}



    public String showAttributes() {
        StringBuilder sb = new StringBuilder();
        sb.append("Model{")
                .append("id=").append(id)
                .append(", name='").append(name).append('\'')
                .append(", AttributesList=[");

        if (AttributesList != null && !AttributesList.isEmpty()) {
            for (Attributes attributes : AttributesList) {
                sb.append("\n    ").append(attributes.toString());
            }
            sb.append("\n");
        }
        sb.append("]}");
        return sb.toString();
    }



}
