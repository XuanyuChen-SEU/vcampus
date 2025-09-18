import org.mindrot.jbcrypt.BCrypt;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConvertUserPasswords {
    public static void main(String[] args) {
        try {
            // 读取原始CSV文件
            List<String> lines = Files.readAllLines(Paths.get("../vcampus-database/src/main/resources/db/tb_user.csv"));
            List<String> convertedLines = new ArrayList<>();
            
            // 保留标题行
            if (!lines.isEmpty()) {
                convertedLines.add(lines.get(0));
            }
            
            // 转换密码行
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split(",");
                
                if (parts.length >= 2) {
                    String userId = parts[0];
                    String plainPassword = parts[1];
                    
                    // 加密密码
                    String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
                    
                    // 重新组装行
                    String convertedLine = userId + "," + hashedPassword;
                    convertedLines.add(convertedLine);
                    
                    System.out.println("转换用户: " + userId + " - 原密码: " + plainPassword + " -> 加密完成");
                }
            }
            
            // 写入转换后的文件
            Files.write(Paths.get("../vcampus-database/src/main/resources/db/tb_user_encrypted.csv"), convertedLines);
            System.out.println("密码转换完成！输出文件: tb_user_encrypted.csv");
            System.out.println("共转换了 " + (lines.size() - 1) + " 个用户的密码");
            
        } catch (Exception e) {
            System.err.println("转换过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
