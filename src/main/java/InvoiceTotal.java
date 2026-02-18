import java.util.Objects;

public class InvoiceTotal {
    public int id;
    public String customerName;
    public InvoiceStatusEnum status;
    public Double total;

    public InvoiceTotal () {}

    public InvoiceTotal(int id, String customerName, InvoiceStatusEnum status, Double total) {
        this.id = id;
        this.customerName = customerName;
        this.status = status;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public InvoiceStatusEnum getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatusEnum status) {
        this.status = status;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceTotal that = (InvoiceTotal) o;
        return id == that.id && Objects.equals(customerName, that.customerName) && status == that.status && Objects.equals(total, that.total);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerName, status, total);
    }

    @Override
    public String toString() {
        return "InvoiceTotal{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", status=" + status +
                ", total=" + total +
                '}';
    }
}
