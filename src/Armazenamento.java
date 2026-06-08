import java.sql.*;
import java.util.Scanner;

public class Armazenamento {
    public static void createTable(Connection conn) throws SQLException {
        // Ajustado: Adicionadas as vírgulas que faltavam ao final das linhas de qrcode e criador
        String sql = "CREATE TABLE IF NOT EXISTS armazenamento (" +
                "id SERIAL PRIMARY KEY, "+
                "rem_nome VARCHAR(20) NOT NULL, "+
                "rem_id INTEGER NOT NULL, "+
                "rem_validade INTEGER NOT NULL CHECK (rem_validade BETWEEN 1800 AND 3026), "+
                "rem_lote VARCHAR(20) NOT NULL,"+
                "rem_qrcode TEXT NOT NULL, "+
                "criador VARCHAR(80) NOT NULL, "+
                "doador VARCHAR(80) NOT NULL)"
                ;

        Statement stmt = conn.createStatement();
        stmt.execute(sql); //Executa comando sql
        stmt.close(); // fecha instrução
    }

    public static void create(Connection conn, Scanner in) throws SQLException {
        System.out.println("Informe o nome do remédio: ");
        String nome = in.next();
        System.out.println("Informe o ID do remédio: ");
        int remId = in.nextInt();
        System.out.println("Informe a validade do remédio (Ano): ");
        int validade = in.nextInt();
        System.out.println("Informe o lote do remédio: ");
        String lote = in.next();
        System.out.println("Informe o QR Code do remédio: ");
        String qrCode = in.next();
        System.out.println("Informe o criador: ");
        String criador = in.next();
        System.out.println("Informe o doador: ");
        String doador = in.next();

        String sql = "INSERT INTO armazenamento (rem_nome, rem_id, rem_validade, "+
                "rem_lote, rem_qrcode, criador, doador) "+
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, nome);
        ps.setInt(2, remId);
        ps.setInt(3, validade);
        ps.setString(4, lote);
        ps.setString(5, qrCode);
        ps.setString(6, criador);
        ps.setString(7, doador);
        ps.executeUpdate();
        ps.close();
    }

    public static void read(Connection conn) throws SQLException {
        String sql = "SELECT * FROM armazenamento ORDER BY rem_nome";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            int id = rs.getInt("id");
            String nome = rs.getString("rem_nome");
            int remId = rs.getInt("rem_id");
            int validade = rs.getInt("rem_validade");
            String lote = rs.getString("rem_lote");
            String qrCode = rs.getString("rem_qrcode");
            String criador = rs.getString("criador");
            String doador = rs.getString("doador");

            System.out.printf(
                    "[%d] %s (ID: %d) | Validade: %d | Lote: %s | QR: %s | Criador: %s | Doador: %s%n",
                    id, nome, remId, validade, lote, qrCode, criador, doador
            );
        }
        rs.close();
        stmt.close();
    }

    public static void update(Connection conn, Scanner in) throws SQLException {
        // São 7 campos editáveis no total
        String[] sql = new String[7];
        String[] campos = new String[7];
        boolean[] isInt = new boolean[7];

        sql[0] = "UPDATE armazenamento SET rem_nome = ? WHERE id = ?";
        sql[1] = "UPDATE armazenamento SET rem_id = ? WHERE id = ?";
        sql[2] = "UPDATE armazenamento SET rem_validade = ? WHERE id = ?";
        sql[3] = "UPDATE armazenamento SET rem_lote = ? WHERE id = ?";
        sql[4] = "UPDATE armazenamento SET rem_qrcode = ? WHERE id = ?";
        sql[5] = "UPDATE armazenamento SET criador = ? WHERE id = ?";
        sql[6] = "UPDATE armazenamento SET doador = ? WHERE id = ?";

        campos[0] = "Nome do Remédio";
        campos[1] = "ID do Remédio";
        campos[2] = "Validade";
        campos[3] = "Lote";
        campos[4] = "QR Code";
        campos[5] = "Criador";
        campos[6] = "Doador";

        isInt[0] = false;
        isInt[1] = true;  // rem_id é int
        isInt[2] = true;  // rem_validade é int
        isInt[3] = false;
        isInt[4] = false;
        isInt[5] = false;
        isInt[6] = false;

        System.out.print("Informe o ID do registro de armazenamento a ser atualizado: ");
        int id = in.nextInt();

        for (int i = 0; i < sql.length; i++) {
            PreparedStatement ps = conn.prepareStatement(sql[i]);

            int novoInt = 0;
            String novoTexto = "";

            System.out.print("Informe o novo valor de " + campos[i] + ": ");

            if (isInt[i]) {
                novoInt = in.nextInt();
                ps.setInt(1, novoInt);
            } else {
                novoTexto = in.next();
                ps.setString(1, novoTexto);
            }

            ps.setInt(2, id);

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas == 0) {
                System.out.println("Registro de armazenamento não encontrado.");
                ps.close();
                break;
            }

            System.out.println("Campo " + campos[i] + " alterado com sucesso!");
            ps.close();
        }
    }

    public static void delete(Connection conn, Scanner in) throws SQLException {
        String sql = "DELETE FROM armazenamento WHERE id = ?";

        System.out.print("Informe o ID do registro a ser deletado: ");
        int id = in.nextInt();

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);

        int linhasAfetadas = ps.executeUpdate();
        ps.close();

        if (linhasAfetadas > 0) System.out.println("Registro de armazenamento removido!");
        else System.out.println("ID não encontrado.");
    }

    public static String menu(Scanner in) {
        System.out.print(
                "\nCRUD ARMAZENAMENTO"+
                        "\n1 - Listar itens"+
                        "\n2 - Inserir item"+
                        "\n3 - Atualizar itens"+
                        "\n4 - Remover item"+
                        "\n0 - Sair"+
                        "\nOpção: "
        );
        return in.next();
    }

    public static boolean opcao(Connection conn, Scanner in, String op) throws SQLException {
        switch (op) {
            case "1": read(conn); break;
            case "2": create(conn, in); break;
            case "3": update(conn, in); break;
            case "4": delete(conn, in); break;
            case "0": System.out.println("Saindo..."); return true;
            default: System.out.println("Entrada inválida.");
        }
        return false;
    }

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/Farmacia";
        try {
            Connection conn = DriverManager.getConnection(url, "postgres", "666");
            System.out.println("Conexão efetuada com sucesso.");

            createTable(conn);

            Scanner in = new Scanner(System.in);
            boolean sair = false;
            while (!sair) {
                sair = opcao(conn, in, menu(in));
            }
            in.close();
        } catch (SQLException e) {
            System.out.println("Erro ao conectar com o banco: " + e.getMessage());
        }
    }
}