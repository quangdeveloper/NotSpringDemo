package vn.vnpay.notspringdemo.model;

public class ParameterORA {

    private String type;

    private String name;

    private Object value;

    public ParameterORA(String name, Object value,String type) {
        this.type = type;
        this.name = name;
        this.value = value;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
