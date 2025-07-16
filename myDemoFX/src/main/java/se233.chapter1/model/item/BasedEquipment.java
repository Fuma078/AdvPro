package se233.chapter1.model.item;

import java.io.Serializable;
import javafx.scene.input.DataFormat;

public class BasedEquipment implements Serializable {
    public static final DataFormat DATA_FORMAT = new DataFormat( "src.main.java.se233.chapter1.model.item.BasedEquipment");
    protected String name;
    protected String imgpath;
    public String getName() { return name; }
    public String getImagepath() { return imgpath; }
    public void setImagepath(String imgpath) { this.imgpath = imgpath; }
}