import java.sql.*;
import java.util.Scanner;

public class Farmacia {
    public static void createTable(Connection conn) throws SQLException{
        String sql = "CREATE TABLE IF NOT EXISTS farmacia (" +
                "id SERIAL PRIMARY KEY, "+
                "nome_Farmacia VARCHAR(80) NOT NULL, "+
                "localizacao VARCHAR(80) NOT NULL, "+
                "horario_abrir INTEGER NOT NULL,"+
                "horario_fechar INTEGER NOT NULL,"+
                "dias_abertos VARCHAR(150) NOT NULL)";

        Statement stmt = conn.createStatement();
        stmt.execute(sql);
        stmt.close();
    }

    public static void create(Connection conn, Scanner in) throws SQLException{
        System.out.println("Informe o nome da Farmácia: ");
        String Nome = in.next();
        System.out.println("Informe a localização da Farmácia: ");
        String localizacao = in.next();
        System.out.println("Informe o horário da farmácia a ser aberta: ");
        int horario_abrir = in.nextInt();
        System.out.println("Informe o horário da farmácia a ser fechada: ");
        int horario_fechar = in.nextInt();
        System.out.println("Informe os dias que a farmácia será aberta: ");
        String dias_abertos = in.next();

        String sql = "INSERT INTO farmacia (nome_Farmacia, localizacao, horario_abrir, horario_fechar, dias_abertos)" + " VALUES (?, ?, ?, ?, ?)";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, Nome);
        ps.setString(2, localizacao);
        ps.setInt(3, horario_abrir);
        ps.setInt(4, horario_fechar);
        ps.setString(5, dias_abertos);
        ps.executeUpdate();
        ps.close();
    }

    public static void read(Connection conn) throws SQLException{
        String sql = "SELECT * FROM farmacia ORDER BY nome_farmacia";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()){
            int id = rs.getInt("id");
            String nome = rs.getString("nome_farmacia");
            String localizacao = rs.getString("localizacao");
            int horario_abrir = rs.getInt("horario_abrir");
            int horario_fechar = rs.getInt("horario_fechar");
            String dias_abertos = rs.getString("dias_abertos");

            System.out.printf(
                    "[%d] %s | Localizacao: %s | Horário abrir: %d | Horário fechar: %d | Dias: %s%n",
                    id, nome, localizacao, horario_abrir, horario_fechar, dias_abertos
            );
        }
        rs.close();
        stmt.close();
    }

    public static void update(Connection conn, Scanner in) throws SQLException{
        String[] sql = new String[5];
        String[] campos = new String[5];
        boolean[] isInt = new boolean[5];

        sql[0] = "UPDATE farmacia SET nome_Farmacia = ? WHERE id = ?";
        sql[1] = "UPDATE farmacia SET localizacao = ? WHERE id = ?";
        sql[2] = "UPDATE farmacia SET horario_abrir = ? WHERE id = ?";
        sql[3] = "UPDATE farmacia SET horario_fechar = ? WHERE id = ?";
        sql[4] = "UPDATE farmacia SET dias_abertos = ? WHERE id = ?";

        campos[0] = "Nome";
        campos[1] = "Localização";
        campos[2] = "Horários abertos";
        campos[3] = "Horários fechados";
        campos[4] = "Dias";

        isInt[0] = false;
        isInt[1] = false;
        isInt[2] = true;
        isInt[3] = true;
        isInt[4] = false;

        System.out.print("Informe o ID da farmácia a ser atualizado: ");
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
                System.out.println("Farmácia não encontrado.");
                ps.close();
                break;
            }

            System.out.println("Campo " + campos[i] + " alterado com sucesso!");

            ps.close();
        }
    }

    public static void delete(Connection conn, Scanner in) throws SQLException{
        String sql = "DELETE FROM farmacia WHERE id = ?";

        System.out.print("Informe o ID da Farmácia a ser deletada: ");
        int id = in.nextInt();

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);

        int linhasAfetadas = ps.executeUpdate();
        ps.close();

        if(linhasAfetadas > 0) System.out.println("Farmácia removida!");
        else System.out.println("ID não encontrado.");

    }
    public static String menu(Scanner in){
        System.out.print(
                "\nCRUD"+
                        "\n1 - Listar Farmácias"+
                        "\n2 - Inserir Farmácias"+
                        "\n3 - Atualizar Farmácias"+
                        "\n4 - Remover Farmácias"+
                        "\n0 - Sair"+
                        "\nOpção: "
        );
        String resp = in.next();
        return resp;
    }
    public static boolean opcao(Connection conn,Scanner in, String op) throws SQLException{
        switch (op){
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
            System.out.println("Conexão com sucesso.");

            createTable(conn);

            Scanner in = new Scanner(System.in);
            boolean sair = false;
            while(sair == false){
                sair = opcao(conn,in, menu(in));
            }

        }
        catch (SQLException e){
            System.out.println("Erro ao conectar com o banco: " + e.getMessage());
        }
    }
}