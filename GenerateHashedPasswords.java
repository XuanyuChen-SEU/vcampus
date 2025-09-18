import org.mindrot.jbcrypt.BCrypt;

public class GenerateHashedPasswords {
    public static void main(String[] args) {
        String[] passwords = {"7654321", "1111111", "2222221", "8765432", "2222222", 
                            "9876543", "0987654", "1098765", "2109876", "3210987", "4321098"};
        
        System.out.println("生成的BCrypt密码哈希值:");
        System.out.println();
        
        for (String password : passwords) {
            String hashed = BCrypt.hashpw(password, BCrypt.gensalt(10));
            System.out.println("原密码: " + password + " -> 加密后: " + hashed);
        }
    }
}
