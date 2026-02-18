import java.util.Objects;

public class InvoiceTaxSummary {

    private Integer invoiceId;
    private Double totalHt;
    private Double totalTva;
    private Double totalTtc;

    public InvoiceTaxSummary() {}

    public InvoiceTaxSummary(Integer invoiceId, Double totalHt, Double totalTva, Double totalTtc) {
        this.invoiceId = invoiceId;
        this.totalHt = totalHt;
        this.totalTva = totalTva;
        this.totalTtc = totalTtc;
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Double getTotalHt() {
        return totalHt;
    }

    public void setTotalHt(Double totalHt) {
        this.totalHt = totalHt;
    }

    public Double getTotalTva() {
        return totalTva;
    }

    public void setTotalTva(Double totalTva) {
        this.totalTva = totalTva;
    }

    public Double getTotalTtc() {
        return totalTtc;
    }

    public void setTotalTtc(Double totalTtc) {
        this.totalTtc = totalTtc;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceTaxSummary that = (InvoiceTaxSummary) o;
        return Objects.equals(invoiceId, that.invoiceId) && Objects.equals(totalHt, that.totalHt) && Objects.equals(totalTva, that.totalTva) && Objects.equals(totalTtc, that.totalTtc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceId, totalHt, totalTva, totalTtc);
    }

    @Override
    public String toString() {
        return "InvoiceTaxSummary{" +
                "invoiceId=" + invoiceId +
                ", totalHt=" + totalHt +
                ", totalTva=" + totalTva +
                ", totalTtc=" + totalTtc +
                '}';
    }
}
