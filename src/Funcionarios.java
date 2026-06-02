import java.sql.*;
import java.util.Scanner;

public class Funcionarios {
    public static void createTable(Connection conn, Scanner in) throws SQLException{
        String sql = "CREATE TABLE IF NOT EXISTS funcionarios (" +
                "id SERIAL PRIMARY KEY, "+
                "funcNome VARCHAR(80) NOT NULL, "+ //muda
                "funcSobrenome VARCHAR(80) NOT NULL, "+  //muda
                "funcLogin VARCHAR(20) NOT NULL, "+ //muda
                "funcSenha VARCHAR(20) NOT NULL,"+ //muda
                "funcNasc INTEGER NOT NULL,"+ //muda
                "funcCodigo INTEGER NOT NULL,"+
                "funcReceita TEXT NOT NULL," //muda
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
        System.out.println("Informe o código do funcionario: ");
        int codigo = in.nextInt();
        System.out.println("Informe a receita do funcionario: ");
        String receita = in.next();

        String sql = "INSERT INTO funcionarios (funcNome, funcSobrenome, funcLogin, "+
                "funcSenha, funcNasc, funcCodigo, funcReceita)"+
                "values (?, ?, ?, ?, ?,?,?)";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, nome);
        ps.setString(2, sobNome);
        ps.setString(3, login);
        ps.setString(4, senha);
        ps.setInt(5, nasc);
        ps.setInt(6, codigo);
        ps.setString(7, receita);
        ps.executeUpdate();
        ps.close();


    }

    public static void read(Connection conn) throws SQLException{
        String sql = "SELECT * FROM funcionarios ORDER BY funcNome";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()){
            int id = rs.getInt("id");
            String nome = rs.getString("funcNome");
            String sobNome = rs.getString("funcSobrenome");
            String login = rs.getString("funcLogin");
            String senha = rs.getString("funcSenha");
            int nasc = rs.getInt("funcNasc");
            int codigo = rs.getInt("funcCodigo");
            String receita = rs.getString("funcReceita");

            System.out.printf(
                    "[%d] %s %s | Login: %s | Senha: %s | Nasc: %d | Código: %d | Receita: %s%n",
                    id, nome, sobNome, login, senha, nasc, codigo, receita
            );
        }

    }

    public static void update(Connection conn, Scanner in) throws SQLException{
        String[] sql = new String[6];
        String[] campos = new String[6];
        boolean[] isInt = new boolean[6];

        sql[0] = "UPDATE funcionarios SET funcNome = ? WHERE id = ?";
        sql[1] = "UPDATE funcionarios SET funcSobrenome = ? WHERE id = ?";
        sql[2] = "UPDATE funcionarios SET funcLogin = ? WHERE id = ?";
        sql[3] = "UPDATE funcionarios SET funcSenha = ? WHERE id = ?";
        sql[4] = "UPDATE funcionarios SET funcNasc = ? WHERE id = ?";
        sql[5] = "UPDATE funcionarios SET funcReceita = ? WHERE id = ?";

        campos[0] = "Nome";
        campos[1] = "Sobrenome";
        campos[2] = "Login";
        campos[3] = "Senha";
        campos[4] = "Nasc";
        campos[5] = "Receita";

        isInt[0] = false;
        isInt[1] = false;
        isInt[2] = false;
        isInt[3] = false;
        isInt[4] = true;
        isInt[5] = false;

        System.out.print("Informe o ID do produto a ser atualizado: ");
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
                System.out.println("Produto não encontrado.");
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

}