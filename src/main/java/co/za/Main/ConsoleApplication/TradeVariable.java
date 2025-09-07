package co.za.Main.ConsoleApplication;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TradeVariable {
    private final StringProperty variable;
    private final StringProperty minimum;
    private final StringProperty maximum;
    private final StringProperty returnMin;
    private final StringProperty returnMax;
    
    public TradeVariable(String variable, String minimum, String maximum, String returnMin, String returnMax) {
        this.variable = new SimpleStringProperty(variable);
        this.minimum = new SimpleStringProperty(minimum);
        this.maximum = new SimpleStringProperty(maximum);
        this.returnMin = new SimpleStringProperty(returnMin);
        this.returnMax = new SimpleStringProperty(returnMax);
    }
    
    // Variable property
    public StringProperty variableProperty() { return variable; }
    public String getVariable() { return variable.get(); }
    public void setVariable(String value) { variable.set(value); }
    
    // Minimum property
    public StringProperty minimumProperty() { return minimum; }
    public String getMinimum() { return minimum.get(); }
    public void setMinimum(String value) { minimum.set(value); }
    
    // Maximum property
    public StringProperty maximumProperty() { return maximum; }
    public String getMaximum() { return maximum.get(); }
    public void setMaximum(String value) { maximum.set(value); }
    
    // Return Min property
    public StringProperty returnMinProperty() { return returnMin; }
    public String getReturnMin() { return returnMin.get(); }
    public void setReturnMin(String value) { returnMin.set(value); }
    
    // Return Max property
    public StringProperty returnMaxProperty() { return returnMax; }
    public String getReturnMax() { return returnMax.get(); }
    public void setReturnMax(String value) { returnMax.set(value); }
}