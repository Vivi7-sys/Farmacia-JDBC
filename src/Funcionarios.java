import java.sql.*;
import java.util.Scanner;

public class Funcionarios {
    public static void createTable(Connection conn) throws SQLException{
        String sql = "CREATE TABLE IF NOT EXISTS funcionarios (" +
                "id SERIAL PRIMARY KEY, "+
                "func_nome VARCHAR(80) NOT NULL, "+ //muda
                "func_sobrenome VARCHAR(80) NOT NULL, "+  //muda
                "func_login VARCHAR(80) NOT NULL, "+ //muda
                "func_senha VARCHAR(150) NOT NULL,"+ //muda
                "func_nasc INTEGER NOT NULL CHECK (func_nasc BETWEEN 1800 AND 3026),"+ //muda
                "farm_nome TEXT NOT NULL)" // muda
                ;

        Statement stmt = conn.createStatement();
        stmt.execute(sql); //Executa comando sql
        stmt.close(); // fecha instrução
    }

    public static void create(Connection conn, Scanner in) throws SQLException{
        System.out.println("Informe o nome do funcionario: ");
        String nome = in.next();
        System.out.println("Informe o sobrenome do funcionario: ");
        String sobNome = in.next();
        System.out.println("Informe o login do funcionario: ");
        String login = in.next();
        System.out.println("Informe a senha do funcionario: ");
        String senha = in.next();
        System.out.println("Informe a nasc do funcionario: ");
        int nasc = in.nextInt();
        System.out.println("Informe o nome da farmácia em que o funcionário trabalha: ");
        String farmNome = in.next();

        String sql = "INSERT INTO funcionarios (func_nome, func_sobrenome, func_login, "+
                "func_senha, func_nasc, farm_nome)"+
                "values (?, ?, ?, ?, ?,?)";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, nome);
        ps.setString(2, sobNome);
        ps.setString(3, login);
        ps.setString(4, senha);
        ps.setInt(5, nasc);
        ps.setString(6, farmNome);
        ps.executeUpdate();
        ps.close();


    }

    public static void read(Connection conn) throws SQLException{
        String sql = "SELECT * FROM funcionarios ORDER BY func_nome";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()){
            int id = rs.getInt("id");
            String nome = rs.getString("func_nome");
            String sobNome = rs.getString("func_sobrenome");
            String login = rs.getString("func_login");
            String senha = rs.getString("func_senha");
            int nasc = rs.getInt("func_nasc");
            String farmNome = rs.getString("farm_nome");

            System.out.printf(
                    "[%d] %s %s | Login: %s | Senha: %s | Nasc: %d | Receita: %s%n",
                    id, nome, sobNome, login, senha, nasc, farmNome
            );
        }

    }

    public static void update(Connection conn, Scanner in) throws SQLException{
        String[] sql = new String[6];
        String[] campos = new String[6];
        boolean[] isInt = new boolean[6];

        sql[0] = "UPDATE funcionarios SET func_nome = ? WHERE id = ?";
        sql[1] = "UPDATE funcionarios SET func_sobrenome = ? WHERE id = ?";
        sql[2] = "UPDATE funcionarios SET func_login = ? WHERE id = ?";
        sql[3] = "UPDATE funcionarios SET func_senha = ? WHERE id = ?";
        sql[4] = "UPDATE funcionarios SET func_nasc = ? WHERE id = ?";
        sql[5] = "UPDATE funcionarios SET farm_nome = ? WHERE id = ?";

        campos[0] = "Nome";
        campos[1] = "Sobrenome";
        campos[2] = "Login";
        campos[3] = "Senha";
        campos[4] = "Nasc";
        campos[5] = "Nome da Farmácia";

        isInt[0] = false;
        isInt[1] = false;
        isInt[2] = false;
        isInt[3] = false;
        isInt[4] = true;
        isInt[5] = false;

        System.out.print("Informe o ID do funcionario a ser atualizado: ");
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
                System.out.println("Funcionário não encontrado.");
                ps.close();
                break;
            }

            System.out.println("Campo " + campos[i] + " alterado com sucesso!");

            ps.close();
        }
    }

    public static void delete(Connection conn, Scanner in) throws SQLException{
        String sql = "DELETE FROM funcionarios WHERE id = ?";

        System.out.print("Informe o ID do funcionario a ser deletado: ");
        int id = in.nextInt();

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);

        int linhasAfetadas = ps.executeUpdate();
        ps.close();

        if(linhasAfetadas > 0) System.out.println("funcionario removido!");
        else System.out.println("ID não encontrado.");

    }

    public static String menu(Scanner in){
        System.out.print(
                "\nCRUD"+
                        "\n1 - Listar funcionários"+
                        "\n2 - Inserir funcionários"+
                        "\n3 - Atualizar funcionários"+
                        "\n4 - Remover funcionários"+
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
        catch (SQLException e){//caso dê erro, desvia pra cá
            System.out.println("Erro ao conectar com o banco: " + e.getMessage());
        }
    }

}