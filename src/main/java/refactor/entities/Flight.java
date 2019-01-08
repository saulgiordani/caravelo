package refactor.entities;

public class Flight {
    private Integer capacity;
    private Integer sold;
    private boolean longHaul;

    public Flight(Integer capacity, Integer sold) {
        this.capacity = capacity;
        this.sold = sold;
        this.longHaul = false;
    }

    public boolean isLongHaul() {
        return longHaul;
    }

    public void setLongHaul(boolean longHaul) {
        this.longHaul = longHaul;
    }

    public Integer getSold() {
        return sold;
    }

    public void setSold(Integer sold) {
        this.sold = sold;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
