package cloud.sect.alm.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class AuthDetector {
    
    // Patterns for login commands
    private static final Pattern[] LOGIN_PATTERNS = {
        Pattern.compile("(?i)/login\\s+<password>"),
        Pattern.compile("(?i)/login\\s+\\[password\\]"),
        Pattern.compile("(?i)Please\\s+login"),
        Pattern.compile("(?i)type\\s+/login"),
        Pattern.compile("(?i)use\\s+/login"),
        Pattern.compile("(?i)เข้าสู่ระบบด้วย\\s+/login"),
        Pattern.compile("(?i)กรุณาพิมพ์\\s+/login"),
        Pattern.compile("(?i)ใช้คำสั่ง\\s+/login")
    };
    
    // Patterns for register commands
    private static final Pattern[] REGISTER_PATTERNS = {
        Pattern.compile("(?i)/register\\s+<password>"),
        Pattern.compile("(?i)/register\\s+\\[password\\]"),
        Pattern.compile("(?i)Please\\s+register"),
        Pattern.compile("(?i)type\\s+/register"),
        Pattern.compile("(?i)use\\s+/register"),
        Pattern.compile("(?i)สมัครสมาชิกด้วย\\s+/register"),
        Pattern.compile("(?i)กรุณาพิมพ์\\s+/register"),
        Pattern.compile("(?i)ใช้คำสั่ง\\s+/register")
    };
    
    // Generic patterns for any command that looks like it's asking for a password
    private static final Pattern GENERIC_AUTH_PATTERN = Pattern.compile("(?i)/(login|l|reg|register|auth)\\s+");

    public enum AuthType {
        NONE,
        LOGIN,
        REGISTER
    }

    public static AuthType detect(String message) {
        String cleanMessage = message.replaceAll("§[0-9a-fk-or]", ""); // Remove color codes
        
        for (Pattern p : REGISTER_PATTERNS) {
            if (p.matcher(cleanMessage).find()) {
                return AuthType.REGISTER;
            }
        }
        
        for (Pattern p : LOGIN_PATTERNS) {
            if (p.matcher(cleanMessage).find()) {
                return AuthType.LOGIN;
            }
        }
        
        // Fallback to simple contains
        String lower = cleanMessage.toLowerCase();
        if (lower.contains("/register") || lower.contains("สมัครสมาชิก")) {
            return AuthType.REGISTER;
        }
        if (lower.contains("/login") || lower.contains("เข้าสู่ระบบ")) {
            return AuthType.LOGIN;
        }
        
        // Last resort: check for command-like patterns
        Matcher m = GENERIC_AUTH_PATTERN.matcher(cleanMessage);
        if (m.find()) {
            String cmd = m.group(1).toLowerCase();
            if (cmd.equals("login") || cmd.equals("l") || cmd.equals("auth")) {
                return AuthType.LOGIN;
            } else if (cmd.equals("reg") || cmd.equals("register")) {
                return AuthType.REGISTER;
            }
        }

        return AuthType.NONE;
    }
}
