import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    // =======================
    // Q1 - Total par facture
    // =======================
    public List<InvoiceTotal> findInvoiceTotals() {

        String sql = """
            SELECT i.id,
                   i.customer_name,
                   i.status,
                   SUM(il.quantity * il.unit_price) AS total
            FROM invoice i
            JOIN invoice_line il ON il.invoice_id = i.id
            GROUP BY i.id, i.customer_name, i.status
        """;

        DBConnection dbConnection = new DBConnection();

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            List<InvoiceTotal> invoiceTotals = new ArrayList<>();

            while (resultSet.next()) {
                InvoiceTotal invoiceTotal = new InvoiceTotal();
                invoiceTotal.setId(resultSet.getInt("id"));
                invoiceTotal.setCustomerName(resultSet.getString("customer_name"));
                invoiceTotal.setStatus(
                        InvoiceStatusEnum.valueOf(resultSet.getString("status"))
                );
                invoiceTotal.setTotal(resultSet.getDouble("total"));

                invoiceTotals.add(invoiceTotal);
            }

            return invoiceTotals;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ==========================================
    // Q2 - Factures CONFIRMED et PAID uniquement
    // ==========================================
    public List<InvoiceTotal> findConfirmedAndPaidInvoiceTotals() {

        String sql = """
            SELECT i.id,
                   i.customer_name,
                   i.status,
                   SUM(il.quantity * il.unit_price) AS total
            FROM invoice i
            JOIN invoice_line il ON il.invoice_id = i.id
            WHERE i.status IN ('CONFIRMED', 'PAID')
            GROUP BY i.id, i.customer_name, i.status
        """;

        DBConnection dbConnection = new DBConnection();

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<InvoiceTotal> result = new ArrayList<>();

            while (rs.next()) {
                InvoiceTotal it = new InvoiceTotal();
                it.setId(rs.getInt("id"));
                it.setCustomerName(rs.getString("customer_name"));
                it.setStatus(InvoiceStatusEnum.valueOf(rs.getString("status")));
                it.setTotal(rs.getDouble("total"));
                result.add(it);
            }

            return result;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ==========================
    // Q3 - Totaux par statut
    // ==========================
    public InvoiceStatusTotals computeStatusTotals() {

        String sql = """
            SELECT
                SUM(CASE WHEN status = 'PAID' THEN total ELSE 0 END) AS total_paid,
                SUM(CASE WHEN status = 'CONFIRMED' THEN total ELSE 0 END) AS total_confirmed,
                SUM(CASE WHEN status = 'DRAFT' THEN total ELSE 0 END) AS total_draft
            FROM (
                SELECT i.status,
                       SUM(il.quantity * il.unit_price) AS total
                FROM invoice i
                JOIN invoice_line il ON il.invoice_id = i.id
                GROUP BY i.id, i.status
            ) t
        """;

        DBConnection dbConnection = new DBConnection();

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();

            InvoiceStatusTotals totals = new InvoiceStatusTotals();
            totals.setTotalPaid(rs.getDouble("total_paid"));
            totals.setTotalConfirmed(rs.getDouble("total_confirmed"));
            totals.setTotalDraft(rs.getDouble("total_draft"));

            return totals;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ==========================
    // Q4 - CA pondéré
    // ==========================
    public Double computeWeightedTurnover() {

        String sql = """
            SELECT SUM(
                CASE status
                    WHEN 'PAID' THEN total
                    WHEN 'CONFIRMED' THEN total * 0.5
                    ELSE 0
                END
            ) AS weighted_total
            FROM (
                SELECT i.status,
                       SUM(il.quantity * il.unit_price) AS total
                FROM invoice i
                JOIN invoice_line il ON il.invoice_id = i.id
                GROUP BY i.id, i.status
            ) t
        """;

        DBConnection dbConnection = new DBConnection();

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getDouble("weighted_total");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<InvoiceTaxSummary> findInvoiceTaxSummaries() {

        String sql = """
        SELECT i.id AS invoice_id,
               ht.total_ht,
               ht.total_ht * t.rate / 100 AS total_tva,
               ht.total_ht * (1 + t.rate / 100) AS total_ttc
        FROM invoice i
        JOIN (
            SELECT invoice_id,
                   SUM(quantity * unit_price) AS total_ht
            FROM invoice_line
            GROUP BY invoice_id
        ) ht ON ht.invoice_id = i.id
        JOIN tax_config t ON true
        ORDER BY i.id
    """;

        DBConnection dbConnection = new DBConnection();

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<InvoiceTaxSummary> result = new ArrayList<>();

            while (rs.next()) {
                InvoiceTaxSummary summary = new InvoiceTaxSummary();
                summary.setInvoiceId(rs.getInt("invoice_id"));
                summary.setTotalHt(rs.getDouble("total_ht"));
                summary.setTotalTva(rs.getDouble("total_tva"));
                summary.setTotalTtc(rs.getDouble("total_ttc"));
                result.add(summary);
            }

            return result;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Double computeWeightedTurnoverTtc() {

        String sql = """
        SELECT SUM(
            CASE i.status
                WHEN 'PAID' THEN total_ttc
                WHEN 'CONFIRMED' THEN total_ttc * 0.5
                ELSE 0
            END
        ) AS weighted_ttc
        FROM invoice i
        JOIN (
            SELECT il.invoice_id,
                   SUM(il.quantity * il.unit_price) *
                   (1 + t.rate / 100) AS total_ttc
            FROM invoice_line il
            JOIN tax_config t ON true
            GROUP BY il.invoice_id, t.rate
        ) totals ON totals.invoice_id = i.id
    """;

        DBConnection dbConnection = new DBConnection();

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getDouble("weighted_ttc");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
